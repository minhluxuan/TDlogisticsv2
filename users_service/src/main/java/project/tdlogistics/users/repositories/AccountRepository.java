package project.tdlogistics.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.users.entities.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findOneByPhoneNumber(String phoneNumber);
}
