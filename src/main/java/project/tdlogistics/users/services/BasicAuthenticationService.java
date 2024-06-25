package project.tdlogistics.users.services;

import project.tdlogistics.users.configurations.MyBeanUtils;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.AuthenticationResponse;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.entities.Token;
import project.tdlogistics.users.repositories.AccountRepository;
import project.tdlogistics.users.repositories.StaffRepository;
import project.tdlogistics.users.repositories.TokenRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class BasicAuthenticationService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StaffRepository staffRepository;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    public BasicAuthenticationService(AccountRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenRepository tokenRepository,
                                 AuthenticationManager authenticationManager,
                                 StaffRepository staffRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.staffRepository = staffRepository;
    }

    public Account register(Account request) throws ExecutionException, InterruptedException {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Tên đăng nhập đã tồn tại. Vui lòng chọn tên đăng nhập khác.");
        }
        
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(request.getRole());
        account.setActive(false);
        account.setEmail(request.getEmail());
        account.setPhoneNumber(request.getPhoneNumber());
        final Account createdAccount = repository.save(account);
        createdAccount.setPassword(null);
        return createdAccount;
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
            String jwt = jwtService.generateToken(account, "STAFF");

            revokeAllTokenByAccount(account);
            saveUserToken(jwt, account);

            return new AuthenticationResponse(jwt, "Xác thực thành công");
        } catch (UsernameNotFoundException e) {
            return new AuthenticationResponse(null, "Xác thực thất bại");
        } catch (AuthenticationException e) {
            return new AuthenticationResponse(null, "Xác thực thất bại");
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

    public Boolean comparePassword(String staffId, String password) {
        try {
            final Optional<Staff> optionalStaff = staffRepository.findById(staffId);
            if (optionalStaff.isEmpty()) {
                throw new EntityNotFoundException("Người dùng không tồn tại");
            }

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        optionalStaff.get().getAccount().getUsername(),
                        password
                )
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updatePassword(String staffId, String newPassword) {
        try {
            final Optional<Staff> staff = staffRepository.findById(staffId);
            staff.get().getAccount().setPassword(passwordEncoder.encode(newPassword));
            repository.save(staff.get().getAccount());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Account updateAccount(String accountId, Account account) {
        Optional<Account> optionalAccount = repository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("Tài khoản không tồn tại");
        }

        Account existingAccount = optionalAccount.get();

        MyBeanUtils.copyNonNullProperties(account, existingAccount);

        final Account updatedAccount = repository.save(existingAccount);
        updatedAccount.setPassword(null);
        return updatedAccount;
    }
}