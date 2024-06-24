package project.tdlogistics.schedule.repositories;

import org.springframework.jdbc.core.RowMapper;

import io.micrometer.common.lang.NonNull;
import project.tdlogistics.schedule.entities.Schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ScheduleRowMapper implements RowMapper<Schedule> {

    @Override
    public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        Schedule schedule = new Schedule();

        schedule.setId(rs.getInt("id"));
        schedule.setTask(rs.getString("task"));
        schedule.setPriority(rs.getInt("priority"));
        schedule.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        schedule.setCompletedAt(rs.getObject("completed_at", LocalDateTime.class));
        schedule.setDeadline(rs.getObject("deadline", LocalDateTime.class));
        schedule.setCompleted(rs.getBoolean("completed"));
        schedule.setLastUpdate(rs.getObject("last_update", LocalDateTime.class));

        return schedule;
    }
}