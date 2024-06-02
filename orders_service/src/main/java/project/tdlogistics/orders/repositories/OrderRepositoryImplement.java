package project.tdlogistics.orders.repositories;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImplement implements CustomerOrderRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int cancelOrderWithTimeConstraint(Map<String, Object> conditions) {
        // Extract fields and values from conditions
        String[] fields = conditions.keySet().toArray(new String[0]);
        Object[] values = conditions.values().toArray(new Object[0]);

        // Calculate the time 30 minutes ago
        LocalDateTime currentTime = LocalDateTime.now().minusMinutes(30);
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Create the SQL WHERE clause
        String whereClause = String.join(" AND ", Arrays.stream(fields)
                .map(field -> field + " = ?")
                .toArray(String[]::new)) + " AND order_time > ?";

        // Construct the SQL query
        String query = String.format("DELETE FROM %s WHERE %s", "orders", whereClause);

        // Execute the query
        Object[] params = Arrays.copyOf(values, values.length + 1);
        params[values.length] = formattedTime;

        return jdbcTemplate.update(query, params);
    }

    @Override
    public int cancelOrderWithoutTimeConstraint(Map<String, Object> conditions) {
        StringJoiner whereClause = new StringJoiner(" AND ");
        for(String field : conditions.keySet()) {
            whereClause.add(field + " = ?");
        }

        String query = String.format("DELETE FROM %s WHERE %s", "orders", whereClause.toString());

        Object[] values = conditions.values().toArray();

        return jdbcTemplate.update(query, values);
    }

}
