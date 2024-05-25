package project.tdlogistics.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.users.entities.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {
    
}
