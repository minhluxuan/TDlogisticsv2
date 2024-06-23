package project.tdlogistics.transport_partners.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.transport_partners.entities.TransportPartner;

public interface TransportPartnerRepository extends JpaRepository<TransportPartner, String> {
    
}
