package project.tdlogistics.users.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    public Customer findOneByFullname(String fullname);
    public Optional<Customer> findByAccount(Account account);
}
