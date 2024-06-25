package project.tdlogistics.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.PartnerStaff;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.entities.TransportPartner;
import project.tdlogistics.users.services.AccountService;
import project.tdlogistics.users.services.FilesService;
import project.tdlogistics.users.services.PartnerStaffService;
import project.tdlogistics.users.services.TransportPartnerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/v2/partner_staffs")
public class TransportPartnerStaffRestController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PartnerStaffService partnerStaffService;

    @Autowired
    private TransportPartnerService transportPartnerService;

    @Autowired
    private FilesService filesService;

    @PostMapping("/create")
    public ResponseEntity<Response<PartnerStaff>> createNewPartnerStaff(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestBody PartnerStaff payload
    ) {
        if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<PartnerStaff>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }
        
        try {
            final String[] agencyIdSubPart = agencyId.split("_");
            final String[] partnerIdSubPart = payload.getPartnerId().split("_");

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)
            && (agencyIdSubPart[0] != partnerIdSubPart[0] || agencyIdSubPart[1] != partnerIdSubPart[1])) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<PartnerStaff>(true, String.format("Đối tác vận tải %s không thuộc quyền quản lý của bưu cục/đại lý %s", payload.getPartnerId(), agencyId), null));
            }

            final TransportPartner tempTransportPartner = new TransportPartner();
            tempTransportPartner.setTransportPartnerId(payload.getPartnerId());
            final TransportPartner transportPartner = transportPartnerService.checkExistTransportPartner(tempTransportPartner);
            if (transportPartner == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Đối tác vận tải %s không tồn tại", payload.getPartnerId()), null));
            }

            final Optional<Account> optionalAccount = accountService.findById(payload.getAccount().getId());
            if (optionalAccount.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, "Tài khoản không tồn tại", null));
            }

            final Optional<PartnerStaff> optionalPartnerStaff = partnerStaffService.getPartnerStaffByCccd(payload.getCccd());
            if (optionalPartnerStaff.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<PartnerStaff>(true, "Nhân viên đối tác vận tải có mã cccd " + payload.getCccd() + " đã tồn tại", null));
            }

            payload.setStaffId(agencyIdSubPart[0] + "_" + agencyIdSubPart[1] + "_" + payload.getCccd());
            payload.setActive(false);
            payload.setAgencyId(agencyId);
            final PartnerStaff createdPartnerStaff = partnerStaffService.createNewPartnerStaff(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<PartnerStaff>(false, "Tạo nhân viên đối tác vận tải thành công", createdPartnerStaff));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<PartnerStaff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<PartnerStaff>>> getPartnerStaff(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "transportPartnerId", required = false) String transportPartnerId,
        @RequestBody PartnerStaff criteria
    ) {
        try {
            if (Role.TRANSPORT_PARTNER_REPRESENTOR.equals(role)) {
                if (!criteria.getPartnerId().equals(transportPartnerId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<PartnerStaff>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
                }
            
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<PartnerStaff>>(
                    false,
                    "Lấy thông tin thành công",
                    partnerStaffService.getPartnerStaffs(criteria)
                ));
            }
    
            if (Role.DRIVER.equals(role)) {
                if (!criteria.getPartnerId().equals(transportPartnerId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<PartnerStaff>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
                }
    
                criteria.setStaffId(userId);
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<PartnerStaff>>(
                    false,
                    "Lấy thông tin thành công",
                    partnerStaffService.getPartnerStaffs(criteria)
                ));
            }
    
            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER, Role.AGENCY_COMPLAINTS_SOLVER).contains(role)) {
                criteria.setAgencyId(agencyId);
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<PartnerStaff>>(
                    false,
                    "Lấy thông tin thành công",
                    partnerStaffService.getPartnerStaffs(criteria)
                ));
            }
    
            if (Set.of(Role.ADMIN, Role.MANAGER, Role.TELLER, Role.COMPLAINTS_SOLVER).contains(role)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<PartnerStaff>>(
                    false,
                    "Lấy thông tin thành công",
                    partnerStaffService.getPartnerStaffs(criteria)
                ));
            }
    
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<PartnerStaff>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<List<PartnerStaff>>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<Response<PartnerStaff>> updatePartnerStaff(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "staffId") String partnerStaffId,
        @RequestBody PartnerStaff payload
    ) {
        if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<PartnerStaff>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            final String[] partnerStaffIdSubParts = partnerStaffId.split("_");
            final String[] agencyIdSubParts = agencyId.split("_");

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)
            && (partnerStaffIdSubParts[0] != agencyIdSubParts[0] || partnerStaffIdSubParts[1] != agencyIdSubParts[1])) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Nhân viên %s không thuộc quyền quản lý của bưu cục %s", partnerStaffId, agencyId), null));
            }

            final PartnerStaff updatedPartnerStaff = partnerStaffService.updatePartnerStaff(partnerStaffId, payload);
            if (updatedPartnerStaff == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Nhân viên %s không tồn tại", partnerStaffId), null));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<PartnerStaff>(true, "Cập nhật thành công", updatedPartnerStaff));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<PartnerStaff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<PartnerStaff>> deletePartnerStaff(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "staffId") String partnerStaffId
    ) {
        if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<PartnerStaff>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            final String[] partnerStaffIdSubParts = partnerStaffId.split("_");
            final String[] agencyIdSubParts = agencyId.split("_");

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)
            && (partnerStaffIdSubParts[0] != agencyIdSubParts[0] || partnerStaffIdSubParts[1] != agencyIdSubParts[1])) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Nhân viên %s không thuộc quyền quản lý của bưu cục %s", partnerStaffId, agencyId), null));
            }

            partnerStaffService.deletePartnerStaff(partnerStaffId);

            return ResponseEntity.status(HttpStatus.OK).body(new Response<PartnerStaff>(true, "Xóa thành công", null));
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Đối tác vận tải %s không tồn tại", partnerStaffId), null)); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<PartnerStaff>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PutMapping("/avatar/update")
    public ResponseEntity<Response<PartnerStaff>> updateLicenseImages(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestParam("front") MultipartFile front,
        @RequestParam("back") MultipartFile back,
        @RequestParam("staffId") String staffId
    ) {
        if (!List.of(Role.ADMIN, Role.AGENCY_MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<PartnerStaff>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            final String[] updatorIdSubParts = userId.split("_");
            final String[] staffIdSubParts = staffId.split("_");
            if (updatorIdSubParts[0] != staffIdSubParts[0] || updatorIdSubParts[1] != staffIdSubParts[1]) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Nhân viên %s không thuộc quyền quản lý của bưu cục %s", staffId, agencyId), null));
            }
        }
        
        if (front.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, "Ảnh mặt trước giấy phép không được để trống", null));
        }

        if (back.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, "Ảnh mặt sau giấy phép không được để trống", null));
        }

        try {
            filesService.isValidImageFile(front);
            filesService.isValidImageFile(back);

            final Optional<PartnerStaff> optionalStaff = partnerStaffService.getPartnerStaffById(staffId);
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<PartnerStaff>(true, String.format("Nhân viên %s không tồn tại", staffId), null));
            }

            if (optionalStaff.get().getImageLicense() != null) {
                for (final String i : optionalStaff.get().getImageLicense()) {
                    filesService.deleteFile("/tdlogistics/partner_staff/image/license/" + i);
                }
            }

            String frontFilename = filesService.sendFile("/tdlogistics/partner_staff/image/license/", "default", front);
            String backFilename = filesService.sendFile("/tdlogistics/partner_staff/image/license/", "default", back);

            PartnerStaff updatedStaff = new PartnerStaff();
            updatedStaff.setImageLicense(List.of(frontFilename, backFilename));

            final PartnerStaff postUpdateStaff = partnerStaffService.updatePartnerStaff(staffId, updatedStaff);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<PartnerStaff>(true, "Cập nhật giấy phép thành công", postUpdateStaff));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<PartnerStaff>(true, "Đã xảy ra lỗi trong quá trình tải file. Vui lòng thử lại", null));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<PartnerStaff>(true, e.getMessage(), null));
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<PartnerStaff>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }
}
