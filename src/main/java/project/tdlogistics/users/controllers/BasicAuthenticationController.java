package project.tdlogistics.users.controllers;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.services.BasicAuthenticationService;
import project.tdlogistics.users.services.ValidationService;
import project.tdlogistics.users.validations.account.Create;
import project.tdlogistics.users.validations.account.Login;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/auth/basic/")
public class BasicAuthenticationController {
    private final BasicAuthenticationService authService;

    private final ValidationService validationService;

    public BasicAuthenticationController(BasicAuthenticationService authService, ValidationService validationService) {
        this.authService = authService;
        this.validationService = validationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<Account>> register(
            @RequestHeader(name = "role") Role role,
            @RequestBody @Valid Account request,
            HttpServletResponse servletResponse
        ) {
        try {
            validationService.validateRequest(request, Create.class);

            if (!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Account>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }
    
            Account createdAccount = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Account>(true, "Tạo tài khoản thành công", createdAccount));
        } catch (BindException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, e.getAllErrors().get(0).getDefaultMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(
            @RequestBody Account request, HttpServletResponse servletResponse
    ) throws RuntimeException, ExecutionException, InterruptedException {
        try {
            validationService.validateRequest(request, Login.class);

            final AuthenticationResponse response = authService.authenticate(request);
            if (response.getToken() != null) {
                Cookie authCookie = new Cookie("JSESSIONID", response.getToken());
                authCookie.setHttpOnly(false);
                authCookie.setSecure(false);
                authCookie.setMaxAge((int) Duration.of(1, ChronoUnit.DAYS).toSeconds());
                authCookie.setPath("/");
                servletResponse.addCookie(authCookie);
            }
            
            if (response.getToken() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<String>(true, response.getMessage(), response.getToken()));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<String>(false, response.getMessage(), response.getToken()));
        } catch (BindException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, e.getAllErrors().get(0).getDefaultMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }
}