package project.tdlogistics.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Otp;
import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.services.OtpAuthenticationService;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// @CrossOrigin(origins = {"http://127.0.0.1:5500", "http://192.168.1.9:8762"}, allowCredentials = "true")
@RestController
@RequestMapping("/v2/auth/otp")
public class OtpAuthenticationController {
    @Autowired
    private OtpAuthenticationService authService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String exchange = "rpc-direct-exchange";

    @PostMapping("/send")
    public ResponseEntity<Response<String>> sendOtp(
            @RequestBody Otp request
    ) throws RuntimeException, ExecutionException, InterruptedException {
        try {
            String jsonRequest = objectMapper.writeValueAsString(new Request<Otp>("sendOtp", null, request));
            System.out.println("HERE0");
            byte[] jsonResponseBytes = (byte[]) amqpTemplate.convertSendAndReceive(exchange, "rpc.emails", jsonRequest);
            if (jsonResponseBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<String>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
            }

            String jsonResponse = new String(jsonResponseBytes);

            Response<Otp> response = objectMapper.readValue(jsonResponse, new TypeReference<Response<Otp>>() {});
            if (response.getError()) {
                return ResponseEntity.status(response.getStatus()).body(new Response<String>(true, response.getMessage(), null));
            }
            
            if (response.getData() == null) {
                return ResponseEntity.status(response.getStatus()).body(new Response<String>(true, response.getMessage(), null));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
            System.out.println("Otp expires at: " + response.getData().getExpires().format(formatter)); 
            final Otp otp = new Otp(response.getData().getOtp(), request.getPhoneNumber(), request.getEmail(), response.getData().getExpires());
            authService.saveOtp(otp);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<String>(true, "Gửi otp thành công.", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(new Response<String>(true, "Gửi otp thất bại", null));
        }
    } 

    @PostMapping("/verify")
    public ResponseEntity<Response<String>> verifyOtp(
            @RequestBody Otp request, HttpServletResponse servletResponse
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
