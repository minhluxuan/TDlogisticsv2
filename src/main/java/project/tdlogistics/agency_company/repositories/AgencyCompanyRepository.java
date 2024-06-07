package project.tdlogistics.agency_company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.agency_company.entities.AgencyCompany;

public interface AgencyCompanyRepository extends JpaRepository<AgencyCompany, Long> {
}

