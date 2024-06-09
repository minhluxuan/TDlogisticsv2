package project.tdlogistics.administrative.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.administrative.entities.Ward;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, Integer> {
    // Add or implement more necessary methods here


    List<Ward> findByProvinceAndDistrict(String province, String district);
}