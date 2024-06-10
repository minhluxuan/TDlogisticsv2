package project.tdlogistics.shipments.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import project.tdlogistics.shipments.entities.Shipment;

public class ShipmentRepositoryImplement implements ShipmentRepositoryInterface {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean updateParentAndIncreaseMass(Shipment shipment, String orderId, String postalCode) {
        String ordersTable = (postalCode != null) ? postalCode + "_orders" : "orders";

        // Order service
        // get 1 order by orderId and orderTable
        // get mass and parent
        
        // Order order = orderService.findOneOrder(orderId, ordersTable);
        // String orderParent = order.getParent();
        String orderParent = "TD_71000_2244556677";
        if(orderParent == shipment.getShipmentId()) {
            System.out.println("Order was added to shipment before.");
            return false;
        }

        // Float orderMass = order.getMass();
        Float orderMass = 30f;
        shipment.setMass(shipment.getMass() + orderMass);
    
        // Update the shipment's mass
        String updateShipmentQuery = "UPDATE " + postalCode + "_shipment SET mass = mass + ? WHERE shipment_id = ?";
        int shipmentRowsAffected = jdbcTemplate.update(updateShipmentQuery, orderMass, shipment.getShipmentId());

        if (shipmentRowsAffected == 0) {
            System.out.println("Shipment does not exist.");
            return false;
        }

        // Update the order's parent field
        String updateOrderQuery = "UPDATE " + ordersTable + " SET parent = ? WHERE order_id = ?";
        int orderRowsAffected = jdbcTemplate.update(updateOrderQuery, shipment.getShipmentId(), orderId);

        return orderRowsAffected > 0;
    }
 
    @Override
    public boolean updateParentAndDecreaseMass(Shipment shipment, String orderId, String postalCode) {
        String ordersTable = (postalCode != null) ? postalCode + "_orders" : "orders";
        String shipmentTable = (postalCode != null) ? postalCode + "_shipment" : "shipment";

        // Order service
        // get 1 order by orderId and orderTable
        // get mass and parent
        
        // Order order = orderService.findOneOrder(orderId, ordersTable);
        // String orderParent = order.getParent();
        String orderParent = "TD_71000_2244556677";
        if(orderParent == shipment.getShipmentId()) {
            System.out.println("Order was added to shipment before.");
            return false;
        }

        // Float orderMass = order.getMass();
        Float orderMass = 30f;
        shipment.setMass(shipment.getMass() + orderMass);
    
        // Update the shipment's mass
        String updateShipmentQuery = "UPDATE " + shipmentTable + " SET mass = mass - ? WHERE shipment_id = ?";
        int shipmentRowsAffected = jdbcTemplate.update(updateShipmentQuery, orderMass, shipment.getShipmentId());

        if (shipmentRowsAffected == 0) {
            System.out.println("Shipment does not exist.");
            return false;
        }

        // Update the order's parent field
        String updateOrderQuery = "UPDATE " + ordersTable + " SET parent = ? WHERE order_id = ?";
        int orderRowsAffected = jdbcTemplate.update(updateOrderQuery, null, orderId);

        return orderRowsAffected > 0;
    }

    @Override
    public Shipment getOneShipment(String shipment_id, String postalCode) {
        String shipmentTable = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        String query = "SELECT * FROM " + shipmentTable + " WHERE shipment_id = ? LIMIT 1";
        List<Shipment> shipments = jdbcTemplate.query(query,  new BeanPropertyRowMapper<>(Shipment.class), new Object[]{shipment_id});

        return shipments.isEmpty() ? null : shipments.get(0);
    }

    @Override
    public int updateStatus(String shipmentId, int status, String postalCode) {
        String shipmentTable = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        String query = "UPDATE " + shipmentTable + " SET status = ? WHERE shipment_id = ?";
        return jdbcTemplate.update(query, status, shipmentId);
    }
}
