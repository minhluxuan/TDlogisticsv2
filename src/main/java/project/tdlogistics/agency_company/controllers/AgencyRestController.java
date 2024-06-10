package project.tdlogistics.agency_company.controllers;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.agency_company.entities.Account;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.AgencyAdmin;
import project.tdlogistics.agency_company.entities.AgencyCompany;
import project.tdlogistics.agency_company.entities.Request;
import project.tdlogistics.agency_company.entities.Response;
import project.tdlogistics.agency_company.entities.Role;
import project.tdlogistics.agency_company.services.AgencyCompanyService;
import project.tdlogistics.agency_company.services.AgencyService;
import project.tdlogistics.agency_company.services.FileUploadService;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v2/agencies")
public class AgencyRestController {
    private static final List<String> agencyCannotBeAffected = List.of("TD_00000_077165007713");

    private final AgencyCompanyService agencycompanyService;
    private final AgencyService agencyService;
    private final FileUploadService fileService;
    private ObjectMapper objectMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    @Autowired
    public AgencyRestController(ObjectMapper objectMapper, AgencyCompanyService agencycompanyService, AgencyService agencyService,
            FileUploadService fileService) {
        this.objectMapper = objectMapper;
        this.agencycompanyService = agencycompanyService;
        this.agencyService = agencyService;
        this.fileService = fileService;
    }

