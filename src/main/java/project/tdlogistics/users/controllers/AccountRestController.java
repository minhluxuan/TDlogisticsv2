package project.tdlogistics.users.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import project.tdlogistics.users.dto.UpdatePasswordDto;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.services.AccountService;
import project.tdlogistics.users.services.BasicAuthenticationService;
import project.tdlogistics.users.services.StaffService;

@RestController
@RequestMapping("/v2/accounts")
public class AccountRestController {
    @Autowired
    private BasicAuthenticationService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private StaffService staffService;

    @PutMapping("/password")
    public ResponseEntity<Response<String>> updatePassword(
        @RequestHeader(name = "userId") String userId,
        @RequestBody UpdatePasswordDto requestBody
    ) {
        try {
            if (authService.comparePassword(userId, requestBody.password)
            && authService.updatePassword(userId, requestBody.newPassword)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(new Response<String>(false, "Cập nhật mật khẩu thành công", null));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<String>(true, "Cập nhật mật khẩu thất bại", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<String>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Account>> updateAccount(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "accountId") String userAccountId,
        @RequestParam(name = "accountId") String accountId,
        @RequestBody Account payload
    ) {
        try {
            if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role) && !userAccountId.equals(accountId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Account>(true, "Thông tin không hợp lệ", null));
            }

            if (Set.of(Role.AGENCY_MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                final Optional<Staff> optionalStaff = staffService.getStaffByAccountId(accountId);
                
                if (optionalStaff.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Account>(true, "Người dùng không tồn tại", null));
                }

                final String[] userIdSubParts = userId.split("_");
                final String[] staffIdSubParts = optionalStaff.get().getStaffId().split("_");

                if (!userIdSubParts[0].equals(staffIdSubParts[0]) || !userIdSubParts[1].equals(staffIdSubParts[1])) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, String.format("Nhân viên %s không tồn tại trong bưu cục/đại lý %s", optionalStaff.get().getStaffId(), agencyId), null));
                }
            }

            final Account updatedAccount = authService.updateAccount(accountId, payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Account>(false, "Cập nhật tài khoản thành công", updatedAccount));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Account>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Account>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Account>>> getAccounts(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestBody Account criteria
    ) {
        if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Account>>(false, "Lấy thông tin tài khoản thành công", accountService.getManyAccountsOfAgency(agencyId, criteria)));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Account>>(false, "Lấy thông tin tài khoản thành công", accountService.getManyAccounts(criteria)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<List<Account>>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

}
