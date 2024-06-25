package project.tdlogistics.users.services;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.repositories.AccountRepository;
import project.tdlogistics.users.repositories.StaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private final StaffRepository staffRepository;

    public AccountService(AccountRepository repository, StaffRepository staffRepository) {
        this.repository = repository;
        this.staffRepository = staffRepository;
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
        // info.setPassword(passwordEncoder.encode(info.getPassword()));
        info.setActive(false);
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
        }

        return userDetails;
    }

    public List<Account> getManyAccountsOfAgency(String agencyId, Account criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Account> example = Example.of(criteria, matcher);

        return repository.findAll(example)
            .stream()
            .filter(a -> 
            Set.of(
                Role.AGENCY_MANAGER,
                Role.AGENCY_HUMAN_RESOURCE_MANAGER,
                Role.TELLER,
                Role.COMPLAINTS_SOLVER,
                Role.SHIPPER,
                Role.DRIVER,
                Role.TRANSPORT_PARTNER_REPRESENTOR
            ).contains(a.getRole()))
            .filter(a -> {
                Account tempAccount = new Account();
                tempAccount.setId(a.getId());
                final Optional<Staff> optionalStaff = staffRepository.findByAccount(tempAccount);
                
                return optionalStaff.isPresent() && optionalStaff.get().getAgencyId().equals(agencyId);
            })
            .collect(Collectors.toList());
    }

    public List<Account> getManyAccounts(Account criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Account> example = Example.of(criteria, matcher);
    
        return repository.findAll(example);
    }
}