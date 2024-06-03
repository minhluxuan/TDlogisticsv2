package project.tdlogistics.users.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import project.tdlogistics.users.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public Customer findOneByFullname(String fullname);
}
