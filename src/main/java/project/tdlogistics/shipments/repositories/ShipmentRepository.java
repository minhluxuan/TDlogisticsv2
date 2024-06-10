package project.tdlogistics.shipments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.tdlogistics.shipments.entities.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, String>, ShipmentRepositoryInterface {
    // Add or implement more neccessary methods here
    public String countByShipmentId(String shipmentId);

    // @Query(value = "SELECT * FROM :tableName WHERE shipment_id = :shipmentId LIMIT 1", nativeQuery = true)
    // public Shipment findOneByShipmentId(@Param("tableName") String tableName, @Param("shipmentId") String shipmentId);
  
    public Optional<Shipment> findOneByShipmentId(String shipmentId);

    @Query(value = "SELECT * FROM :tableName WHERE shipment_id = :shipmetId LIMIT 1", nativeQuery = true)
    public Optional<Shipment> findOneByShipmentId(@Param("shipmentId") String shipmentId, @Param("tableName") String tableName);

}
