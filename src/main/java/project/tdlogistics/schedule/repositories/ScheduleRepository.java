package project.tdlogistics.schedule.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import project.tdlogistics.schedule.entities.Schedule; // Adjust the package path as necessary

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    // Define custom query methods here

    @Query("SELECT s FROM Schedule s WHERE s.deadline > CURRENT_TIMESTAMP")
    List<Schedule> findSchedulesBeforeDeadline();

    @Query("SELECT s FROM Schedule s WHERE s.deadline < ?1")
    List<Schedule> findSchedulesBeforeDeadline(LocalDateTime deadline);
}