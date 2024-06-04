package project.tdlogistics.agency_company.controllers;

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
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.AgencyCompany;
import project.tdlogistics.agency_company.entities.DTOs.AgencyCreateDTO;
import project.tdlogistics.agency_company.entities.placeholder.Response;
import project.tdlogistics.agency_company.services.AgencyCompanyService;
import project.tdlogistics.agency_company.services.AgencyService;
import project.tdlogistics.agency_company.services.FileUploadService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v2/agencies")
public class AgencyController {

    private static final Integer MANAGER_ROLE = 1;
    private static final Integer ADMIN_ROLE = 1;
    private static final List<String> agencyCannotBeAffected = List.of("TD_00000_077165007713");

    private final AgencyCompanyService agencycompanyService;
    private final AgencyService agencyService;
    private final FileUploadService fileService;

    @Autowired
    public AgencyController(AgencyCompanyService agencycompanyService, AgencyService agencyService,
            FileUploadService fileService) {
        this.agencycompanyService = agencycompanyService;
        this.agencyService = agencyService;
        this.fileService = fileService;
    }

    @GetMapping("/check")
    public ResponseEntity<Response> checkAgency(@RequestParam String agency_id) {
        try {
            final Optional<Agency> exited = agencyService.checkExistAgency(agency_id);
            if (exited.isPresent()) {
                return ResponseEntity.ok(
                        new Response<>(false, String.format("Bưu cục có mã bưu cục %s đã tồn tại.", agency_id), null));
            }
            return ResponseEntity
                    .ok(new Response<>(false, "Bưu cục có mã bưu cục " + agency_id + " chưa tồn tại.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Đã có lỗi xảy ra", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity getAgencies(@RequestBody Agency agency,
            @RequestHeader(value = "user", required = false) String user,
            @RequestParam(required = false) Integer rows,
            @RequestParam(required = false) Integer page) {
        System.out.println("here1");
        try {
            if (MANAGER_ROLE == 0) { // for manageer
                // skip validation
                agency.setAgency_id(user);
                Optional<Agency> result = agencyService.getOneAgency(agency);
                if (result.isEmpty()) {
                    return ResponseEntity.status(404).body(new Response<>(false,
                            String.format("Lấy thông tin bưu cục cho user: %s không thành công", user), null));
                }
                Agency return_agency = result.get();
                // manageward is defined in the configuration
                // get company, implemeted in entity

                if (return_agency.isIndividual_company()) {

                    AgencyCompany temp = new AgencyCompany();
                    temp.setAgency_id(return_agency.getAgency_id());

                    Optional<AgencyCompany> agencyCompany = agencycompanyService.checkExistAgencyCompany(temp);

                    if (agencyCompany.isPresent()) {

                        AgencyCompany agencyCompResult = agencyCompany.get();

                        return_agency.setCompany_name(agencyCompResult.getCompany_name());
                        return_agency.setTax_number(agencyCompResult.getTax_number());
                        return_agency.setLicense(agencyCompResult.getLicense());

                    }
                }

                return ResponseEntity.ok(new Response<>(false, "oke", return_agency));

            }
            if (ADMIN_ROLE == 1) {

                int defaultRows = 0; // default value for rows
                int defaultPage = 10; // default value for page

                // int finalRows = (rows != null) ? rows : defaultRows;
                // int finalPage = (page != null) ? page : defaultPage;

                List<Agency> result = agencyService.getManyAgencies(agency, null, null);

                if (result == null) {
                    return ResponseEntity.internalServerError().body(new Response<>(true, "Đã có lỗi xảy ra", null));
                }

                for (Agency return_agency : result) {

                    if (return_agency.isIndividual_company()) {

                        AgencyCompany temp = new AgencyCompany();
                        temp.setAgency_id(return_agency.getAgency_id());

                        Optional<AgencyCompany> agencyCompany = agencycompanyService.checkExistAgencyCompany(temp);

                        if (agencyCompany.isPresent()) {

                            AgencyCompany agencyCompResult = agencyCompany.get();

                            return_agency.setCompany_name(agencyCompResult.getCompany_name());
                            return_agency.setTax_number(agencyCompResult.getTax_number());
                            return_agency.setLicense(agencyCompResult.getLicense());

                        }
                    }

                }

                return ResponseEntity.ok(new Response<>(false, "oke", result));

                // validate pagination
                // validate body
                // const { error } = agencyValidation.validateFindingAgencyByAdmin(req.body);
                // if (error) {
                // return res.status(400).json({
                // error: true,
                // message: error.message,
                // });

            }

            return ResponseEntity.badRequest().body(new Response<>(true, "Đã có lỗi xảy ra", null));

            // Optional<Agency> result = agencyService.checkExistAgency(agency);
            // // Optional<Agency> result1 = agencyService.checkExistAgency();
            // System.out.println(result);
            // // System.out.println(result1);
            // return ResponseEntity.ok(new Response<>(true, "get thanh cong", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, e.getMessage(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity createNewAgency(@RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestBody AgencyCreateDTO body) {

        // validate body
        try {

            Agency agencyTemp = Agency.fromDTO(body);

            boolean checkPostalCode = agencyService.checkPostalCode(agencyTemp.getProvince(),
                    agencyTemp.getDistrict(),
                    agencyTemp.getPostal_code());

            if (!checkPostalCode) {
                return ResponseEntity.badRequest().body(new Response<>(true, "Lỗi hệ thống", null));
            }

            boolean checkWardsOcupation = agencyService.checkWardsOcupation(agencyTemp.getProvince(),
                    agencyTemp.getDistrict(), agencyTemp.getManaged_wards());

            if (!checkWardsOcupation) {
                return ResponseEntity.badRequest().body(new Response<>(true, "Lỗi hệ thống", null));
            }

            /*
             * const resultCheckingExistStaff = await
             * staffsService.checkExistStaff(tempUser);
             *
             * if (resultCheckingExistStaff.existed) {
             * return res.status(409).json({
             * error: true,
             * message: resultCheckingExistStaff.message,
             * });
             * }
             */

            if (agencyTemp.isIndividual_company()) {

                AgencyCompany agencyCompanyTemp = new AgencyCompany();

                if (agencyTemp.getCompany_name() != null) {
                    agencyCompanyTemp.setCompany_name(agencyTemp.getCompany_name());
                }

                if (agencyTemp.getTax_number() != null) {
                    agencyCompanyTemp.setTax_number(agencyTemp.getTax_number());
                }
                // validate the company

                Optional<AgencyCompany> resultCheckExistCompany = agencycompanyService
                        .checkExistAgencyCompany(agencyCompanyTemp);
                if (resultCheckExistCompany.isPresent()) {
                    return ResponseEntity.badRequest().body(new Response<>(false, "Company đã tồn tại", null));
                }

            }

            final String agencyId = body.getType() + body.getPostal_code() + body.getUser_cccd();

            String defaultUsername = body.getEmail().split("@")[0];
            String username;
            while (true) {
                String randomString = RandomStringUtils.randomNumeric(4);
                username = defaultUsername + randomString;

                // if (!staffsService.checkExistStaff(username)) {
                // break;
                // }
                break;
            }

            String password = RandomStringUtils.randomAlphanumeric(8);
            // String hashedPassword = utils.hash(password);
            String hashedPassword = password;

            // create staff herre

            agencyTemp.setAgency_id(agencyId);

            Agency resultCreateAgency = agencyService.createNewAgency(agencyTemp);

            String textResultCreatingAgency = "";

            if (resultCreateAgency == null) {
                textResultCreatingAgency = String
                        .format("Tạo bưu cục có mã bưu cục %s trong cơ sở dữ liệu tổng thất bại.", agencyId);
            } else {
                textResultCreatingAgency = String
                        .format("Tạo bưu cục có mã bưu cục %s trong cơ sở dữ liệu tổng thành công.", agencyId);

            }

            String textResultCreatingStaff = "";
            // create staff herere

            String textResultCreateTable = agencyService.createTablesForAgency(agencyTemp.getPostal_code());

            Response resultLocateAgencyInArea = agencyService.locateAgencyInArea(0, agencyTemp.getProvince(),
                    agencyTemp.getDistrict(), agencyTemp.getManaged_wards(), agencyId, agencyTemp.getPostal_code());

            String textResultLocateAgencyInArea = resultLocateAgencyInArea.getMessage();

            // If (individual company) (ok)
            // file res (ok)
            if (body.getIndividual_company() == true) {
                List<String> licenseImgs = new ArrayList<>();
                if (files != null && files.length > 0) {
                    for (MultipartFile file : files) {
                        licenseImgs.add(file.getOriginalFilename());
                    }

                }

                AgencyCompany newAgencyCompany = new AgencyCompany();
                newAgencyCompany.setAgency_id(agencyId);
                newAgencyCompany.setCompany_name(body.getCompany_name());
                newAgencyCompany.setTax_number(body.getTax_number());
                newAgencyCompany.setLicense(licenseImgs);

                AgencyCompany resultCreateAgencyCompany = agencycompanyService.createNewAgencyCompany(newAgencyCompany);

                String textResultCreatingNewAgencyCompany = "";
                if (resultCreateAgencyCompany != null) {
                    textResultCreatingNewAgencyCompany = String.format(
                            "Tạo bưu cục doanh nghiệp có mã bưu cục %s trong cơ sở dữ liệu agency_company thất bại.",
                            agencyId);
                } else {
                    textResultCreatingNewAgencyCompany = String.format(
                            "Tạo bưu cục doanh nghiệp có mã bưu cục %s trong cơ sở dữ liệu agency_company thành công",
                            agencyId);
                }

                // need adjustment here
                if (files != null && files.length >= 0) {
                    for (MultipartFile file : files) {
                        fileService.save(file);
                    }

                }

                // call mail service HERE
                String message = String.format("Kết quả:\n%s\n%s\n%s\n%s\n%s",
                        textResultCreatingStaff,
                        textResultCreatingAgency,
                        textResultCreateTable,
                        textResultLocateAgencyInArea,
                        textResultCreatingNewAgencyCompany);

                return ResponseEntity.ok().body(new Response<>(false, message, null));
            }
            // call mail servive here
            String message = String.format("Kết quả:\n%s\n%s\n%s\n%s\n%s",
                    textResultCreatingStaff,
                    textResultCreatingAgency,
                    textResultCreateTable,
                    textResultLocateAgencyInArea);

            return ResponseEntity.ok().body(new Response<>(false, message, null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }

    }

    @PutMapping("/update")
    public ResponseEntity updateAgency(@RequestParam String agencyId, @RequestBody Agency body) {

        try {

            // validate

            if (agencyCannotBeAffected.contains(agencyId)) {
                return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi xayr ar", null));

            }
            boolean resultUpdateAgency = agencyService.updateAgency(agencyId, body);
            if (!resultUpdateAgency) {
                return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi xayr ar", null));
            }

            return ResponseEntity.ok().body(new Response<>(false,
                    String.format("Cập nhật thông tin bưu cục có mã bưu cục %s thành công", agencyId), null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteAgency(@RequestParam String agencyId, @RequestBody Agency body) {
        // valudate id
        try {

            if (agencyCannotBeAffected.contains(agencyId)) {
                return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi xayr ar", null));

            }

            Optional<Agency> resultFindOneAgency = agencyService.checkExistAgency(agencyId);
            if (resultFindOneAgency.isEmpty()) {
                return ResponseEntity.ok().body(new Response<>(false,
                        String.format("Bưu cục không tồn tại", agencyId), null));
            }
            // String agencyId = resultFindOneAgency.get().getAgency_id();
            String province = resultFindOneAgency.get().getProvince();
            String district = resultFindOneAgency.get().getDistrict();
            List<String> manageWards = resultFindOneAgency.get().getManaged_wards();

            Response resultDeleteOneAgency = agencyService.deleteAgency(resultFindOneAgency.get());

            String textResultDeletingAgency;
            if (!resultDeleteOneAgency.getError()) {
                textResultDeletingAgency = String.format("Xóa bưu cục có mã bưu cục %s thất bại.", agencyId);
            } else {
                textResultDeletingAgency = String.format("Xóa bưu cục có mã bưu cục %s thành công.", agencyId);
            }
            String[] agencyIdSubParts = agencyId.split("_");
            String postalCode = agencyIdSubParts[1];

            Response resultDropTableForAgency = agencyService.dropTablesForAgency(postalCode);
            String textResultDropTableForAgency = resultDropTableForAgency.getMessage();

            Response resultLocateAgencyInArea = agencyService.locateAgencyInArea(1, province,
                    district, manageWards, agencyId, postalCode);
            String textResultLocateAgencyInArea = resultLocateAgencyInArea.getMessage();

            String message = String.format("Kết quả:\n%s\n%s\n%s",
                    textResultDeletingAgency,
                    textResultDropTableForAgency,
                    textResultLocateAgencyInArea);
            return ResponseEntity.ok().body(new Response<>(false, message, null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));

        }

    }

    @GetMapping("/get_managed_wards")
    public ResponseEntity getManagedWards(@RequestParam String agencyId) {
        // validateion
        try {
            // valdate agency_id
            return ResponseEntity.ok()
                    .body(new Response<>(false, "Lấy thông tin các phường/xã/thị trấn được đảm nhận thành công.",
                            agencyService.getManagedWards(agencyId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }
    }

    @GetMapping("/test")
    public ResponseEntity testUnit() {
        try {
            String postalCode = "72000";
            // Response result = agencyService.dropTablesForAgency(postalCode);
            List<String> wards = List.of("Xã Trừ Văn Thố");
            Response result2 = agencyService.locateAgencyInArea(0, "Tỉnh Bình Dương", "Huyện Bàu Bàng", wards,
                    "TD_00000_077165007714", postalCode);

            return ResponseEntity.ok().body(new Response<>(false, result2.getMessage(),
                    null));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response<>(true, e.getMessage(), null));
        }
    }

    @PutMapping("/update_agency_company_license")
    public ResponseEntity updateLicenseAgencyCompany(@RequestParam(value = "files") MultipartFile[] files,
            @RequestParam String agencyId) {

        try {
            if (files == null || files.length <= 0) {
                return ResponseEntity.badRequest().body(new Response<>(true, "Ảnh không được để trống", null));
            }
            if (agencyCannotBeAffected.contains(agencyId)) {
                return ResponseEntity.badRequest().body(new Response<>(true, "Agency không thể bị tác động", null));

            }
            List<String> licensesImgs = new ArrayList<>();
            if (files != null) {
                for (MultipartFile file : files) {
                    licensesImgs.add(file.getOriginalFilename());
                    System.out.println(file.getOriginalFilename());
                }
            }
            AgencyCompany agencyCompany = new AgencyCompany();
            agencyCompany.setLicense(licensesImgs);
            AgencyCompany resultUpdateAgencyCompany = agencycompanyService.updateAgencyCompany(agencyId, agencyCompany);

            Path folderPath = Paths.get("storage", "agency_company", "license", agencyId);
            if (fileService.directoryExists(folderPath)) {
                fileService.deleteDirectoryIfExists(folderPath);
            }

            Path tempLicenseFolder = Paths.get("storage", "agency_company", "license_temp");
            fileService.createDirectoryIfNotExists(tempLicenseFolder);

            Path officialFolderLicense = Paths.get("storage", "agency_company", "license", agencyId);
            fileService.createDirectoryIfNotExists(officialFolderLicense);

            for (MultipartFile file : files) {
                Path tempFileLicensePath = tempLicenseFolder.resolve(file.getOriginalFilename());
                if (fileService.directoryExists(tempFileLicensePath)) {
                    Path officialLicensePath = officialFolderLicense.resolve(file.getOriginalFilename());
                    try {
                        Files.move(tempFileLicensePath, officialLicensePath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException("Error moving file", e);
                    }
                }
            }
            if (resultUpdateAgencyCompany == null) {
                return ResponseEntity.badRequest().body(new Response<>(true,
                        String.format("Bưu cục doanh nghiệp có mã bưu cục %s không tồn tại.`", agencyId), null));

            }

            return ResponseEntity.ok().body(new Response<>(false, "Cập nhật thành công", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi xayr ar", null));

        }
    }

    @PutMapping("/update_agency_company")
    public ResponseEntity updateAgencyCompany(@RequestParam String agencyId, @RequestBody AgencyCompany body) {
        // validate
        if (agencyCannotBeAffected.contains(agencyId)) {
            return ResponseEntity.badRequest().body(new Response<>(true, "ddax co loi xayr ar", null));

        }
        AgencyCompany resultUpdateAgencyCompany = agencycompanyService.updateAgencyCompany(agencyId, body);

        if (resultUpdateAgencyCompany == null) {
            return ResponseEntity.badRequest().body(new Response<>(true,
                    String.format("Bưu cục doanh nghiệp có mã bưu cục %s không tồn tại.`", agencyId), null));

        }
        return ResponseEntity.ok().body(new Response<>(false, "Cập nhật thành công", null));

    }

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
