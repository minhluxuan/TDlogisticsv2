package project.tdlogistics.agency_company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import project.tdlogistics.agency_company.entities.Agency;
import java.util.*;

public interface AgencyRepository extends JpaRepository<Agency, String>, CustomAgencyRepository {
    Optional<Agency> findByEmail(String email);
}

interface CustomAgencyRepository {
    boolean checkExistTableWithPostalCode(String postalCode);

    @Transactional
    boolean checkTableExists(String tableName);

    @Transactional
    String createTablesForAgency(String postalCode) throws Exception;

    @Transactional
    void dropTablesForAgency(String postalCode) throws Exception;

    // @Transactional
    // Response locateAgencyInArea(int choice, String province, String district, List<String> wards, String agencyId,
    //         String postalCode) throws Exception;

    // // @Transactional
    // // List<Agency> getManyAgencies(Map<String, Object> info, Map<String, Object>
    // // paginationConditions);

    // @Transactional
    // List<String> getAgencyManagedWards(String agencyId);
}
