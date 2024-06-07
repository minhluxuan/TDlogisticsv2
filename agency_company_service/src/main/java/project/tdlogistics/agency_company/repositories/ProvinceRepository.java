package project.tdlogistics.agency_company.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.tdlogistics.agency_company.entities.Province;

public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    // Add or implement more necessary methods here
    @Query("SELECT p.managedBy FROM Province p WHERE p.province = ?1")
    String findManagedByByProvince(String province);

    Optional<Province> findProvinceByProvince(String province);
}