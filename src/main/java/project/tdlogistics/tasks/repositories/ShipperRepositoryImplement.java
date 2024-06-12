package project.tdlogistics.tasks.repositories;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import project.tdlogistics.tasks.entities.Order;
import project.tdlogistics.tasks.entities.ShipperTask;
import project.tdlogistics.tasks.repositories.ColumnNameMapper;

@Repository
public class ShipperRepositoryImplement implements ShipperRepositoryInterface {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    @Override
    public List<Map<String, Object>> getObjectsCanHandleTask(String agencyId) {
        String query = "SELECT v.transport_partner_id, v.agency_id, v.staff_id, v.vehicle_id, v.type, v.license_plate, " +
                       "v.max_load, v.mass, v.busy, v.created_at, v.last_update, a.agency_name, NULL AS transport_partner_name, s.fullname " +
                       "FROM vehicle AS v " +
                       "LEFT JOIN agency AS a ON v.agency_id = a.agency_id " +
                       "LEFT JOIN staff AS s ON v.staff_id = s.staff_id " +
                       "WHERE v.agency_id = ? " +
                       "ORDER BY created_at DESC;";

        return jdbcTemplate.queryForList(query, new Object[]{agencyId});
    }

    @Transactional
    @Override
    public List<ShipperTask> getTasks(Map<String, Object> criteria, String postalCode) {
        String shipperTasksTable = postalCode + "_" + "shipper_tasks";
        StringBuilder query = new StringBuilder("SELECT * FROM ").append(shipperTasksTable);
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

        return namedParameterJdbcTemplate.query(query.toString(), parameters, new BeanPropertyRowMapper<>(ShipperTask.class));
    }

    @Override
    public List<ShipperTask> getHistory(Map<String, Object> criteria, String postalCode) {
        String shipperTasksTable = postalCode + '_' + "shipper_tasks";

        String query = "SELECT * FROM " + shipperTasksTable;
        Integer option = (Integer) criteria.remove("option");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (!criteria.isEmpty()) {
            String whereClause = " WHERE " + String.join(" AND ", criteria.keySet().stream()
                    .map(key -> key + " = :" + key).toArray(String[]::new));
            query += whereClause;

            criteria.forEach(parameters::addValue);

            if (option != null) {
                switch (option) {
                    case 1:
                        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        query += " AND DATE(created_at) = :today";
                        parameters.addValue("today", today);
                        break;
                    case 2:
                        LocalDate currentDate = LocalDate.now();
                        LocalDate monday = currentDate.minusDays((currentDate.getDayOfWeek().getValue() % 7) - 1);
                        LocalDate sunday = monday.plusDays(6);
                        query += " AND DATE(created_at) > :monday AND DATE(created_at) < :sunday";
                        parameters.addValue("monday", monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        parameters.addValue("sunday", sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        break;
                    case 3:
                        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
                        LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
                        query += " AND DATE(created_at) > :firstDayOfMonth AND DATE(created_at) < :lastDayOfMonth";
                        parameters.addValue("firstDayOfMonth", firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        parameters.addValue("lastDayOfMonth", lastDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        break;
                }
            }
        } else {
            if (option != null) {
                switch (option) {
                    case 1:
                        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        query += " WHERE DATE(created_at) = :today";
                        parameters.addValue("today", today);
                        break;
                    case 2:
                        LocalDate currentDate = LocalDate.now();
                        LocalDate monday = currentDate.minusDays((currentDate.getDayOfWeek().getValue() % 7) - 1);
                        LocalDate sunday = monday.plusDays(6);
                        query += " WHERE DATE(created_at) > :monday AND DATE(created_at) < :sunday";
                        parameters.addValue("monday", monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        parameters.addValue("sunday", sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        break;
                    case 3:
                        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
                        LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
                        query += " WHERE DATE(created_at) > :firstDayOfMonth AND DATE(created_at) < :lastDayOfMonth";
                        parameters.addValue("firstDayOfMonth", firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        parameters.addValue("lastDayOfMonth", lastDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        break;
                }
            }
        }

        query += " ORDER BY created_at DESC";

        return namedParameterJdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<>(ShipperTask.class));
    }
    
}

