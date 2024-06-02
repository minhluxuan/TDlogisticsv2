package project.tdlogistics.agency_company.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.AgencyCompany;
import project.tdlogistics.agency_company.entities.DTOs.AgencyCreateDTO;
import project.tdlogistics.agency_company.entities.placeholder.Response;
import project.tdlogistics.agency_company.services.AgencyCompanyService;
import project.tdlogistics.agency_company.services.AgencyService;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v2/agencies")
public class AgencyCompanyController {

    private static final Integer MANAGER_ROLE = 1;
    private static final Integer ADMIN_ROLE = 1;

    private final AgencyCompanyService agencycompanyService;
    private final AgencyService agencyService;

    @Autowired
    public AgencyCompanyController(AgencyCompanyService agencycompanyService, AgencyService agencyService) {
        this.agencycompanyService = agencycompanyService;
        this.agencyService = agencyService;
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

    public ResponseEntity createNewAgency(@RequestBody AgencyCreateDTO body) {

        // validate body
        try {

            Agency agency_body = Agency.fromDTO(body);

            boolean checkPostalCode = agencyService.checkPostalCode(agency_body.getProvince(),
                    agency_body.getDistrict(),
                    agency_body.getPostal_code());

            if (!checkPostalCode) {
                return ResponseEntity.badRequest().body(new Response<>(true, "Lỗi hệ thống", null));
            }

            boolean checkWardsOcupation = agencyService.checkWardsOcupation(agency_body.getProvince(),
                    agency_body.getDistrict(), agency_body.getManaged_wards());

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

            if (agency_body.isIndividual_company()) {

                AgencyCompany tempAgencyCompany = new AgencyCompany();

                if (agency_body.getCompany_name() != null) {
                    tempAgencyCompany.setCompany_name(agency_body.getCompany_name());
                }

                if (agency_body.getTax_number() != null) {
                    tempAgencyCompany.setTax_number(agency_body.getTax_number());
                }
                // validate the company

                Optional<AgencyCompany> tempCom = agencycompanyService.checkExistAgencyCompany(tempAgencyCompany);
                if (tempCom.isPresent()) {
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

            agency_body.setAgency_id(agencyId);

            Agency createdAgency = agencyService.createNewAgency(agency_body);

            String textResultCreatingAgency = "";

            if (createdAgency == null) {
                return ResponseEntity.badRequest().body(new Response<>(true, "Đã có lỗi xảy ra", null));
            }

            List<String> tableList = agencyService.createTablesForAgency(agency_body.getPostal_code());
            String textResultCreatingTables = "";
            if (tableList.size() < 5) {
                textResultCreatingTables = "Taọ bảng thấy bại";
            } else {
                textResultCreatingTables = "Tạo thành công";
            }

            return ResponseEntity.ok().body(new Response<>(false, "Tạo Agency thành công", createdAgency));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(true, e.getMessage(), null));
        }

    }
}