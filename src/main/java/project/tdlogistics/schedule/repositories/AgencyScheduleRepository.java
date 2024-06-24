package project.tdlogistics.schedule.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import project.tdlogistics.schedule.entities.Schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AgencyScheduleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @SuppressWarnings("deprecation")
    public List<Schedule> findBeforeDeadline(String agencyCode, LocalDateTime deadline) {
        String sql = "SELECT * FROM " + agencyCode + "_schedule WHERE deadline < ?";
        Timestamp providedDeadline = Timestamp.valueOf(deadline);
        List<Schedule> result = jdbcTemplate.query(sql, new Object[] { providedDeadline }, new ScheduleRowMapper());

        return result;
    }
}