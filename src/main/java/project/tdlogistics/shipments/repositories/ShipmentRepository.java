package project.tdlogistics.shipments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.shipments.entities.Shipment;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    // Add or implement more neccessary methods here
    public String countByShipmentId(String shipmentId);
    public Optional<Shipment> findOneByShipmentId(String shipmentId);

}
