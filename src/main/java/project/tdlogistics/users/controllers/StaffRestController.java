package project.tdlogistics.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.lang.Collections;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Agency;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.entities.UnitRequest;
import project.tdlogistics.users.services.AccountService;
import project.tdlogistics.users.services.AdministrativeService;
import project.tdlogistics.users.services.AgencyService;
import project.tdlogistics.users.services.FilesService;
import project.tdlogistics.users.services.StaffService;
import project.tdlogistics.users.services.ValidationService;
import project.tdlogistics.users.validations.staffs.CreateByAdmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/v2/staffs")
public class StaffRestController {
    @Autowired
    private StaffService staffService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private AdministrativeService administrativeService;

    @Autowired
    private FilesService filesService;

    @Autowired
    private ValidationService validationService;

    @PostMapping("/check")
    public ResponseEntity<Response<Staff>> checkExistCustomer(@RequestParam Staff criteria) {
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
    
    @GetMapping("/")
    public ResponseEntity<Response<Staff>> getAuthenticatedStaffInfo(@RequestHeader(name = "userId") String staffId) {
        try {
            final Optional<Staff> optionalStaff = staffService.getStaffById(staffId);
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Staff>(false, String.format("Nhân viên %s không tồn tại", staffId), optionalStaff.get()));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<Staff>(false, "Lấy thông tin nhân viên thành công", optionalStaff.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Staff>> createNewStaff(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String creatorId,
        @RequestHeader(name = "agencyId") String agencyId,
        @Validated(CreateByAdmin.class) @RequestBody Staff payload
    ) throws MethodArgumentNotValidException {
        try {
            if (List.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                validationService.validateRequest(payload, CreateByAdmin.class);

                // Check account existence
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

                // Check agency existence
                Agency tempAgency = new Agency();
                tempAgency.setAgencyId(payload.getAgencyId());
                final Agency agency = agencyService.checkExistAgency(tempAgency);
                if (agency == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Bưu cục/Đại lý %s không tồn tại", payload.getAgencyId()), null));
                }

                // Handle managed wards
                if (optionalAccount.get().getRole().equals(Role.SHIPPER)) {
                    if (agency.getManagedWards().size() == 0) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, "Bưu cục/Đại lý " + payload.getAgencyId() + "chưa quản lý phường/xã/thị trấn nào để để có thể phân vùng cho shipper.", null));
                    }

                    for (final String w : payload.getManagedWards()) {
                        if (!agency.getManagedWards().contains(w)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Staff>(true, w + " " + agency.getDistrict() + " " + agency.getProvince() + " không thuộc quyền quản lý của bưu bưu cục " + payload.getAgencyId(), null));
                        }
                    }

                    for (final String w : payload.getManagedWards()) {
                        final UnitRequest unit = administrativeService.checkExistWard(new UnitRequest(agency.getProvince(), agency.getDistrict(), w));
                        if (unit == null) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("%s, %s, %s không tồn tại", w, agency.getDistrict(), agency.getProvince()), null));
                        }

                        if (unit.getShipper() != null) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, String.format("%s, %s, %s đã được đảm nhận bởi shipper %s", w, agency.getDistrict(), agency.getProvince(), unit.getShipper()), null));
                        }
                    }
                }

                String[] agencyIdSubParts = payload.getAgencyId().split("_");
                payload.setStaffId(agencyIdSubParts[0] + "_" + agencyIdSubParts[1] + "_" + payload.getCccd());
                payload.setAccount(optionalAccount.get());
                final Staff newStaff = staffService.createNewStaff(payload);
                
                if (optionalAccount.get().getRole().equals(Role.SHIPPER)) {
                    System.out.println(true);
                    for (final String w : payload.getManagedWards()) {
                        HashMap<String, Object> updatingUnitCriteria = new HashMap<String, Object>();
                        updatingUnitCriteria.put("province", agency.getProvince());
                        updatingUnitCriteria.put("district", agency.getDistrict());
                        updatingUnitCriteria.put("ward", w);
                        final UnitRequest newUnit = new UnitRequest();
                        newUnit.setShipper(payload.getStaffId());
                        administrativeService.updateAdministrativeUnit(updatingUnitCriteria, newUnit);
                    }
                }
                
                return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Staff>(false, "Thêm nhân viên thành công", newStaff));
            }

            if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                validationService.validateRequest(payload, CreateByAdmin.class);

                // Check account existence
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

                // Check agency existence
                Agency tempAgency = new Agency();
                tempAgency.setAgencyId(payload.getAgencyId());
                final Agency agency = agencyService.checkExistAgency(tempAgency);
                if (agency == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Bưu cục/Đại lý %s không tồn tại", payload.getAgencyId()), null));
                }

                // Handle managed wards
                if (optionalAccount.get().getRole().equals(Role.SHIPPER)) {
                    if (agency.getManagedWards().size() == 0) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, "Bưu cục/Đại lý " + payload.getAgencyId() + "chưa quản lý phường/xã/thị trấn nào để để có thể phân vùng cho shipper.", null));
                    }

                    for (final String w : payload.getManagedWards()) {
                        if (!agency.getManagedWards().contains(w)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Staff>(true, w + " " + agency.getDistrict() + " " + agency.getProvince() + " không thuộc quyền quản lý của bưu bưu cục " + payload.getAgencyId(), null));
                        }
                    }

                    for (final String w : payload.getManagedWards()) {
                        final UnitRequest unit = administrativeService.checkExistWard(new UnitRequest(agency.getProvince(), agency.getDistrict(), w));
                        if (unit == null) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("%s, %s, %s không tồn tại", w, agency.getDistrict(), agency.getProvince()), null));
                        }
                        System.out.println("SHIPPER");
                        System.out.println(unit.getShipper());
                        if (unit.getShipper() != null) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, String.format("%s, %s, %s đã được đảm nhận bởi shipper %s", w, agency.getDistrict(), agency.getProvince(), unit.getShipper()), null));
                        }
                    }
                }

                String[] agencyIdSubParts = agencyId.split("_");
                payload.setStaffId(agencyIdSubParts[0] + "_" + agencyIdSubParts[1] + "_" + payload.getCccd());
                payload.setAccount(optionalAccount.get());
                final Staff newStaff = staffService.createNewStaff(payload);
                
                if (optionalAccount.get().getRole().equals(Role.SHIPPER)) {
                    System.out.println(true);
                    for (final String w : payload.getManagedWards()) {
                        HashMap<String, Object> updatingUnitCriteria = new HashMap<String, Object>();
                        updatingUnitCriteria.put("province", agency.getProvince());
                        updatingUnitCriteria.put("district", agency.getDistrict());
                        updatingUnitCriteria.put("ward", w);
                        final UnitRequest newUnit = new UnitRequest();
                        newUnit.setShipper(payload.getStaffId());
                        administrativeService.updateAdministrativeUnit(updatingUnitCriteria, newUnit);
                    }
                }
                
                return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Staff>(false, "Thêm nhân viên thành công", newStaff));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Staff>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Staff>>> getStaffs(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String staffId,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestBody Staff criteria
    ) {
        if (Set.of(
            Role.ADMIN, 
            Role.MANAGER, 
            Role.HUMAN_RESOURCE_MANAGER, 
            Role.TELLER, 
            Role.COMPLAINTS_SOLVER
        ).contains(role)) {
            final List<Staff> staffs = staffService.getStaffs(criteria);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Staff>>(false, "Lấy dữ liệu thành công", staffs));
        }

        if (Set.of(
            Role.AGENCY_MANAGER,
            Role.AGENCY_HUMAN_RESOURCE_MANAGER
        ).contains(role)) {
            criteria.setAgencyId(agencyId);
            final List<Staff> staffs = staffService.getStaffs(criteria);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Staff>>(false, "Lấy dữ liệu thành công", staffs));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Staff>>(false, "Lấy dữ liệu thành công", Collections.emptyList()));
    }

    @SuppressWarnings("unlikely-arg-type")
    @PutMapping("/update")
    public ResponseEntity<Response<Staff>> updateStaff(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestParam(value = "staffId", required = true) String staffId,
        @RequestBody Staff payload
    ) {
        try {
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

            final Staff staff = optionalStaff.get();
            if (payload.getManagedWards() != null) {
                if (Role.SHIPPER != staff.getAccount().getRole() && payload.getManagedWards() != null) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Response<Staff>(true, "Trường managedWards không được cho phép", null));
                }

                if (Role.SHIPPER == staff.getAccount().getRole() && payload.getManagedWards() != null) {
                    Agency tempAgency = new Agency();
                    tempAgency.setAgencyId(staff.getAgencyId());
                    final Agency agency = agencyService.checkExistAgency(tempAgency);
                    if (agency == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Bưu cục/Đại lý %s không tồn tại", payload.getAgencyId()), null));
                    }

                    if (agency.getManagedWards() == null || agency.getManagedWards().size() == 0) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, "Bưu cục/Đại lý " + agency.getAgencyId() + " chưa quản lý phường/xã/thị trấn nào để có thể phân vùng cho shipper", null));
                    }

                    for (final String w : agency.getManagedWards()) {
                        if (!agency.getManagedWards().contains(w)) {
                            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Response<Staff>(true, w + ", " + agency.getDistrict() + ", " + agency.getProvince() + " không thuộc quyền quản lý của bưu cục " + agency.getAgencyId(), null));
                        }
                    }

                    for (final String w : payload.getManagedWards()) {
                        // Get ward to check occupation
                        final UnitRequest unit = administrativeService.checkExistWard(new UnitRequest(agency.getProvince(), agency.getDistrict(), w));
                        if (unit.getShipper() != null && !unit.getShipper().equals(staffId)) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Staff>(true, String.format("%s, %s, %s đã được đảm nhận bởi shipper %s", w, agency.getDistrict(), agency.getProvince(), unit.getShipper()), null));
                        }
                    }

                    // Get managed wards of shipper before
                    final UnitRequest tempUnit = new UnitRequest();
                    tempUnit.setShipper(staffId);
                    List<UnitRequest> units = administrativeService.findWards(tempUnit);
                    final List<UnitRequest> complementWards = new ArrayList<UnitRequest>(units);
                    complementWards.remove(payload.getManagedWards());

                    HashMap<String, Object> updatingUnitCriteria = new HashMap<String, Object>();
                    updatingUnitCriteria.put("province", agency.getProvince());
                    updatingUnitCriteria.put("district", agency.getDistrict());
                    // Update ward
                    for (final String w : payload.getManagedWards()) {
                        updatingUnitCriteria.put("ward", w);
                        administrativeService.updateAdministrativeUnit(updatingUnitCriteria, new UnitRequest(agency.getProvince(), agency.getDistrict(), w, staffIdSubParts[1], agency.getAgencyId(), staffId));
                    }

                    updatingUnitCriteria.put("allowShipperNull", true);
                    for (final UnitRequest u : complementWards) {
                        updatingUnitCriteria.put("ward", u.getWard());
                        administrativeService.updateAdministrativeUnit(updatingUnitCriteria, new UnitRequest(agency.getProvince(), agency.getDistrict(), u.getWard(), staffIdSubParts[1], agency.getAgencyId(), null));
                    }
                }
            }

            if (payload.getPaidSalary() != null && payload.getPaidSalary() > 0) {
                payload.setPaidSalary(payload.getPaidSalary() + (staff.getPaidSalary() == null ? 0 : staff.getPaidSalary()));
            }

            final Staff postUpdateStaff = staffService.updateStaffInfo(staffId, payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Staff>(false, "Cập nhật thông tin nhân viên thành công", postUpdateStaff));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PutMapping("/avatar/update")
    public ResponseEntity<Response<Staff>> updateAvatar(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestParam(name = "staffId") String staffId,
        @RequestParam("avatar") MultipartFile file
    ) {
        role = Role.ADMIN;
        if (!List.of(Role.ADMIN, Role.AGENCY_MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Staff>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            final String[] updatorIdSubParts = userId.split("_");
            final String[] staffIdSubParts = staffId.split("_");
            if (updatorIdSubParts[0] != staffIdSubParts[0] || updatorIdSubParts[1] != staffIdSubParts[1]) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Nhân viên %s không tồn tại trong bưu cục %s", staffId, agencyId), null));
            }
        }
        
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, "Ảnh không được để trống", null));
        }

        try {
            filesService.isValidImageFile(file);

            final Optional<Staff> optionalStaff = staffService.getStaffById(staffId);
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Nhân viên %s không tồn tại", staffId), null));
            }

            if (optionalStaff.get().getAvatar() != null) {
                filesService.deleteFile("/tdlogistics/staff/image/avatar/" + optionalStaff.get().getAvatar());
            }

            String filename = filesService.sendFile("/tdlogistics/staff/image/avatar", "default", file);
            Staff updatedStaff = new Staff();
            updatedStaff.setAvatar(filename);
            final Staff postUpdateStaff = staffService.updateStaffInfo(staffId, updatedStaff);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Staff>(true, "Cập nhật ảnh đại diện thành công", postUpdateStaff));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, "Đã xảy ra lỗi trong quá trình tải file. Vui lòng thử lại", null));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Staff>(true, e.getMessage(), null));
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Staff>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/avatar/get")
    public ResponseEntity<byte[]> getAvatar(
        // @RequestHeader(name = "role") Role role,
        // @RequestHeader(name = "userId") String userId,
        // @RequestHeader(name = "agencyId") String agencyId,
        @RequestParam(name = "staffId") String staffId
    ) {
        Role role = Role.ADMIN;
        String agencyId = "BC_fhwsjfnwskfws";
        String userId = "BC_wjkdfnwfw";
        try {
            if (List.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER, Role.COMPLAINTS_SOLVER).contains(role)) {
                final Optional<Staff> optionalStaff = staffService.getStaffById(staffId);
                if (optionalStaff.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }

                HttpHeaders headers = new HttpHeaders();
                // headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentType(new MediaType("image", "heic"));

                return new ResponseEntity<>(filesService.getFile("/tdlogistics/staff/image/avatar/" + optionalStaff.get().getAvatar()), headers, HttpStatus.OK);
            }

            if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER, Role.AGENCY_COMPLAINTS_SOLVER).contains(role)) {
                Staff tempStaff = new Staff();
                tempStaff.setAgencyId(agencyId);
                tempStaff.setStaffId(staffId);
                final Optional<Staff> optionalStaff = staffService.getOneStaff(tempStaff);
                if (optionalStaff.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);

                return new ResponseEntity<>(filesService.getFile("/tdlogistics/staff/image/avatar/" + optionalStaff.get().getAvatar()), headers, HttpStatus.OK);
            }

            if (List.of(Role.SHIPPER, Role.DRIVER).contains(role)) {
                if (!userId.equals(staffId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }

                final Optional<Staff> optionalStaff = staffService.getStaffById(staffId);
                if (optionalStaff.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);

                return new ResponseEntity<>(filesService.getFile("/tdlogistics/staff/image/avatar/" + optionalStaff.get().getAvatar()), headers, HttpStatus.OK);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null);
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