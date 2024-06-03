package project.tdlogistics.users.services;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Token;
import project.tdlogistics.users.repositories.AccountRepository;
import project.tdlogistics.users.repositories.TokenRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticationService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AccountRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenRepository tokenRepository,
                                 AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(Account request) throws ExecutionException, InterruptedException {
        // check if user already exist. if exist than authenticate the user
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse(null, "User already exist");
        }

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(request.getRole());
        
        String jwt = jwtService.generateToken(account);
        System.out.println(account);
        repository.save(account);

        saveUserToken(jwt, account);
        return new AuthenticationResponse(jwt, "User registration was successful");
    }

    public AuthenticationResponse authenticate(Account request) throws RuntimeException, ExecutionException, InterruptedException {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
            );

            Account account = repository.findByUsername(request.getUsername()).orElseThrow(null);
            String jwt = jwtService.generateToken(account);

            revokeAllTokenByAccount(account);
            saveUserToken(jwt, account);

            return new AuthenticationResponse(jwt, "User login was successful");
        } catch (UsernameNotFoundException e) {
            return new AuthenticationResponse(null, "User not found");
        } catch (AuthenticationException e) {
            return new AuthenticationResponse(null, "Authentication failed");
        }
    }
    
    private void revokeAllTokenByAccount(Account account) throws ExecutionException, InterruptedException {
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
}