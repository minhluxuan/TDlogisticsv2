package project.tdlogistics.orders.repositories;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;

import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.repositories.ColumnNameMapper;

@Repository
public class OrderRepositoryImplement implements OrderRepositoryInterface {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
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
                .map(field -> ColumnNameMapper.mappingColumn(field) + " = ?")
                .toArray(String[]::new)) + " AND created_at > ?";

        // Construct the SQL query
        String query = String.format("DELETE FROM %s WHERE %s", "orders", whereClause);

        // Execute the query
        Object[] params = Arrays.copyOf(values, values.length + 1);
        params[values.length] = formattedTime;

        System.out.println("Params: " + Arrays.toString(params));
        // System.out.println(conditions.toString());

        return jdbcTemplate.update(query, params);
    }

    @Transactional
    @Override
    public int cancelOrderWithoutTimeConstraint(Map<String, Object> conditions) {
        StringJoiner whereClause = new StringJoiner(" AND ");
        for(String field : conditions.keySet()) {
            whereClause.add(ColumnNameMapper.mappingColumn(field) + " = ?");
        }

        String query = String.format("DELETE FROM %s WHERE %s", "orders", whereClause.toString());

        Object[] values = conditions.values().toArray();

        return jdbcTemplate.update(query, values);
    }

}




// @Repository
// @Transactional
// public class OrderRepositoryImplement implements OrderRepositoryInterface {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public int cancelOrderWithTimeConstraint(Map<String, Object> conditions) {
//         // Extract fields and values from conditions
//         String[] fields = conditions.keySet().toArray(new String[0]);
//         Object[] values = conditions.values().toArray(new Object[0]);

//         // Calculate the time 30 minutes ago
//         LocalDateTime currentTime = LocalDateTime.now().minusMinutes(30);
//         String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//         // Create the SQL WHERE clause
//         String whereClause = String.join(" AND ", Arrays.stream(fields)
//                 .map(field -> field + " = ?")
//                 .toArray(String[]::new)) + " AND order_time > ?";

//         // Construct the SQL query
//         String query = String.format("DELETE FROM %s WHERE %s", "orders", whereClause);

//         // Execute the query
//         Query entityManagerQuery = entityManager.createNativeQuery(query);
//         for (int i = 0; i < values.length; i++) {
//             entityManagerQuery.setParameter(i + 1, values[i]);
//         }
//         entityManagerQuery.setParameter(values.length + 1, formattedTime);
        
//         return entityManagerQuery.executeUpdate();
//     }

//     @Override
//     public int cancelOrderWithoutTimeConstraint(Map<String, Object> conditions) {
//         StringJoiner whereClause = new StringJoiner(" AND ");
//         for (String field : conditions.keySet()) {
//             whereClause.add(field + " = :" + field);
//         }

//         // Construct the SQL query
//         String query = String.format("DELETE FROM %s WHERE %s", "orders", whereClause.toString());

//         // Execute the query
//         Query entityManagerQuery = entityManager.createNativeQuery(query);
//         for (Map.Entry<String, Object> entry : conditions.entrySet()) {
//             entityManagerQuery.setParameter(entry.getKey(), entry.getValue());
//         }
        
//         return entityManagerQuery.executeUpdate();
//     }
// }

