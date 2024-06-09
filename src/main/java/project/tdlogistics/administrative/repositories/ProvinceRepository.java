package project.tdlogistics.administrative.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.tdlogistics.administrative.entities.Province;

public interface ProvinceRepository extends JpaRepository<Province, String> {
    @Query("SELECT p.managedBy FROM Province p WHERE p.province = ?1")
    String findManagedByByProvince(String province);
}