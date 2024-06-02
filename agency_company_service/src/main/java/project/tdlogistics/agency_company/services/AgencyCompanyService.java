package project.tdlogistics.agency_company.services;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import project.tdlogistics.agency_company.entities.AgencyCompany;
import project.tdlogistics.agency_company.repositories.AgencyCompanyRepository;


@Service
public class AgencyCompanyService {
    private final AgencyCompanyRepository agencyCompanyRepository;

    @Autowired
    public AgencyCompanyService(AgencyCompanyRepository agencyCompanyRepository) {
        this.agencyCompanyRepository = agencyCompanyRepository;
    }

    public Optional<AgencyCompany> checkExistAgencyCompany(AgencyCompany criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<AgencyCompany> example = Example.of(criteria, matcher);
        return agencyCompanyRepository.findOne(example);
    }


    public AgencyCompany createNewAgencyCompany(AgencyCompany agencyCompany) {
        return agencyCompanyRepository.save(agencyCompany);
    }

    public AgencyCompany updateAgencyCompany(String id, AgencyCompany agencyCompany) {
        Optional<AgencyCompany> AgencyCompanyOptional = agencyCompanyRepository.findById(id);
        if (AgencyCompanyOptional.isEmpty()) {
            return null;
        }
        AgencyCompany agencyCompanyToUpdate = AgencyCompanyOptional.get();

        if (agencyCompany.getCompany_name() != null) {
            agencyCompanyToUpdate.setCompany_name(agencyCompany.getCompany_name());
        }
        if (agencyCompany.getLicense() != null) {
            agencyCompanyToUpdate.setLicense(agencyCompany.getLicense());
        }
        if (agencyCompany.getTax_number() != null) {
            agencyCompanyToUpdate.setTax_number(agencyCompany.getTax_number());
        }

        return agencyCompanyRepository.save(agencyCompanyToUpdate);

    }

    public void deleteAgencyCompany(AgencyCompany criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<AgencyCompany> example = Example.of(criteria, matcher);
        Optional<AgencyCompany> agencyCompany = agencyCompanyRepository.findOne(example);
        agencyCompany.ifPresent(agencyCompanyRepository::delete);
    }
}
