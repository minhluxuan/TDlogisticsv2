package project.tdlogistics.users.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import project.tdlogistics.users.configurations.MyBeanUtils;
import project.tdlogistics.users.entities.PartnerStaff;
import project.tdlogistics.users.repositories.TransportPartnerStaffRepository;

@Service
public class PartnerStaffService {

    @Autowired
    private TransportPartnerStaffRepository partnerStaffRepository;

    public Optional<PartnerStaff> checkExistPartnerStaff(PartnerStaff criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<PartnerStaff> example = Example.of(criteria, matcher);
        return partnerStaffRepository.findOne(example);
    }

    public Optional<PartnerStaff> getPartnerStaffById(String id) {
        return partnerStaffRepository.findById(id);
    }

    public List<PartnerStaff> getPartnerStaffByPartnerId(String partnerId) {
        return partnerStaffRepository.findByPartnerId(partnerId);
    }

    public Optional<PartnerStaff> getPartnerStaffByCccd(String cccd) {
        return partnerStaffRepository.findOneByCccd(cccd);
    }

    public List<PartnerStaff> getPartnerStaffs(PartnerStaff criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<PartnerStaff> example = Example.of(criteria, matcher);
        return partnerStaffRepository.findAll(example);
    }

    public PartnerStaff createNewPartnerStaff(PartnerStaff payload) {
        return partnerStaffRepository.save(payload);
    }

    public PartnerStaff updatePartnerStaff(String staffId, PartnerStaff payload) {
        Optional<PartnerStaff> optionalPartnerStaff = partnerStaffRepository.findById(staffId);
        if (optionalPartnerStaff.isEmpty()) {
            return null;
        }

        MyBeanUtils.copyNonNullProperties(payload, optionalPartnerStaff.get());
        return partnerStaffRepository.save(optionalPartnerStaff.get());
    }

    public void deletePartnerStaff(String staffId) throws EmptyResultDataAccessException {
        partnerStaffRepository.deleteById(staffId);
    }
}
