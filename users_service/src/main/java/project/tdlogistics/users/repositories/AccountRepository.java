package project.tdlogistics.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.users.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
}
