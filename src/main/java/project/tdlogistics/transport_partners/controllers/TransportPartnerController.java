package project.tdlogistics.transport_partners.controllers;

import org.springframework.web.bind.annotation.RestController;

import project.tdlogistics.transport_partners.entities.Account;
import project.tdlogistics.transport_partners.entities.Agency;
import project.tdlogistics.transport_partners.entities.Response;
import project.tdlogistics.transport_partners.entities.TransportPartner;
import project.tdlogistics.transport_partners.entities.TransportPartnerRepresentor;
import project.tdlogistics.transport_partners.services.AccountService;
import project.tdlogistics.transport_partners.services.AgencyService;
import project.tdlogistics.transport_partners.services.TransportPartnerService;
import project.tdlogistics.transport_partners.services.TransportPartnerStaffService;
import project.tdlogistics.transport_partners.entities.Role;
import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/v2/transport_partners")
public class TransportPartnerController {

    @Autowired
    private TransportPartnerService transportPartnerService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransportPartnerStaffService transportPartnerStaffService;

    @PostMapping("/search")
    public ResponseEntity<Response<List<TransportPartner>>> getTransportPartners(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "transportPartnerId", required = false) String transportPartnerId,
        @RequestBody TransportPartner criteria
    ) {
        try {
            if (Set.of(Role.TRANSPORT_PARTNER_REPRESENTOR).contains(role)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<TransportPartner>>(
                    false,
                    "Lấy dữ liệu thành công",
                    transportPartnerService.getTransportPartners(criteria)
                ));
            }
            
            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER, Role.AGENCY_COMPLAINTS_SOLVER).contains(role)) {
                criteria.setAgencyId(agencyId);
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<TransportPartner>>(
                    false,
                    "Lấy thông tin thành công",
                    transportPartnerService.getTransportPartners(criteria)
                ));
            }

            if (Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER, Role.COMPLAINTS_SOLVER).contains(role)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<TransportPartner>>(
                    false,
                    "Lấy dữ liệu thành công",
                    transportPartnerService.getTransportPartners(criteria)
                )); 
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<TransportPartner>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<List<TransportPartner>>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<TransportPartner>> createTransportPartner(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestBody TransportPartner payload
    ) {
        if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<TransportPartner>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {

            // Check agency existence
            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                Agency tempAgency = new Agency();
                tempAgency.setAgencyId(agencyId);
                final Agency agency = agencyService.checkExistAgency(tempAgency);
                if (agency == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<TransportPartner>(true, "Bưu cục/Đại lý của bạn không thể tìm thấy trong bưu cục. Vui lòng kiểm tra lại", null));
                }
            }
            else if (Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                // validate input here
            }


            // Check account with phone number existence
            final Account accountWithPhoneNumber = accountService.checkExistAccount(new Account(null, payload.getRepresentor().getAccount().getPhoneNumber(), null));
            if (accountWithPhoneNumber != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<TransportPartner>(true, String.format("Tài khoản có số điện thoại %s đã tồn tại", payload.getRepresentor().getAccount().getPhoneNumber()), null));
            }

            // Check account with email existence
            final Account accountWithEmail = accountService.checkExistAccount(new Account(null, null, payload.getRepresentor().getAccount().getEmail()));
            if (accountWithEmail != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<TransportPartner>(true, String.format("Tài khoản có email %s đã tồn tại", payload.getRepresentor().getAccount().getEmail()), null));
            }

            String defaultUsername = payload.getRepresentor().getAccount().getEmail().split("@")[0];
            String username;

            // check and define username
            while (true) {
                String randomString = RandomStringUtils.randomNumeric(4);
                username = defaultUsername + randomString;

                final Account accountWithUsername = accountService.checkExistAccount(new Account(username, null, null));

                if (accountWithUsername == null) {
                    break;
                }
            }

            TransportPartnerRepresentor representor = new TransportPartnerRepresentor();
            representor.setCccd(payload.getRepresentor().getCccd());
            if (transportPartnerStaffService.checkExiTransportPartnerRepresentor(representor) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<TransportPartner>(true, String.format("Nhân viên đối tác vận tải có căn cước công dân %s đã tồn tại", payload.getRepresentor().getCccd()), null));
            }

            final String[] agencyIdSubParts = agencyId.split("_");
            final String transportPartnerId = agencyIdSubParts[0] + "_" + agencyIdSubParts[1] + "_" + payload.getRepresentor().getCccd();

            payload.getRepresentor().setStaffId(transportPartnerId);
            payload.getRepresentor().setAgencyId(agencyId);
            payload.getRepresentor().setPartnerId(transportPartnerId);
            payload.getRepresentor().getAccount().setUsername(username);
            payload.getRepresentor().getAccount().setPassword(RandomStringUtils.randomAlphanumeric(8));
            payload.getRepresentor().getAccount().setRole(Role.TRANSPORT_PARTNER_REPRESENTOR);
            payload.getRepresentor().setActive(false);

            // Create transport partner staff account
            final Account createdAccount = accountService.createNewAccount(payload.getRepresentor().getAccount());
            if (createdAccount == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<TransportPartner>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
            }

            // Create transport partner staff
            payload.getRepresentor().setAccount(createdAccount);
            final TransportPartnerRepresentor createdRepresentor = transportPartnerStaffService.createTransportPartnerRepresentor(payload.getRepresentor());

            // Create transport partner
            payload.setTransportPartnerId(transportPartnerId);
            final TransportPartner createdTransportPartner = transportPartnerService.createTransportPartner(payload);
            createdTransportPartner.setRepresentor(createdRepresentor);

            // Send account to email here

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<TransportPartner>(false, "Tạo đối tác vận tải thành công", createdTransportPartner));
        } catch (InternalError e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<TransportPartner>(true, e.getMessage(), null));
        } 
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<TransportPartner>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<TransportPartner>> updateTransportPartner(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "transportPartnerId") String transportPartnerId,
        @RequestBody TransportPartner payload
    ) {
        try {
            if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<TransportPartner>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            final String[] userIdSubParts = userId.split("_");
            final String[] transportPartnerIdSubParts = transportPartnerId.split("_");

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)
            && (userIdSubParts[0] != transportPartnerIdSubParts[0] || userIdSubParts[1] != transportPartnerIdSubParts[1]) 
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<TransportPartner>(true, String.format("Đối tác vận tải %s không thuộc quyền quản lý của bạn", transportPartnerId), null));
            }

            final TransportPartner updatedTransportPartner = transportPartnerService.updateTransportPartner(transportPartnerId, payload);
            if (updatedTransportPartner == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<TransportPartner>(true, String.format("Đối tác vận tải %s không tồn tại", transportPartnerId), null));
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<TransportPartner>(false, "Cập nhật thành công", updatedTransportPartner));  
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<TransportPartner>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<TransportPartner>> deleteTransportPartner(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "transportPartnerId") String transportPartnerId,
        @RequestBody TransportPartner payload
    ) {
        try {
            if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<TransportPartner>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            final String[] userIdSubParts = userId.split("_");
            final String[] transportPartnerIdSubParts = transportPartnerId.split("_");

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)
            && (userIdSubParts[0] != transportPartnerIdSubParts[0] || userIdSubParts[1] != transportPartnerIdSubParts[1]) 
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<TransportPartner>(true, String.format("Đối tác vận tải %s không thuộc quyền quản lý của bạn", transportPartnerId), null));
            }

            transportPartnerService.deleteTransportPartner(transportPartnerId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<TransportPartner>(false, "Xoá thành công", null));  
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<TransportPartner>(true, String.format("Đối tác vận tải %s không tồn tại", transportPartnerId), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<TransportPartner>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }
}
