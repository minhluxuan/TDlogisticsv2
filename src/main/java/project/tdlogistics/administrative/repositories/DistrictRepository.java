package project.tdlogistics.administrative.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.administrative.entities.District;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, String> {
    // Add or implement more neccessary methods here
    List<District> findByProvince(String province);
}