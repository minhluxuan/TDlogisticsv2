package project.tdlogistics.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.users.entities.Account;

public interface UserRepository<T, ID> extends JpaRepository<Account, Long> {
    // public List<Account> findByUsernameAndRole(String username, String role);

    // @Query("SELECT u FROM User u WHERE u.username = :username AND u.role = :role")
    // public List<Account> findByUsernameAndRoleQuery(@Param("username") String username, @Param("role") String role);
}
