package project.tdlogistics.users.services;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.repositories.AccountRepository;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Optional<Account> findById(String id) {
        return repository.findById(id);
    }

    public UserDetails loadUserByAccountId(String accountId) {
        UserDetails userDetails = null;
        try {
            Optional<Account> account = repository.findById(accountId);
            userDetails = account.orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            // throw new UsernameNotFoundException("Error occurred while fetching user details", e);
        }

        return userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails userDetails = null;
        try {
            Optional<Account> account = repository.findByUsername(username);
            userDetails = account.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            // throw new UsernameNotFoundException("Error occurred while fetching user details", e);
        }

        return userDetails;
    }
}