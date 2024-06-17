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

