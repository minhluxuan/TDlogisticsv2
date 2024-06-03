package project.tdlogistics.users.controllers;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.services.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/auth")
public class AuthenticationController {
    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(
            @RequestBody Account request, HttpServletResponse servletResponse
            ) throws ExecutionException, InterruptedException {
        AuthenticationResponse response = authService.register(request);
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

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<String>(true, response.getMessage(), response.getToken()));
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(
            @RequestBody Account request, HttpServletResponse servletResponse
    ) throws RuntimeException, ExecutionException, InterruptedException {
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

        return ResponseEntity.status(HttpStatus.OK).body(new Response<String>(true, response.getMessage(), response.getToken()));
    }
}