package project.tdlogistics.agency_company.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.placeholder.Response;

import java.util.*;
import java.util.List;

public interface AgencyRepository extends JpaRepository<Agency, String>, CustomAgencyRepository {
    Optional<Agency> findByEmail(String email);
}

interface CustomAgencyRepository {
    boolean checkExistTableWithPostalCode(String postalCode);

    @Transactional
    boolean checkTableExists(String tableName);

    @Transactional
    List<String> createTablesForAgency(String postalCode) throws Exception;

    @Transactional
    Response dropTablesForAgency(String postalCode) throws Exception;

    @Transactional
    Response locateAgencyInArea(int choice, String province, String district, List<String> wards, String agencyId,
            String postalCode) throws Exception;

    // @Transactional
    // List<Agency> getManyAgencies(Map<String, Object> info, Map<String, Object>
    // paginationConditions);

    @Transactional
    List<String> getAgencyManagedWards(String agencyId);
}
