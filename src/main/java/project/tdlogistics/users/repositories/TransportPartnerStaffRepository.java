package project.tdlogistics.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.tdlogistics.users.entities.PartnerStaff;

public interface TransportPartnerStaffRepository extends JpaRepository<PartnerStaff, String> {
    public List<PartnerStaff> findByPartnerId(String partnerId);
    public Optional<PartnerStaff> findOneByCccd(String cccd);
}
