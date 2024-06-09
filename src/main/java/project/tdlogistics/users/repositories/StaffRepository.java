package project.tdlogistics.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.users.entities.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {
    public Staff findOneByFullname(String fullname);
    public Optional<Staff> findOneByCccd(String cccd);
}
