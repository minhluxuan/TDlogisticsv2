package project.tdlogistics.tasks.repositories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import project.tdlogistics.tasks.entities.DriverTask;

@Repository
public class DriverRepositoryImplement implements DriverRepositoryInterface {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Map<String, Object>> getObjectsCanHandleTaskByAdmin() {
        String query = "SELECT v.transport_partner_id, v.agency_id, v.staff_id, v.vehicle_id, v.type, v.license_plate, " +
                "v.max_load, v.mass, v.busy, v.created_at, v.last_update, a.agency_name, t.transport_partner_name, p.fullname " +
                "FROM vehicle AS v " +
                "LEFT JOIN agency AS a ON v.agency_id = a.agency_id " +
                "LEFT JOIN transport_partner AS t ON v.transport_partner_id = t.transport_partner_id " +
                "LEFT JOIN partner_staff AS p ON v.staff_id = p.staff_id " +
                "WHERE v.agency_id LIKE 'TD%' " +
                "ORDER BY v.created_at DESC";

        return jdbcTemplate.queryForList(query);
    }

    public List<Map<String, Object>> getObjectsCanHandleTaskByAgency(String agencyId) {
        String query = "SELECT v.transport_partner_id, v.agency_id, v.staff_id, v.vehicle_id, v.type, v.license_plate, " +
                "v.max_load, v.mass, v.busy, v.created_at, v.last_update, a.agency_name, t.transport_partner_name, p.fullname " +
                "FROM vehicle AS v " +
                "LEFT JOIN agency AS a ON v.agency_id = a.agency_id " +
                "LEFT JOIN transport_partner AS t ON v.transport_partner_id = t.transport_partner_id " +
                "LEFT JOIN partner_staff AS p ON v.staff_id = p.staff_id " +
                "WHERE v.agency_id = ? " +
                "ORDER BY v.created_at DESC";
    
        return jdbcTemplate.queryForList(query, agencyId);
    }

    public List<DriverTask> getTasks(Map<String, Object> criteria, String postalCode) {
        String driverTasksTable = (postalCode == null) ? "driver_tasks" : (postalCode + "_driver_tasks");
        StringBuilder query = new StringBuilder("SELECT * FROM ").append(driverTasksTable);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        Integer option = (Integer) criteria.remove("option");

        if (!criteria.isEmpty()) {
            query.append(" WHERE ");
            String whereClause = criteria.entrySet().stream()
                    .map(entry -> entry.getKey() + " = :" + entry.getKey())
                    .collect(Collectors.joining(" AND "));
            query.append(whereClause);

            criteria.forEach(parameters::addValue);
        }

        if (option != null) {
            if (criteria.isEmpty()) {
                query.append(" WHERE ");
            } else {
                query.append(" AND ");
            }

            LocalDate today = LocalDate.now();
            switch (option) {
                case 0:
                    query.append("completed = false");
                    break;
                case 1:
                    query.append("DATE(created_at) = :today AND completed = false");
                    parameters.addValue("today", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                case 2:
                    LocalDate mondayOfTheWeek = today.with(java.time.DayOfWeek.MONDAY);
                    LocalDate sundayOfTheWeek = today.with(java.time.DayOfWeek.SUNDAY);
                    query.append("DATE(created_at) >= :monday AND DATE(created_at) <= :sunday AND completed = false");
                    parameters.addValue("monday", mondayOfTheWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    parameters.addValue("sunday", sundayOfTheWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid option value: " + option);
            }
        }

        query.append(" ORDER BY created_at DESC");

        return namedParameterJdbcTemplate.query(query.toString(), parameters, new BeanPropertyRowMapper<>(DriverTask.class));
    }

    
}
