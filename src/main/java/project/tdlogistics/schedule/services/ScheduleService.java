package project.tdlogistics.schedule.services;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import project.tdlogistics.schedule.configurations.MyBeanUtils;
import project.tdlogistics.schedule.entities.Schedule;
import project.tdlogistics.schedule.entities.placeholder.Response;
import project.tdlogistics.schedule.repositories.AgencyScheduleRepository;
import project.tdlogistics.schedule.repositories.DBUtils;
import project.tdlogistics.schedule.repositories.ScheduleColumnNameMapper;
import project.tdlogistics.schedule.repositories.ScheduleRepository;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DBUtils dbUtils;
    private final AgencyScheduleRepository agencyScheduleRepository;

    // constructor
    public ScheduleService(ScheduleRepository scheduleRepository, DBUtils dbUtils,
            AgencyScheduleRepository agencyScheduleRepository) {
        this.scheduleRepository = scheduleRepository;
        this.dbUtils = dbUtils;
        this.agencyScheduleRepository = agencyScheduleRepository;

    }

    public Schedule CreateScheduleForAdmin(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule createNewScheduleForAgency(Schedule info, String agencyCode) throws Exception {

        if (info == null) {
            throw new Exception("Schedule info is null");
        }
        if (agencyCode == null) {
            throw new Exception("Agency code is null");
        }

        String schedulesTable = agencyCode + "_schedule";
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : Schedule.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(info);

            fields.add(ScheduleColumnNameMapper.mappingColumn(field.getName()));
            values.add(value);

        }

        boolean resultInsert = dbUtils.insert(schedulesTable, fields, values) > 0;

        if (resultInsert) {
            return info;
        } else {
            return null;
        }
    }

    public List<Schedule> getScheduleForAdmin(Schedule criteria) {
        // List<Schedule> combinedResults = new ArrayList<>();

        // Find by deadline if it exists
        List<Schedule> resultFindByDeadline = new ArrayList<>();
        if (criteria.getDeadline() != null) {
            resultFindByDeadline = scheduleRepository
                    .findSchedulesBeforeDeadline(criteria.getDeadline());
            // combinedResults.addAll(resultFindByDeadline);
        }

        // Find bycriteria excluding deadline
        criteria.setDeadline(null);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Schedule> example = Example.of(criteria, matcher);
        List<Schedule> resultFindByCriteria = scheduleRepository.findAll(example);

        System.out.println("resultFindByCriteria: " + resultFindByCriteria);
        System.out.println("resultFindByDeadline: " + resultFindByDeadline);

        // Combine results
        if (resultFindByDeadline.isEmpty() || resultFindByDeadline.size() == 0) {
            return resultFindByCriteria;
        } else {
            resultFindByCriteria.retainAll(resultFindByDeadline);
            return resultFindByCriteria;
        }
    }

    // @SuppressWarnings("unlikely-arg-type")
    public List<Schedule> getScheduleForAgency(Schedule criteria, String agencyCode) throws Exception {
        if (agencyCode == null) { // unexpected behavior
            throw new Exception("Agency code is null");
        }
        if (criteria == null) {
            throw new Exception("Criteria is null");
        }

        List<Schedule> resultFindByDeadline = new ArrayList<>();
        if (criteria.getDeadline() != null) {
            resultFindByDeadline = new ArrayList<>();
            agencyScheduleRepository.findBeforeDeadline(agencyCode, criteria.getDeadline());
        }

        String schedulesTable = agencyCode + "_schedule";
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : Schedule.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(criteria);
            if (value != null && !field.getName().equals("deadline")) {
                fields.add(ScheduleColumnNameMapper.mappingColumn(field.getName()));
                values.add(value);
            }
        }

        System.out.println(fields);
        System.out.println(values);
        List<Schedule> resultFindByCriteria = dbUtils.find(schedulesTable, fields, values, true, null, null,
                Schedule.class);

        if (resultFindByDeadline.isEmpty() || resultFindByDeadline.size() == 0) {
            return resultFindByCriteria;
        } else {
            resultFindByCriteria.retainAll(resultFindByDeadline);
            return resultFindByCriteria;
        }
    }

    public Schedule updateScheduleForAdmin(Integer id, Schedule info) throws Exception {
        Optional<Schedule> existingSchedule = scheduleRepository.findById(id);
        if (existingSchedule.isEmpty()) {
            throw new IllegalArgumentException("Schedule not found");
        }

        Schedule scheduleToUpdate = existingSchedule.get();
        MyBeanUtils.copyNonNullProperties(info, scheduleToUpdate);
        scheduleToUpdate.setLastUpdate(LocalDateTime.now());
        return scheduleRepository.save(scheduleToUpdate);
    }

    public Schedule updateScheduleForAgency(Integer id, Schedule info, String agencyCode) throws Exception {
        if (agencyCode == null) {
            throw new Exception("Agency code is null");
        }
        info.setLastUpdate(LocalDateTime.now());
        String schedulesTable = agencyCode + "_schedule";
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : Schedule.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(info);
            if (value != null) {
                fields.add(ScheduleColumnNameMapper.mappingColumn(field.getName()));
                values.add(value);
            }

        }
        boolean resultUpdate = dbUtils.update(schedulesTable, fields, values, List.of("id"), List.of(id)) > 0;

        if (resultUpdate) {
            return info;
        } else {
            return null;
        }
    }

    public Schedule deleteScheduleForAdmin(Integer id) throws Exception {
        Schedule tempSchedule = new Schedule();
        tempSchedule.setId(id);

        Schedule resultGetOneSchedule = getScheduleForAdmin(tempSchedule).get(0);

        if (resultGetOneSchedule == null) {
            throw new IllegalArgumentException("Schedule not found");
        } else {
            scheduleRepository.deleteById(id);
            return resultGetOneSchedule;
        }

        // scheduleRepository.deleteById(id);
    }

    public Schedule deleteScheduleForAgency(Integer id, String agencyCode) throws Exception {
        if (agencyCode == null || id == null) {
            throw new Exception("Null value not allowed");
        }

        Schedule tempSchedule = new Schedule();
        tempSchedule.setId(id);

        Schedule resultGetOneSchedule = getScheduleForAgency(tempSchedule, agencyCode).get(0);

        if (resultGetOneSchedule == null) {
            throw new IllegalArgumentException("Schedule not found");
        }

        String schedulesTable = agencyCode + "_schedule";
        boolean resultDelte = dbUtils.deleteById(schedulesTable, id) > 0;

        if (resultDelte) {
            return tempSchedule;
        } else {
            return null;
        }
    }

}
