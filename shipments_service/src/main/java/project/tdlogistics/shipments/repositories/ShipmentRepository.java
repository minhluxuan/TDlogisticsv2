package project.tdlogistics.shipments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import project.tdlogistics.shipments.entities.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    // Add or implement more neccessary methods here
    public String countByShipmentId(String shipmentId);

    // @Query(value = "SELECT * FROM :tableName WHERE shipment_id = :shipmentId LIMIT 1", nativeQuery = true)
    // public Shipment findOneByShipmentId(@Param("tableName") String tableName, @Param("shipmentId") String shipmentId);
  
    public Shipment findOneByShipmentId(String shipmentId);
}
