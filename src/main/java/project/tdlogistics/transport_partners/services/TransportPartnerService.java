package project.tdlogistics.transport_partners.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import project.tdlogistics.transport_partners.entities.TransportPartner;
import project.tdlogistics.transport_partners.repositories.TransportPartnerRepository;
import project.tdlogistics.transport_partners.configurations.MyBeanUtils;

@Service
public class TransportPartnerService {
    
    @Autowired
    private TransportPartnerRepository transportPartnerRepository;

    public List<TransportPartner> getTransportPartners(TransportPartner criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<TransportPartner> example = Example.of(criteria, matcher);
        return transportPartnerRepository.findAll(example);
    }

    public Optional<TransportPartner> checkExistTransportPartner(TransportPartner criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<TransportPartner> example = Example.of(criteria, matcher);
        return transportPartnerRepository.findOne(example);
    }

    public TransportPartner createTransportPartner(TransportPartner payload) {
        return transportPartnerRepository.save(payload);
    }

    public TransportPartner updateTransportPartner(String id, TransportPartner payload) {
        final Optional<TransportPartner> optionalTransportPartner = transportPartnerRepository.findById(id);
        if (optionalTransportPartner.isEmpty()) {
            return null;
        }

        TransportPartner existingTransportPartner = optionalTransportPartner.get();

        MyBeanUtils.copyNonNullProperties(payload, existingTransportPartner);

        return transportPartnerRepository.save(existingTransportPartner);
    }

    public void deleteTransportPartner(String id) throws EmptyResultDataAccessException {
        transportPartnerRepository.deleteById(id);
    }
}
