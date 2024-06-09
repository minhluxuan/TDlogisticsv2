package project.tdlogistics.users.services;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.repositories.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

    public Optional<Account> checkExistStaffAccount(Account criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Account> example = Example.of(criteria, matcher);
        List<Account> accounts = repository.findAll(example);
        List<Account> nonOfCustomerRoleAccounts = accounts.stream().filter(a -> !a.getRole().equals(Role.CUSTOMER)).collect(Collectors.toList());;
        if (nonOfCustomerRoleAccounts.size() > 0) {
            return Optional.of(nonOfCustomerRoleAccounts.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Account createNewAccount(Account info) {
        return repository.save(info);
    }

    public Optional<Account> findById(String id) {
        return repository.findById(id);
    }

    public Optional<Account> findOneByPhoneNumber(String phoneNumber) {
        return repository.findOneByPhoneNumber(phoneNumber);
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