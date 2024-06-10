package project.tdlogistics.shipments.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DBUtils {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public <T> T findOneUnion(String table, List<String> fields, List<Object> values, Class<T> type) {
        String query;
        if (fields == null || values == null || fields.isEmpty() || values.isEmpty()) {
            query = "SELECT * FROM " + table + " LIMIT 1";
            try {
                List<T> results = namedParameterJdbcTemplate.query(query, new BeanPropertyRowMapper<>(type));
                return results.get(0);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
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

            try {
                List<T> results = namedParameterJdbcTemplate.query(queryBuilder.toString(), params, new BeanPropertyRowMapper<>(type)); 
                return results.get(0);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }  
        }
    }

    public <T> T findOneIntersect(String table, List<String> fields, List<Object> values, Class<T> type) {
        String query;
        if (fields == null || values == null || fields.isEmpty() || values.isEmpty()) {
            query = "SELECT * FROM " + table + " LIMIT 1";
            try {
                List<T> results = namedParameterJdbcTemplate.query(query, new BeanPropertyRowMapper<>(type));
                return results.get(0);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
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

            try {
                List<T> results = namedParameterJdbcTemplate.query(queryBuilder.toString(), params, new BeanPropertyRowMapper<>(type)); 
                return results.get(0);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }  
        }
    }

    public <T> List<T> find(String table, List<String> fields, List<Object> values, boolean desc, Integer limit, Integer offset, Class<T> type) {
        String query;
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (fields != null && values != null && !fields.isEmpty() && !values.isEmpty()) {
            String whereClause = String.join(" AND ", fields.stream().map(field -> field + " = :" + field).toArray(String[]::new));
            query = "SELECT * FROM " + table + " WHERE " + whereClause;

            for (int i = 0; i < fields.size(); i++) {
                params.addValue(fields.get(i), values.get(i));
            }

            if (desc) {
                query += " ORDER BY created_at DESC";
            }

            if (offset != null && offset > 0) {
                if (limit != null && limit > 0) {
                    query += " LIMIT :offset, :limit";
                    params.addValue("offset", offset);
                    params.addValue("limit", limit);
                } else {
                    query += " LIMIT :offset, 18446744073709551615"; // MySQL's largest possible value for LIMIT
                    params.addValue("offset", offset);
                }
            } else if (limit != null && limit > 0) {
                query += " LIMIT :limit";
                params.addValue("limit", limit);
            }
        } else {
            query = "SELECT * FROM " + table;

            if (desc) {
                query += " ORDER BY created_at DESC";
            }

            if (offset != null && offset > 0) {
                if (limit != null && limit > 0) {
                    query += " LIMIT :offset, :limit";
                    params.addValue("offset", offset);
                    params.addValue("limit", limit);
                } else {
                    query += " LIMIT :offset, 18446744073709551615"; // MySQL's largest possible value for LIMIT
                    params.addValue("offset", offset);
                }
            } else if (limit != null && limit > 0) {
                query += " LIMIT :limit";
                params.addValue("limit", limit);
            }
        }

        return namedParameterJdbcTemplate.query(query, params, new BeanPropertyRowMapper<>(type));
    }

    public int insert(String table, List<String> fields, List<Object> values) {
        if (fields == null || values == null || fields.isEmpty() || values.isEmpty()) {
            throw new IllegalArgumentException("Fields and values must not be empty");
        }
    
        String query = "INSERT INTO " + table + " (" +
                       String.join(", ", fields) + 
                       ") VALUES (" +
                       String.join(", ", values.stream().map(v -> "?").toArray(String[]::new)) + 
                       ")";
        System.out.println(query);
        System.out.println(values.toString());
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

    public int deleteOne(String table, List<String> fields, List<Object> values) {
        if (fields.size() != values.size()) {
            throw new IllegalArgumentException("Fields and values list sizes must match.");
        }

        String whereClause = String.join(" AND ", fields.stream().map(field -> field + " = ?").toArray(String[]::new));

        String query = String.format("DELETE FROM %s WHERE %s LIMIT 1", table, whereClause);

        return jdbcTemplate.update(query, values.toArray());
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
