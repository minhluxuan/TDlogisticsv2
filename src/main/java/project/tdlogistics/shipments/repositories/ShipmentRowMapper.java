package project.tdlogistics.shipments.repositories;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.shipments.entities.Order;
import project.tdlogistics.shipments.entities.Shipment;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ShipmentRowMapper implements RowMapper<Shipment> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("null")
    @Override
    public Shipment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Shipment shipment = new Shipment();
        

        shipment.setShipmentId(rs.getString("shipment_id"));
        shipment.setAgencyId(rs.getString("agency_id"));
        shipment.setAgencyIdDest(rs.getString("agency_id_dest"));
        shipment.setLongSource(rs.getFloat("long_source"));
        shipment.setLatSource(rs.getFloat("lat_source"));
        shipment.setCurrentAgencyId(rs.getString("current_agency_id"));
        shipment.setCurrentLat(rs.getFloat("current_lat"));
        shipment.setCurrentLong(rs.getFloat("current_long"));
        shipment.setLongDestination(rs.getFloat("long_destination"));
        shipment.setLatDestination(rs.getFloat("lat_destination"));
        shipment.setTransportPartnerId(rs.getString("transport_partner_id"));
        shipment.setStaffId(rs.getString("staff_id"));
        shipment.setVehicleId(rs.getString("vehicle_id"));
        shipment.setMass(rs.getFloat("mass"));
        
        // Convert JSON string to List<String> for orderIds
        String orderIdsString = rs.getString("order_ids");
        if (orderIdsString != null && !orderIdsString.isEmpty()) {
            try {
                List<String> orderIds = objectMapper.readValue(orderIdsString, new TypeReference<List<String>>() {});
                shipment.setOrderIds(orderIds);
            } catch (IOException e) {
                throw new SQLException("Failed to convert JSON to List<String>: " + orderIdsString, e);
            }
        }

        shipment.setParent(rs.getString("parent"));
        shipment.setStatus(rs.getInt("status"));
        shipment.setCreatedAt(rs.getTimestamp("created_at"));
        shipment.setLastUpdate(rs.getTimestamp("last_update"));

        String journeyString = rs.getString("journey");
        System.out.println(journeyString);
        if (journeyString != null && !journeyString.isEmpty()) {
            try {
                List<Map<String, String>> journey = objectMapper.readValue(journeyString, new TypeReference<List<Map<String, String>>>() {});
                shipment.setJourney(journey);
            } catch (IOException e) {
                throw new SQLException("Failed to convert JSON to List<<Map<String, String>>: " + journeyString, e);
            }
        }
        
        return shipment;
    }
}

