package project.tdlogistics.schedule.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.tdlogistics.schedule.configurations.Ultils;
import project.tdlogistics.schedule.entities.Role;
import project.tdlogistics.schedule.entities.Schedule;
import project.tdlogistics.schedule.entities.placeholder.Response;
import project.tdlogistics.schedule.services.ScheduleService;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    private final Ultils utils;

    // private final ScheduleRepository scheduleRepository;

    // private final AgencyScheduleRepository agencyScheduleRepository;

    @Autowired
    public ScheduleRestController(ScheduleService scheduleService, Ultils utils) {
        this.scheduleService = scheduleService;
        this.utils = utils;
        // this.scheduleRepository = scheduleRepository;
        // this.agencyScheduleRepository = agencyScheduleRepository;
    }

    // @GetMapping("/test")
    // public ResponseEntity<List<Schedule>> test() {

    // String str = "2024-06-24 10:43:01";
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
    // HH:mm:ss");
    // LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    // LocalDateTime deadline = LocalDateTime.of(2024, 12, 11, 3, 2, 1);
    // LocalDateTime findDeadline = LocalDateTime.of(2025, 11, 11, 3, 2, 1);

    // String agencyCode = "71000";
    // Integer Id = 133391225;
    // Schedule testObject = new Schedule();
    // // testObject.setId(Math.abs(UUID.randomUUID().hashCode()));
    // // testObject.setTask("Lấy hàng tại 136 Bùi Thị Thiêm");
    // testObject.setPriority(1);
    // // testObject.setCompleted(false);
    // testObject.setDeadline(findDeadline);
    // // testObject.setCompletedAt(null);
    // // testObject.setCreatedAt(dateTime);
    // // testObject.setLastUpdate(LocalDateTime.now());

    // try {

    // // List<Schedule> result = scheduleService.getScheduleForAdmin(testObject);
    // // List<Schedule> findByRepo =
    // // scheduleRepository.findSchedulesBeforeDeadline(findDeadline);
    // // System.out.println(result);
    // // scheduleService.createNewScheduleInAgency(testObject, agencyCode);
    // List<Schedule> findByRepo = scheduleService.getScheduleForAgency(testObject,
    // agencyCode);
    // System.out.println(findByRepo);
    // // scheduleService.CreateScheduleForAdmin(testObject);
    // // scheduleService.updateScheduleForAgency(Id, testObject, agencyCode);
    // // scheduleService.deleteScheduleForAgency(Id, agencyCode);

    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return null;
    // }
    @PostMapping("/create")
    public ResponseEntity<Response<Schedule>> createSchedule(@RequestBody Schedule schedule,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "staffId", required = false) String staffId) {

        try {

            Set<Role> adminRoles = Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER,
                    Role.COMPLAINTS_SOLVER);
            Set<Role> agencyRoles = Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER,
                    Role.AGENCY_COMPLAINTS_SOLVER);

            if (adminRoles.contains(role)) {
                Schedule scheduleToCreate = new Schedule();
                scheduleToCreate.setTask(schedule.getTask());
                scheduleToCreate.setPriority(schedule.getPriority());
                scheduleToCreate.setDeadline(schedule.getDeadline());

                Schedule resultCreateSchedule = scheduleService.CreateScheduleForAdmin(schedule);

                if (resultCreateSchedule == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response<>(true, "Tạo công việc mới thất bại.", null));
                }

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new Response<>(false, "Tạo công việc mới thành công.", null));

            } else if (agencyRoles.contains(role)) {
                String postalCode = utils.getPostalCodeFromAgencyID(staffId);
                if (postalCode == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new Response<>(true, "Không tìm thấy mã bưu cục của đơn vị.", null));
                }
                Schedule scheduleToCreate = new Schedule();
                scheduleToCreate.setTask(schedule.getTask());
                scheduleToCreate.setPriority(schedule.getPriority());
                scheduleToCreate.setDeadline(schedule.getDeadline());

                Schedule resultCreateSchedule = scheduleService.createNewScheduleForAgency(scheduleToCreate,
                        postalCode);

                if (resultCreateSchedule == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response<>(true, "Tạo công việc mới thất bại.", null));
                }

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new Response<>(false, "Tạo công việc mới thành công.", resultCreateSchedule));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Lỗi hệ thống.", null));
        }
        // scheduleService.CreateScheduleForAdmin(schedule);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(true, "Lỗi hệ thống.", null));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<List<Schedule>>> getSchedule(@RequestBody Schedule schedule,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "staffId", required = false) String staffId) {

        try {
            Set<Role> adminRoles = Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER,
                    Role.COMPLAINTS_SOLVER);
            Set<Role> agencyRoles = Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER,
                    Role.AGENCY_COMPLAINTS_SOLVER);

            if (adminRoles.contains(role)) {
                List<Schedule> resultGetSchedule = scheduleService.getScheduleForAdmin(schedule);

                if (resultGetSchedule == null || resultGetSchedule.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy thông tin công việc thành công.", resultGetSchedule));
            } else if (agencyRoles.contains(role)) {
                String postalCode = utils.getPostalCodeFromAgencyID(staffId);
                if (postalCode == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new Response<>(true, "Không tìm thấy mã bưu cục của đơn vị.", null));
                }

                List<Schedule> resultGetSchedule = scheduleService.getScheduleForAgency(schedule, postalCode);

                if (resultGetSchedule == null || resultGetSchedule.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy thông tin công việc thành công.", resultGetSchedule));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Lỗi hệ thống.", null));
        }
        return null;
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Schedule>> updateSchedule(@RequestBody Schedule schedule,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "staffId", required = false) String staffId,
            @RequestHeader(value = "id", required = false) Integer scheduleId) {
        try {

            Set<Role> adminRoles = Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER,
                    Role.COMPLAINTS_SOLVER);
            Set<Role> agencyRoles = Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER,
                    Role.AGENCY_COMPLAINTS_SOLVER);

            if (adminRoles.contains(role)) {

                Schedule tempSchedule = new Schedule();
                tempSchedule.setId(scheduleId);

                Schedule resultGetOneSchedule = scheduleService.getScheduleForAdmin(tempSchedule).get(0);

                if (resultGetOneSchedule == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                if (resultGetOneSchedule.getCompleted()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response<>(true, "Công việc đã hoàn thành, không thể cập nhật.", null));
                }

                Schedule resultUpdateSchedule = scheduleService.updateScheduleForAdmin(scheduleId, schedule);

                if (resultUpdateSchedule == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response<>(true, "Cập nhật công việc thất bại.", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Cập nhật công việc thành công.", resultUpdateSchedule));
            } else if (agencyRoles.contains(role)) {
                String postalCode = utils.getPostalCodeFromAgencyID(staffId);
                if (postalCode == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new Response<>(true, "Không tìm thấy mã bưu cục của đơn vị.", null));
                }

                Schedule tempSchedule = new Schedule();
                tempSchedule.setId(scheduleId);

                Schedule resultGetOneSchedule = scheduleService.getScheduleForAgency(tempSchedule, postalCode).get(0);

                if (resultGetOneSchedule == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                if (resultGetOneSchedule.getCompleted()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response<>(true, "Công việc đã hoàn thành, không thể cập nhật.", null));
                }

                // cast to string

                // String scheduleIdString = String.valueOf(scheduleId);

                Schedule resultUpdateSchedule = scheduleService.updateScheduleForAgency(
                        scheduleId, schedule,
                        postalCode);

                if (resultUpdateSchedule == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response<>(true, "Cập nhật công việc thất bại.", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Cập nhật công việc thành công.", resultUpdateSchedule));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Lỗi hệ thống.", null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(true, "Lỗi hệ thống.", null));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Schedule>> deleteSchedule(@RequestBody Schedule schedule,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "staffId", required = false) String staffId,
            @RequestHeader(value = "id", required = false) Integer scheduleId) {
        try {

            // role
            Set<Role> adminRoles = Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER,
                    Role.COMPLAINTS_SOLVER);
            Set<Role> agencyRoles = Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER,
                    Role.AGENCY_COMPLAINTS_SOLVER);

            if (adminRoles.contains(role)) {

                Schedule resultDeleteSchedule = scheduleService.deleteScheduleForAdmin(scheduleId);

                if (resultDeleteSchedule == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Xóa công việc thành công.", resultDeleteSchedule));
            } else if (agencyRoles.contains(role)) {
                String postalCode = utils.getPostalCodeFromAgencyID(staffId);
                if (postalCode == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new Response<>(true, "Không tìm thấy mã bưu cục của đơn vị.", null));
                }

                Schedule tempSchedule = new Schedule();
                tempSchedule.setId(scheduleId);

                Schedule resultGetOneSchedule = scheduleService.getScheduleForAdmin(tempSchedule).get(0);

                if (resultGetOneSchedule == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                Schedule resultDeleteSchedule = scheduleService.deleteScheduleForAgency(scheduleId, postalCode);

                if (resultDeleteSchedule == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy công việc.", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Xóa công việc thành công.", resultDeleteSchedule));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Lỗi hệ thống.", null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(true, "Lỗi hệ thống.", null));
    }

}