package project.tdlogistics.orders.repositories;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.orders.entities.Order;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderRowMapper implements RowMapper<Order> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("null")
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getString("order_id"));
        order.setAgencyId(rs.getString("agency_id"));
        order.setCod(rs.getString("cod"));
        order.setCreatedAt(rs.getString(""));
        order.setDetailDest(rs.getString("detail_dest"));
        order.setDetailSource(rs.getString("detail_source"));
        order.setDistrictDest(rs.getString("district_dest"));
        order.setFee(rs.getString("fee"));
        order.setHeight(rs.getString("height"));
        order.setLastUpdate(rs.getString("last_update"));
        order.setLatDestination(rs.getString("lat_destination"));
        order.setLatSource(rs.getString("lat_source"));
        order.setLength(rs.getString("length"));
        order.setLongDestination(rs.getString("long_destination"));
        order.setLongSource(rs.getString("long_source"));
        order.setMass(rs.getString("mass"));
        order.setMiss(rs.getString("miss"));
        order.setNameReceiver(rs.getString("name_receiver"));
        order.setNameSender(rs.getString("name_sender"));
        order.setOrderCode(rs.getString("order_code"));
        order.setPaid(rs.getString("paid"));
        order.setParent(rs.getString("parent"));
        order.setPhoneNumberReceiver(rs.getString("phone_number_receiver"));
        order.setPhoneNumberSender(rs.getString("phone_number_sender"));
        order.setProvinceDest(rs.getString("province_dest"));
        order.setProvinceSource(rs.getString("province_source"));
        order.setQrcode(rs.getString("qrcode"));
        order.setReceiveImages(rs.getString("receive_images"));
        order.setReceiveSignature(rs.getString("receive_signature"));
        order.setSendImages(rs.getString("send_images"));
        order.setSendSignature(rs.getString("send_signature"));
        order.setServiceType(rs.getString("service_type"));
        order.setShipper(rs.getString("shipper"));
        order.setSignature(rs.getString("signature"));
        order.setStatusCode(rs.getString("statusCode"));
        order.setUserId(rs.getString("user_id"));
        order.setWardDest(rs.getString("ward_dest"));
        order.setWardSource(rs.getString("ward_source"));
        order.setWidth(rs.getString("width"));
        
        String journeyString = rs.getString("journey");
        System.out.println(journeyString);
        if (journeyString != null && !journeyString.isEmpty()) {
            try {
                List<Map<String, String>> journey = objectMapper.readValue(journeyString, new TypeReference<List<Map<String, String>>>() {});
                order.setJourney(journey);
            } catch (IOException e) {
                throw new SQLException("Failed to convert JSON to List<String>: " + journeyString, e);
            }
        }
        
        return order;
    }
}

