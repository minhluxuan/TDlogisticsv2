package project.tdlogistics.shipments.repositories;

import project.tdlogistics.shipments.entities.Shipment;

public interface ShipmentRepositoryInterface {
    boolean createNewShipment(Shipment shipment, String postalCode);
    boolean updateParentAndIncreaseMass(Shipment shipment, String orderId, String postalCode);
    boolean updateParentAndDecreaseMass(Shipment shipment, String orderId, String postalCode);
    Shipment getOneShipment(String shipmentId, String postalCode);
    int updateStatus(String shipmentId, int status, String postalCode);
}
