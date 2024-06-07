package project.tdlogistics.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.lang.Collections;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Agency;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.services.AccountService;
import project.tdlogistics.users.services.StaffService;
import project.tdlogistics.users.services.ValidationService;
import project.tdlogistics.users.validations.staffs.CreateByAdmin;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/v2/staffs")
public class StaffRestController {
    @Autowired
    StaffService staffService;

    @Autowired
    AccountService accountService;

    private final ValidationService validationService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    public StaffRestController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/check")
    public ResponseEntity<Response<Staff>> getMethodName(@RequestParam Staff criteria) {
        try {
            final Optional<Staff> optionalStaff = staffService.checkExistStaff(criteria);
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Staff>(false, "Nhân viên không tồn tại", optionalStaff.get()));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<Staff>(false, "Nhân viên đã tồn tại", optionalStaff.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }
    

    @PostMapping("/create")
    public ResponseEntity<Response<Staff>> createNewStaff(@RequestHeader HttpHeaders headers, @Validated(CreateByAdmin.class) @RequestBody Staff payload) throws MethodArgumentNotValidException {
        try {
            // final String role = headers.getFirst("role");
            final String role = "ADMIN";
            if (List.of("ADMIN", "MANAGER", "HUMAN_RESOURCE_MANAGER").contains(role)) {
                validationService.validateRequest(payload, CreateByAdmin.class);
                final Optional<Account> optionalAccount = accountService.findById(payload.getAccount().getId()); 
                if (optionalAccount.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Tài khoản " + payload.getAccount().getId() + " không tồn tại", null));
                }

                final Optional<Staff> optionalStaff = staffService.getStaffByCccd(payload.getCccd());
                if (optionalStaff.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, "Nhân viên có mã cccd " + payload.getCccd() + " đã tồn tại", null));
                }

                if (optionalAccount.get().getRole().equals(Role.SHIPPER) && (payload.getManagedWards() == null || payload.getManagedWards().size() == 0)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Staff>(true, "Trường managedWards không được để trống", null));
                }

                // String jsonRequestGettingAgency = new ObjectMapper().writeValueAsString(new Request<String>("findAgencyById", null, payload.getAgencyId()));
                // String jsonResponseGettingAgency = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.agency", jsonRequestGettingAgency);
                // if (jsonResponseGettingAgency == null) {
                //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Bưu cục/Đại lý " + payload.getAgencyId() + " không tồn tại", null));
                // }

                // Response<Agency> responseGettingAgency = ((new ObjectMapper()).registerModule(new JavaTimeModule())).readValue(jsonResponseGettingAccount, new TypeReference<Response<Agency>>() {});
                    
                // if (responseGettingAgency.getError()) {
                //     return ResponseEntity.status(responseGettingAgency.getStatus()).body(new Response<Staff>(responseGettingAgency.getError(), responseGettingAgency.getMessage(), null));
                // }

                // final Agency agency = responseGettingAgency.getData();
                // if (agency == null) {
                //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Bưu cục/Đại lý " + payload.getAgencyId() + " không tồn tại", null));
                // }
                final Agency agency = new Agency("TD_00000_077165007713", "Thành phố Hồ Chí Minh", "Quận 1", "Phường Phạm Ngũ Lão", List.of("Phường Phạm Ngũ Lão", "Phường Đa Kao"));

                // // String managedWards = Arrays.copyOf()
                // if (account.getRole().equals("SHIPPER")) {
                //     if (agency.getManagedWards().size() == 0) {
                //         return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, "Bưu cục/Đại lý " + payload.getAgencyId() + "chưa quản lý phường/xã/thị trấn nào để để có thể phân vùng cho shipper.", null));
                //     }

                //     for (final String w : payload.getManagedWards()) {
                //         if (!agency.getManagedWards().contains(w)) {
                //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Staff>(true, w + " " + agency.getDistrict() + " " + agency.getProvince() + " không thuộc quyền quản lý của bưu bưu cục " + payload.getAgencyId(), null));
                //         }
                //     }
                // }

                // for (final String w : payload.getManagedWards()) {
                //     String jsonRequestGettingAdministrative = new ObjectMapper().writeValueAsString(new Request<AdministrativeUnit>("findOneAdministrativeUnit", null, new AdministrativeUnit(agency.getProvince(), agency.getDistrict(), w)));
                //     String jsonResponseGettingAdministrative = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrativeUnit", jsonRequestGettingAdministrative);
                //     if (jsonResponseGettingAdministrative == null) {
                //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Tài khoản " + payload.getAccountId() + " không tồn tại", null));
                //     }

                //     Response<AdministrativeUnit> responseGettingAdministrative = ((new ObjectMapper()).registerModule(new JavaTimeModule())).readValue(jsonResponseGettingAccount, new TypeReference<Response<AdministrativeUnit>>() {});
                        
                //     if (responseGettingAdministrative.getError()) {
                //         return ResponseEntity.status(responseGettingAdministrative.getStatus()).body(new Response<Staff>(responseGettingAdministrative.getError(), responseGettingAdministrative.getMessage(), null));
                //     }

                //     final AdministrativeUnit unit = responseGettingAdministrative.getData();
                //     if (unit.getShipper() != null) {
                //         return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, w + " " + agency.getDistrict() + " " + agency.getProvince() + " đã được đảm nhận bởi shipper " + unit.getShipper(), null));
                //     }
                // }

                String[] agencyIdSubParts = payload.getAgencyId().split("_");
                payload.setStaffId(agencyIdSubParts[0] + "_" + agencyIdSubParts[1] + "_" + payload.getCccd());
                payload.setAccount(optionalAccount.get());
                System.out.println(payload.getStaffId());
                staffService.createNewStaff(payload);
                
                // if (account.getRole().equals("SHIPPER")) {
                //     for (final String w : payload.getManagedWards()) {
                //         HashMap<String, Object> updatingUnitCriteria = new HashMap<String, Object>();
                //         updatingUnitCriteria.put("province", agency.getProvince());
                //         updatingUnitCriteria.put("district", agency.getDistrict());
                //         updatingUnitCriteria.put("ward", w);
                //         final AdministrativeUnit newUnit = new AdministrativeUnit();
                //         newUnit.setShipper(payload.getStaffId());
                //         String jsonRequestUpdatingAdministrativeUnit = new ObjectMapper().writeValueAsString(new Request<AdministrativeUnit>("updateOneAdministrativeUnit", updatingUnitCriteria, newUnit));
                //         amqpTemplate.convertSendAndReceive(exchange, "rpc.administrativeUnit", jsonRequestUpdatingAdministrativeUnit);
                //     }
                // }
            }
            payload.getAccount().setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Staff>(false, "Thêm nhân viên thành công", payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Staff>>> getStaffs(@RequestBody Staff criteria) {
        final Role role = Role.AGENCY_MANAGER; // This should be dynamically determined in real scenarios
        final String agencyId = "BC_71000_077204005692";

        Set<Role> adminRoles = Set.of(
            Role.ADMIN, 
            Role.MANAGER, 
            Role.HUMAN_RESOURCE_MANAGER, 
            Role.TELLER, 
            Role.COMPLAINTS_SOLVER
        );

        Set<Role> agencyRoles = Set.of(
            Role.AGENCY_MANAGER,
            Role.AGENCY_HUMAN_RESOURCE_MANAGER
        );

        if (adminRoles.contains(role)) {
            final List<Staff> staffs = staffService.getStaffs(criteria);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Staff>>(false, "Lấy dữ liệu thành công", staffs));
        }

        if (agencyRoles.contains(role)) {
            criteria.setAgencyId(agencyId);
            final List<Staff> staffs = staffService.getStaffs(criteria);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Staff>>(false, "Lấy dữ liệu thành công", staffs));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Staff>>(false, "Lấy dữ liệu thành công", Collections.emptyList()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Staff>> updateStaff(@RequestParam(value = "staffId", required = true) String staffId, @RequestBody Staff info) {
        try {
            final Role role = Role.AGENCY_HUMAN_RESOURCE_MANAGER; // This should be dynamically determined in real scenarios
        final String agencyId = "BC_71000_077204005692";

        final String[] updatorIdSubParts = agencyId.split("_");
        final String[] staffIdSubParts = staffId.split("_");
        if (Set.of(
            Role.AGENCY_MANAGER,
            Role.AGENCY_HUMAN_RESOURCE_MANAGER
        ).contains(role) && (!updatorIdSubParts[0].equals(staffIdSubParts[0]) || !updatorIdSubParts[1].equals(staffIdSubParts[1]))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Nhân viên " + staffId + " không tồn tại trong bưu cục " + agencyId, null));
        }

        final Optional<Staff> optionalStaff = staffService.getStaffById(staffId);
        if (optionalStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Nhân viên không tồn tại", null));
        }

        if (info.getPaidSalary() != null || info.getManagedWards() != null && info.getManagedWards().size() > 0) {
            final Staff staff = optionalStaff.get();
            System.out.println(staff);
            if (Role.SHIPPER != staff.getAccount().getRole() && info.getManagedWards() != null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Response<Staff>(true, "Trường managedWards không được cho phép", null));
            }

            if (Role.SHIPPER == staff.getAccount().getRole() && info.getManagedWards() != null && info.getManagedWards().size() > 0) {
                // String jsonRequestGettingAgency = new ObjectMapper().writeValueAsString(new Request<String>("findAgencyById", null, staff.getAgencyId()));
                // String jsonResponseGettingAgency = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.agency", jsonRequestGettingAgency);
                // if (jsonResponseGettingAgency == null) {
                //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Bưu cục/Đại lý " + staff.getAgencyId() + " không tồn tại", null));
                // }

                // Response<Agency> responseGettingAgency = ((new ObjectMapper()).registerModule(new JavaTimeModule())).readValue(jsonResponseGettingAccount, new TypeReference<Response<Agency>>() {});
                    
                // if (responseGettingAgency.getError()) {
                //     return ResponseEntity.status(responseGettingAgency.getStatus()).body(new Response<Staff>(responseGettingAgency.getError(), responseGettingAgency.getMessage(), null));
                // }

                // final Agency agency = responseGettingAgency.getData();
                // if (agency == null) {
                //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Bưu cục/Đại lý " + staff.getAgencyId() + " không tồn tại", null));
                // }

                final Agency agency = new Agency("BC_71000_077204005691", "Thành phố Hồ Chí Minh", "Quận 1", "Phường Phạm Ngũ Lão", List.of("Phường Phạm Ngũ Lão", "Phường Đa Kao"));
                if (agency.getManagedWards() == null || agency.getManagedWards().size() == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Bưu cục/Đại lý " + agency.getAgencyId() + " chưa quản lý phường/xã/thị trấn nào để có thể phân vùng cho shipper", null));
                }

                for (final String w : agency.getManagedWards()) {
                    if (!agency.getManagedWards().contains(w)) {
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Response<Staff>(true, w + ", " + agency.getDistrict() + ", " + agency.getProvince() + " không thuộc quyền quản lý của bưu cục " + agency.getAgencyId(), null));
                    }
                }

                // for (final String w : staff.getManagedWards()) {
                //     //     String jsonRequestGettingAdministrative = new ObjectMapper().writeValueAsString(new Request<AdministrativeUnit>("findOneAdministrativeUnit", null, new AdministrativeUnit(agency.getProvince(), agency.getDistrict(), w)));
                //     //     String jsonResponseGettingAdministrative = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrativeUnit", jsonRequestGettingAdministrative);
                //     //     if (jsonResponseGettingAdministrative == null) {
                //     //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Tài khoản " + staff.getAccountId() + " không tồn tại", null));
                //     //     }
    
                //     //     Response<AdministrativeUnit> responseGettingAdministrative = ((new ObjectMapper()).registerModule(new JavaTimeModule())).readValue(jsonResponseGettingAccount, new TypeReference<Response<AdministrativeUnit>>() {});
                            
                //     //     if (responseGettingAdministrative.getError()) {
                //     //         return ResponseEntity.status(responseGettingAdministrative.getStatus()).body(new Response<Staff>(responseGettingAdministrative.getError(), responseGettingAdministrative.getMessage(), null));
                //     //     }
    
                //     //     final AdministrativeUnit unit = responseGettingAdministrative.getData();
                //     //     if (unit.getShipper() != null) {
                //     //         return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, w + " " + agency.getDistrict() + " " + agency.getProvince() + " đã được đảm nhận bởi shipper " + unit.getShipper(), null));
                //     //     }
                //     // }
                // }
            }

            if (info.getPaidSalary() != null && info.getPaidSalary() > 0) {
                info.setPaidSalary(info.getPaidSalary() + staff.getPaidSalary());
            }
        }

        final Staff postUpdateStaff = staffService.updateStaffInfo(staffId, info);
        // final Staff postUpdateStaff = new Staff();
        return ResponseEntity.status(HttpStatus.OK).body(new Response<Staff>(false, "Cập nhật thông tin nhân viên thành công", postUpdateStaff));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Staff>> deleteStaff(@RequestParam(value = "staffId", required = true) String staffId) {
        try {
            final Role role = Role.AGENCY_HUMAN_RESOURCE_MANAGER; // This should be dynamically determined in real scenarios
            final String agencyId = "BC_71000_077204005692";

            final String[] updatorIdSubParts = agencyId.split("_");
            final String[] staffIdSubParts = staffId.split("_");
            if (Set.of(
                Role.AGENCY_MANAGER,
                Role.AGENCY_HUMAN_RESOURCE_MANAGER
            ).contains(role) && (!updatorIdSubParts[0].equals(staffIdSubParts[0]) || !updatorIdSubParts[1].equals(staffIdSubParts[1]))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Nhân viên " + staffId + " không tồn tại trong bưu cục " + agencyId, null));
            }

            final Optional<Staff> optionalStaff = staffService.getStaffById(staffId);
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Nhân viên không tồn tại", null));
            }

            staffService.deleteStaff(staffId);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(false, "Xóa nhân viên thành công", null)); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Thông tin không hợp lệ. Lỗi:");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(" [").append(error.getField()).append("] ").append(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Object>(true, errorMessage.toString(), null));
    }
}