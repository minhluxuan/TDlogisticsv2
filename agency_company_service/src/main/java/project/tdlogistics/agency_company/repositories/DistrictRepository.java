package project.tdlogistics.agency_company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.agency_company.entities.District;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, String> {
    // Add or implement more neccessary methods here
    List<District> findByProvince(String province);

    District findOneDistricsByProvinceAndDistrict(String province, String district);

    Optional<District> findDistrictByProvinceAndDistrict(String province, String district);
}