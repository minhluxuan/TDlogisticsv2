package project.tdlogistics.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Otp;
import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.services.AccountService;
import project.tdlogistics.users.services.OtpAuthenticationService;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/v2/auth/otp")
public class OtpAuthenticationController {
    @Autowired
    private OtpAuthenticationService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    // private final String exchange = ""; // default exchange
    private final String routingKey = "rpc.emails";

    @CrossOrigin
    @PostMapping("/send")
    public ResponseEntity<Response<String>> login(
            @RequestBody Otp request
    ) throws RuntimeException, ExecutionException, InterruptedException {
        try {
            String jsonRequest = (new ObjectMapper()).writeValueAsString(new Request<Otp>("sendOtp", null, new Otp(null, "0981430418", "minhfaptv@gmail.com", null)));
            byte[] responseBytes = (byte[]) amqpTemplate.convertSendAndReceive(exchange, "rpc.emails", jsonRequest);
            String jsonResponse = new String(responseBytes);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Response<Otp> response = objectMapper.readValue(jsonResponse, new TypeReference<Response<Otp>>() {});
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

    @CrossOrigin
    @PostMapping("/verify")
    public ResponseEntity<Response<String>> login(
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
