package project.tdlogistics.users.services;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.repositories.CustomerRepository;
import project.tdlogistics.users.repositories.StaffRepository;
import project.tdlogistics.users.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY = "BvPHGM8C0ia4uOuxxqPD5DTbWC9F9TWvPStp3pb7ARo0oK2mJ3pd3YG4lxA9i8bj6OTbadwezxgeEByY";
    private final TokenRepository tokenRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;

    public JwtService(TokenRepository tokenRepository, StaffRepository staffRepository, CustomerRepository customerRepository) {
        this.tokenRepository = tokenRepository;
        this.staffRepository = staffRepository;
        this.customerRepository = customerRepository;
    }

    public String extractAccountId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token) throws ExecutionException, InterruptedException {
        boolean validToken = tokenRepository
                .findByToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return !isTokenExpired(token) && validToken;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String generateToken(Account account, String option) {
        if (option.equals("STAFF")) {
            final Optional<Staff> optionalStaff = staffRepository.findByAccount(account);
            if (optionalStaff.isEmpty()) return null;
            String token = Jwts
                .builder()
                .subject(account.getId())
                .claim("userId", optionalStaff.get().getStaffId())
                .claim("agencyId", optionalStaff.get().getAgencyId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000 ))
                .signWith(getSigninKey())
                .compact();

            return token;
        }
        else if (option.equals("CUSTOMER")) {
            final Optional<Customer> optionalCustomer = customerRepository.findByAccount(account);
            if (optionalCustomer.isEmpty()) return null;
            String token = Jwts
                .builder()
                .subject(account.getId())
                .claim("role", account.getRole())
                .claim("userId", optionalCustomer.get().getId())
                .claim("agencyId", null)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000 ))
                .signWith(getSigninKey())
                .compact();
            System.out.println(token);
            return token;
        }

        return null;
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}