package project.tdlogistics.users.repositories;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByAccount(Account account);
    Optional<Token> findByToken(String token);
}

