package project.tdlogistics.shipments.repositories;

import project.tdlogistics.shipments.entities.Shipment;

public interface ShipmentRepositoryInterface {
    public boolean updateParentAndIncreaseMass(Shipment shipment, String orderId, String postalCode);
    public boolean updateParentAndDecreaseMass(Shipment shipment, String orderId, String postalCode);
    public Shipment getOneShipment(String shipment_id, String postalCode);
}
