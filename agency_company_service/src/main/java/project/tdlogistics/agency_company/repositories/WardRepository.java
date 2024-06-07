package project.tdlogistics.agency_company.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.agency_company.entities.Ward;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, Integer> {
    // Add or implement more necessary methods here

    List<Ward> findByProvinceAndDistrict(String province, String district);

    Optional<Ward> findByProvinceAndDistrictAndWard(String province, String district, String ward);
}