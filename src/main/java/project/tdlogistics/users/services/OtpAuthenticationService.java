package project.tdlogistics.users.services;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Otp;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.entities.Token;
import project.tdlogistics.users.repositories.AccountRepository;
import project.tdlogistics.users.repositories.CustomerRepository;
import project.tdlogistics.users.repositories.OtpRepository;
import project.tdlogistics.users.repositories.TokenRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.util.Duration;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class OtpAuthenticationService {
    private final JwtService jwtService;
    
    private final TokenRepository tokenRepository;

    private final CustomerRepository customerRepository;

    private final AccountRepository accountRepository;

    private final OtpRepository otpRepository;

    private final ObjectMapper objectMapper;

    public OtpAuthenticationService(OtpRepository otpRepository,
                                 CustomerRepository customerRepository,
                                 AccountRepository accountRepository,
                                 JwtService jwtService,
                                 TokenRepository tokenRepository,
                                 ObjectMapper objectMapper) {
        this.otpRepository = otpRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.objectMapper = objectMapper;
    }

    public AuthenticationResponse authenticate(Otp request) throws RuntimeException, ExecutionException, InterruptedException {
        try {
            final Optional<Otp> optionalOtp = otpRepository.findOneByPhoneNumberAndEmailAndOtp(request.getPhoneNumber(), request.getEmail(), request.getOtp());
            if (optionalOtp.isEmpty()) {
                return new AuthenticationResponse(null, "Otp không tồn tại");
            }

            OffsetDateTime expires = optionalOtp.get().getExpires();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
            System.out.println("Otp expires at: " + expires.format(formatter)); // Log with correct timezone

            if (OffsetDateTime.now().isAfter(expires)) {
                return new AuthenticationResponse(null, "Otp đã hết hạn");
            }

            Optional<Account> optionalAccount = accountRepository.findOneByPhoneNumber(request.getPhoneNumber());
            if (optionalAccount.isEmpty()) {
                Account newAccount = new Account(null, null, request.getPhoneNumber(), request.getEmail(), Role.CUSTOMER);
                Customer newCustomer = new Customer();
                accountRepository.save(newAccount);
                newCustomer.setAccount(newAccount);
                customerRepository.save(newCustomer);
                String jwt = jwtService.generateToken(newAccount, "CUSTOMER");
                saveUserToken(jwt, newAccount);
                return new AuthenticationResponse(jwt, "Xác thực thành công");
            }

            String jwt = jwtService.generateToken(optionalAccount.get(), "CUSTOMER");
            saveUserToken(jwt, optionalAccount.get());
            return new AuthenticationResponse(jwt, "Xác thực thành công");
        } catch (UsernameNotFoundException e) {
            return new AuthenticationResponse(null, "User not found");
        } catch (AuthenticationException e) {
            return new AuthenticationResponse(null, "Authentication failed");
        }
    }
    
    public void revokeAllTokenByAccount(Account account) throws ExecutionException, InterruptedException {
        List<Token> validTokens = tokenRepository.findByAccount(account);
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, Account account) throws ExecutionException, InterruptedException {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setAccount(account);
        tokenRepository.save(token);
    }

    public void saveOtp(Otp otp) {
        otpRepository.save(otp);
    }
}