    @GetMapping("/check")
    public ResponseEntity<Response<Agency>> checkExistAgency(@RequestParam(name = "agencyId") String agencyId) {
        try {
            final Optional<Agency> optionalAgency = agencyService.checkExistAgency(agencyId);
            if (optionalAgency.isPresent()) {
                return ResponseEntity.ok(
                        new Response<>(false, String.format("Bưu cục %s đã tồn tại.", agencyId), optionalAgency.get()));
            }
            return ResponseEntity
                    .ok(new Response<>(false, String.format("Bưu cục %s không tồn tại.", agencyId), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Agency>>> getAgencies(@RequestBody Agency agency,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "staffId", required = false) String staffId,
            @RequestHeader(value = "agencyId", required = false) String agencyId,
            @RequestParam(required = false) Integer rows,
            @RequestParam(required = false) Integer page) {
        try {
            role = Role.MANAGER;
            agencyId = "BC_71000_077204005691";
            if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER, Role.AGENCY_COMPLAINTS_SOLVER, Role.SHIPPER).contains(role)) {
                agency.setAgencyId(agencyId);
                Optional<Agency> resultGettingOneAgency = agencyService.getOneAgency(agency);
                if (resultGettingOneAgency.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Agency>>(false, "Lấy thông tin bưu cục thành công", Collections.emptyList()));
                }

                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Agency>>(false, "Lấy thông tin bưu cục thành công", List.of(resultGettingOneAgency.get())));
            }
            else if (List.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Agency>>(false, "Lấy thông tin bưu cục thành thành công", agencyService.getManyAgencies(agency)));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<Agency>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, e.getMessage(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Agency>> createNewAgency(@RequestBody Agency info) {
        try {
            agencyService.checkPostalCode(info.getProvince(), info.getDistrict(), info.getPostalCode());
            agencyService.checkWardsOcupation(info.getProvince(), info.getDistrict(), info.getManagedWards());
            
            // Check exist account with phone number
            final String jsonRequestCheckingExistAccountWithPhoneNumber = objectMapper.writeValueAsString(new Request("checkExistStaffAccount", null, new Account(null, info.getAgencyAdmin().getPhoneNumber(), null)));
            final String jsonResponseCheckingExistAccountWithPhoneNumber = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.users", jsonRequestCheckingExistAccountWithPhoneNumber);
            if (jsonResponseCheckingExistAccountWithPhoneNumber == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            final Response<Account> responseCheckingExistAccountWithPhoneNumber = objectMapper.readValue(jsonResponseCheckingExistAccountWithPhoneNumber, new TypeReference<Response<Account>>() {});
            if (responseCheckingExistAccountWithPhoneNumber.getError()) {
                return ResponseEntity.status(responseCheckingExistAccountWithPhoneNumber.getStatus()).body(new Response<Agency>(true, responseCheckingExistAccountWithPhoneNumber.getMessage(), null));
            }

            if (responseCheckingExistAccountWithPhoneNumber.getData() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Agency>(false, String.format("Tài khoản với số điện thoại %s đã tồn tại", info.getAgencyAdmin().getPhoneNumber()), null));
            }

            // Check exist account with email
            final String jsonRequestCheckingExistAccountWithEmail = objectMapper.writeValueAsString(new Request("checkExistStaffAccount", null, new Account(null, null, info.getAgencyAdmin().getEmail())));
            final String jsonResponseCheckingExistAccountWithEmail = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.users", jsonRequestCheckingExistAccountWithEmail);
            if (jsonResponseCheckingExistAccountWithEmail == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            final Response<Account> responseCheckingExistAccountWithEmail = objectMapper.readValue(jsonResponseCheckingExistAccountWithEmail, new TypeReference<Response<Account>>() {});
            if (responseCheckingExistAccountWithEmail.getError()) {
                return ResponseEntity.status(responseCheckingExistAccountWithEmail.getStatus()).body(new Response<Agency>(true, responseCheckingExistAccountWithEmail.getMessage(), null));
            }

            if (responseCheckingExistAccountWithEmail.getData() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Agency>(false, String.format("Tài khoản với email %s đã tồn tại", info.getAgencyAdmin().getEmail()), null));
            }

            // Check exist staff
            final String jsonRequestCheckingExistStaff = objectMapper.writeValueAsString(new Request("checkExistStaff", null, new AgencyAdmin(info.getAgencyAdmin().getCccd())));
            final String jsonResponseCheckingExistStaff = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.staffs", jsonRequestCheckingExistStaff);
            if (jsonResponseCheckingExistStaff == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            final Response<AgencyAdmin> responseCheckingExistStaff = objectMapper.readValue(jsonResponseCheckingExistStaff, new TypeReference<Response<AgencyAdmin>>() {});
            if (responseCheckingExistStaff.getError()) {
                return ResponseEntity.status(responseCheckingExistStaff.getStatus()).body(new Response<Agency>(true, responseCheckingExistStaff.getMessage(), null));
            }

            if (responseCheckingExistStaff.getData() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Agency>(false, String.format("Nhân viên có mã căn cước %s đã tồn tại", info.getAgencyAdmin().getCccd()), null));
            }
        
            // Check exist company with specific tax code
            if (info.getAgencyCompany() != null) {
                AgencyCompany agencyCompanyTemp = new AgencyCompany();

                if (info.getAgencyCompany().getTaxNumber() != null) {
                    agencyCompanyTemp.setTaxNumber(info.getAgencyCompany().getTaxNumber());
                }

                Optional<AgencyCompany> resultCheckExistAgencyCompany = agencycompanyService.checkExistAgencyCompany(agencyCompanyTemp);
                if (resultCheckExistAgencyCompany.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(false, String.format("Công ty với mã số thuế %s đã tồn tại", info.getAgencyCompany().getTaxNumber()), null));
                }
            }

            // Create random account and check existence
            String defaultUsername = info.getEmail().split("@")[0];
            String username;
            while (true) {
                String randomString = RandomStringUtils.randomNumeric(4);
                username = defaultUsername + randomString;

                final String jsonRequestCheckingExistAccountWithUsername = objectMapper.writeValueAsString(new Request("checkExistStaffAccount", null, new Account(username, null, null)));
                final String jsonResponseCheckingExistAccountWithUsername = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.users", jsonRequestCheckingExistAccountWithUsername);
                if (jsonResponseCheckingExistAccountWithUsername == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
                }

                final Response<Account> responseCheckingExistAccountWithUsername = objectMapper.readValue(jsonResponseCheckingExistAccountWithUsername, new TypeReference<Response<Account>>() {});
                if (responseCheckingExistAccountWithUsername.getError()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
                }

                if (responseCheckingExistAccountWithUsername.getData() == null) {
                    break;
                }
            }
            
            // Create and store account
            final Account agencyAdminAccount = new Account(username, RandomStringUtils.randomAlphanumeric(8), info.getAgencyAdmin().getPhoneNumber(), info.getAgencyAdmin().getPhoneNumber(), Role.AGENCY_MANAGER);
            final String jsonRequestCreatingNewAccount = objectMapper.writeValueAsString(new Request("createNewAccount", null, agencyAdminAccount));
            final String jsonResponseCreatingNewAccount = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.users", jsonRequestCreatingNewAccount);
            if (jsonResponseCreatingNewAccount == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            final Response<Account> responseCreatingNewAccount = objectMapper.readValue(jsonResponseCreatingNewAccount, new TypeReference<Response<Account>>() {});
            if (responseCreatingNewAccount.getError()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            if (responseCreatingNewAccount.getData() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Tạo người dùng thất bại.", null));
            }

            // Create and store staff
            final String agencyId = info.getType() + "_" + info.getPostalCode() + "_" + info.getAgencyAdmin().getCccd();
            info.getAgencyAdmin().setAccount(responseCreatingNewAccount.getData());
            info.getAgencyAdmin().setStaffId(agencyId);
            info.getAgencyAdmin().setAgencyId(agencyId);
            info.getAgencyAdmin().setDeposit(0F);
            info.getAgencyAdmin().setPaidSalary(0F);

            final String jsonRequestCreatingNewStaff = objectMapper.writeValueAsString(new Request("createNewStaff", null, info.getAgencyAdmin()));
            final String jsonResponseCreatingNewStaff = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.staffs", jsonRequestCreatingNewStaff);
            if (jsonResponseCreatingNewStaff == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            final Response<AgencyAdmin> responseCreatingNewStaff = objectMapper.readValue(jsonResponseCreatingNewStaff, new TypeReference<Response<AgencyAdmin>>() {});
            if (responseCreatingNewStaff.getError()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }

            if (responseCreatingNewStaff.getData() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Agency>(true, "Tạo người dùng thất bại.", null));
            }
            
            // Create and store agency
            info.setAgencyId(agencyId);
            info.setRevenue(0F);
            agencyService.createNewAgency(info);

            agencyService.createTablesForAgency(info.getPostalCode());

            agencyService.locateAgencyInArea(info.getProvince(), info.getDistrict(), info.getManagedWards(), info.getPostalCode(), agencyId);

            // Store business license

            // Send account to email

            return ResponseEntity.ok().body(new Response<>(false, "Success", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Agency>> updateAgency(
        @RequestHeader(value = "role", required = false) Role role,
        @RequestParam(name = "agencyId") String agencyId, 
        @RequestBody Agency body) {
        try {
            role = Role.ADMIN;
            if (!List.of(Role.ADMIN, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Agency>(false, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            if (agencyCannotBeAffected.contains(agencyId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Agency>(true, String.format("Bưu cục %s không thể bị tác động", agencyId), null));
            }

            Agency updatedAgency = agencyService.updateAgency(agencyId, body);System.out.println(updatedAgency);
            if (updatedAgency == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Agency>(true, String.format("Bưu cục %s không tồn tại", agencyId), null));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(false, String.format("Cập nhật thông tin bưu cục %s thành công", agencyId), updatedAgency));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Agency>> deleteAgency(@RequestParam(name = "agencyId") String agencyId) {
        try {
            if (agencyCannotBeAffected.contains(agencyId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Agency>(true, "Bưu cục không thể bị tác động", null));
            }

            Optional<Agency> optionalAgency = agencyService.checkExistAgency(agencyId);
            if (optionalAgency.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Agency>(false, String.format("Bưu cục %s không tồn tại", agencyId), null));
            }

            String province = optionalAgency.get().getProvince();
            String district = optionalAgency.get().getDistrict();
            List<String> manageWards = optionalAgency.get().getManagedWards();

            agencyService.deleteAgency(optionalAgency.get().getAgencyId());

            String[] agencyIdSubParts = agencyId.split("_");
            String postalCode = agencyIdSubParts[1];

            agencyService.dropTablesForAgency(postalCode);
            agencyService.locateAgencyInArea(province, district, manageWards, null, null);
            return ResponseEntity.ok().body(new Response<>(false, String.format("Xoá bưu cục/đại lý %s thành công", agencyId), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }
    }

    // @PutMapping("/update_agency_company_license")
    // public ResponseEntity updateLicenseAgencyCompany(@RequestParam(value = "files") MultipartFile[] files,
    //         @RequestParam String agencyId) {

    //     try {
    //         if (files == null || files.length <= 0) {
    //             return ResponseEntity.badRequest().body(new Response<>(true, "Ảnh không được để trống", null));
    //         }
    //         if (agencyCannotBeAffected.contains(agencyId)) {
    //             return ResponseEntity.badRequest().body(new Response<>(true, "Agency không thể bị tác động", null));

    //         }
    //         List<String> licensesImgs = new ArrayList<>();
    //         if (files != null) {
    //             for (MultipartFile file : files) {
    //                 licensesImgs.add(file.getOriginalFilename());
    //                 System.out.println(file.getOriginalFilename());
    //             }
    //         }
    //         AgencyCompany agencyCompany = new AgencyCompany();
    //         agencyCompany.setLicense(licensesImgs);
    //         AgencyCompany resultUpdateAgencyCompany = agencycompanyService.updateAgencyCompany(agencyId, agencyCompany);

    //         Path folderPath = Paths.get("storage", "agency_company", "license", agencyId);
    //         if (fileService.directoryExists(folderPath)) {
    //             fileService.deleteDirectoryIfExists(folderPath);
    //         }

    //         Path tempLicenseFolder = Paths.get("storage", "agency_company", "license_temp");
    //         fileService.createDirectoryIfNotExists(tempLicenseFolder);

    //         Path officialFolderLicense = Paths.get("storage", "agency_company", "license", agencyId);
    //         fileService.createDirectoryIfNotExists(officialFolderLicense);

    //         for (MultipartFile file : files) {
    //             Path tempFileLicensePath = tempLicenseFolder.resolve(file.getOriginalFilename());
    //             if (fileService.directoryExists(tempFileLicensePath)) {
    //                 Path officialLicensePath = officialFolderLicense.resolve(file.getOriginalFilename());
    //                 try {
    //                     Files.move(tempFileLicensePath, officialLicensePath, StandardCopyOption.REPLACE_EXISTING);
    //                 } catch (IOException e) {
    //                     throw new RuntimeException("Error moving file", e);
    //                 }
    //             }
    //         }
    //         if (resultUpdateAgencyCompany == null) {
    //             return ResponseEntity.badRequest().body(new Response<>(true,
    //                     String.format("Bưu cục doanh nghiệp có mã bưu cục %s không tồn tại.`", agencyId), null));

    //         }

    //         return ResponseEntity.ok().body(new Response<>(false, "Cập nhật thành công", null));

    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi xayr ar", null));

    //     }
    // }

    // @GetMapping("/getLicenseAgencyCompany")
    // public ResponseEntity getLicenseAgencyCompany(@RequestParam String agencyId)
    // {
    // try {
    // // Replace this with your actual validation logic
    // // if (error) {
    // // return new ResponseEntity<>(new CustomErrorType(true, error.getMessage()),
    // // HttpStatus.BAD_REQUEST);
    // // }

    // // AgencyCompany resultGettingOneAgencyCompany = agencycompanyService.
    // // (agencyId);
    // if (resultGettingOneAgencyCompany == null) {
    // return ResponseEntity.badRequest()
    // .body(new Response(true, "Bưu cục doanh nghiệp có mã " + agencyId + " không
    // tồn tại.", null));
    // }

    // List<String> licenseImgs = new ArrayList<>();
    // try {
    // licenseImgs = resultGettingOneAgencyCompany.getLicense();
    // } catch (JsonSyntaxException e) {
    // // Ignore the exception and use an empty list
    // }

    // Path folderPath = Paths.get("storage", "agency_company", "license",
    // agencyId);
    // licenseImgs = licenseImgs.stream().map(image ->
    // folderPath.resolve(image).toString())
    // .collect(Collectors.toList());

    // // Replace this with your actual logic to create a zip file
    // // ZipUtils.createZip(licenseImgs, "license.zip");

    // HttpHeaders headers = new HttpHeaders();
    // headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;
    // filename=license.zip");

    // // Replace this with your actual logic to get the zip file as a Resource
    // // Resource file = new FileSystemResource("license.zip");

    // return ResponseEntity.ok().headers(headers).body(file);
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi
    // xayr ar", null));
    // }
    // }

}
