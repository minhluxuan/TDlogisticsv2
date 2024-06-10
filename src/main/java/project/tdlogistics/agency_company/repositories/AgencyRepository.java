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
}
