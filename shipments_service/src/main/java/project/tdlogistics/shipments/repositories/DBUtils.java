package project.tdlogistics.shipments.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DBUtils {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Map<String, Object> findOneUnion(String table, List<String> fields, List<Object> values) {
        String query;
        if (fields == null || values == null || fields.isEmpty() || values.isEmpty()) {
            query = "SELECT * FROM " + table + " LIMIT 1";
            return jdbcTemplate.queryForMap(query);
        } else {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(table).append(" WHERE ");
            for (int i = 0; i < fields.size(); i++) {
                queryBuilder.append(fields.get(i)).append(" = :").append(fields.get(i));
                if (i < fields.size() - 1) {
                    queryBuilder.append(" OR ");
                }
            }
            queryBuilder.append(" LIMIT 1");

            MapSqlParameterSource params = new MapSqlParameterSource();
            for (int i = 0; i < fields.size(); i++) {
                params.addValue(fields.get(i), values.get(i));
            }

            List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(queryBuilder.toString(), params);
            return results.isEmpty() ? null : results.get(0);
        }
    }

    public Map<String, Object> findOneIntersect(String table, List<String> fields, List<Object> values) {
        String query;
        if (fields == null || values == null || fields.isEmpty() || values.isEmpty()) {
            query = "SELECT * FROM " + table + " LIMIT 1";
            return jdbcTemplate.queryForMap(query);
        } else {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(table).append(" WHERE ");
            for (int i = 0; i < fields.size(); i++) {
                queryBuilder.append(fields.get(i)).append(" = :").append(fields.get(i));
                if (i < fields.size() - 1) {
                    queryBuilder.append(" AND ");
                }
            }
            queryBuilder.append(" LIMIT 1");

            MapSqlParameterSource params = new MapSqlParameterSource();
            for (int i = 0; i < fields.size(); i++) {
                params.addValue(fields.get(i), values.get(i));
            }

            List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(queryBuilder.toString(), params);
            return results.isEmpty() ? null : results.get(0);
        }
    }

    public int insert(String table, List<String> fields, List<Object> values) {
        if (fields == null || values == null || fields.isEmpty() || values.isEmpty()) {
            throw new IllegalArgumentException("Fields and values must not be empty");
        }

        String query = "INSERT INTO " + table + " (" +
                       String.join(", ", fields) + 
                       ") VALUES (" +
                       String.join(", ", fields.stream().map(field -> "?").toArray(String[]::new)) + 
                       ")";

        return jdbcTemplate.update(query, values.toArray());
    }

    public int update(String table, List<String> fields, List<Object> values, List<String> conditionFields, List<Object> conditionValues) {
        if (fields == null || values == null || conditionFields == null || conditionValues == null || 
            fields.isEmpty() || values.isEmpty() || conditionFields.isEmpty() || conditionValues.isEmpty()) {
            throw new IllegalArgumentException("Fields, values, conditionFields, and conditionValues must not be empty");
        }

        String setClause = String.join(", ", fields.stream().map(field -> field + " = ?").toArray(String[]::new));
        String whereClause = String.join(" AND ", conditionFields.stream().map(field -> field + " = ?").toArray(String[]::new));

        String query = "UPDATE " + table + " SET " + setClause + " WHERE " + whereClause;

        List<Object> allValues = new java.util.ArrayList<>(values);
        allValues.addAll(conditionValues);

        return jdbcTemplate.update(query, allValues.toArray());
    }

    public int updateOne(String table, List<String> fields, List<Object> values, List<String> conditionFields, List<Object> conditionValues) {
        if (fields == null || values == null || conditionFields == null || conditionValues == null || 
            fields.isEmpty() || values.isEmpty() || conditionFields.isEmpty() || conditionValues.isEmpty()) {
            throw new IllegalArgumentException("Fields, values, conditionFields, and conditionValues must not be empty");
        }

        String setClause = String.join(", ", fields.stream().map(field -> field + " = ?").toArray(String[]::new));
        String whereClause = String.join(" AND ", conditionFields.stream().map(field -> field + " = ?").toArray(String[]::new));

        String query = "UPDATE " + table + " SET " + setClause + " WHERE " + whereClause + " LIMIT 1";

        List<Object> allValues = new ArrayList<>(values);
        allValues.addAll(conditionValues);

        return jdbcTemplate.update(query, allValues.toArray());
    }

    public int deleteOne(String table, String[] fields, Object[] values) {
        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values array lengths must match.");
        }

        String whereClause = String.join(" AND ", fields);

        String query = String.format("DELETE FROM %s WHERE %s LIMIT 1", table, whereClause);

        return jdbcTemplate.update(query, values);
    }

    public int deleteMany(String table, String[] fields, Object[] values) {
        String query;
        
        if (fields != null && values != null && fields.length > 0 && values.length > 0) {
            String whereClause = String.join(" AND ", fields);
            query = String.format("DELETE FROM %s WHERE %s", table, whereClause);
        } else {
            query = String.format("DELETE FROM %s", table);
        }

        return jdbcTemplate.update(query, values);
    }
}
