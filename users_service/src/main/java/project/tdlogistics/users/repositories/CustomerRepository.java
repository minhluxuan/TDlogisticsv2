package project.tdlogistics.users.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import project.tdlogistics.users.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    public Customer findOneByFullname(String fullname);
}
