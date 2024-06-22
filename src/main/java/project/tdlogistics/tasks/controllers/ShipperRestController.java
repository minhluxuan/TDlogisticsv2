package project.tdlogistics.tasks.controllers;

import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.tasks.entities.ListResponse;
import project.tdlogistics.tasks.entities.Order;
import project.tdlogistics.tasks.entities.Response;
import project.tdlogistics.tasks.entities.Role;
import project.tdlogistics.tasks.entities.Shipment;
import project.tdlogistics.tasks.entities.ShipperTask;
import project.tdlogistics.tasks.entities.Vehicle;
import project.tdlogistics.tasks.services.ShipperService;
import project.tdlogistics.tasks.services.VehicleService;
import project.tdlogistics.tasks.dto.CreateShipperTaskDto;
import project.tdlogistics.tasks.dto.GetHistoryDto;
import project.tdlogistics.tasks.dto.GetShipperTaskDto;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v2/tasks/shippers")
public class ShipperRestController {
    
    @Autowired
    private ShipperService shipperService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/get_objects")
    public ResponseEntity<Response<List<Map<String, Object>>>> getMethodName(@RequestHeader(name = "agencyId") String agencyId) throws Exception {
        try {
            List<Map<String, Object>> resultGettingObject = shipperService.getObjectCanHandleTask(agencyId);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Lấy các phương tiện cí thể được phân công công việc thành công",
                resultGettingObject
            ));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }     
    }
    
    @PostMapping("/create")
    public ResponseEntity<Response<ListResponse>> createNewTask (
        @RequestBody CreateShipperTaskDto payload,
        @RequestHeader(name = "userId") String staffId,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "role") Role role
    ) {
        if (!Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<ListResponse>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            String[] staffIdSubPart = staffId.split("_");
            String[] vehicleIdSubPart = payload.vehicleId.split("_");

            if (!staffIdSubPart[0].equals(vehicleIdSubPart[0]) || !staffIdSubPart[1].equals(vehicleIdSubPart[1])) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    String.format("Phương tiện %s không thuộc quyền quản lý của bưu cục/đại lý %s", payload.vehicleId, staffId),
                    null
                ));
            }

            Vehicle tempVehicle = new Vehicle();
            tempVehicle.setVehicleId(vehicleId);
            final Vehicle vehicle = vehicleService.findOneVehicle(tempVehicle);

            if (vehicle == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<ListResponse>(true, String.format("Phương tiện %s không tồn tại", payload.vehicleId), null));
            }

            Shipment shipmentCriteria = new Shipment();
            shipmentCriteria.setShipmentId(payload.shipmentId);
            shipmentCriteria.setAgencyId(agencyId);
            final Shipment shipment = shipperService.findShipment(shipmentCriteria);
            if (shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    String.format("Lô hàng %s không tồn tại trong bưu cục/đại lý %s", payload.shipmentId, agencyId),
                    null
                ));
            }

            final List<String> orderIds = shipment.getOrderIds();
            if (orderIds.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không có đơn hàng nào để phân việc",
                    null
                ));
            }

            shipmentCriteria.setStatus(3);
            final int resultUpdatingShipmentStatus = shipperService.setShipmentStatus(shipmentCriteria);
            if(resultUpdatingShipmentStatus == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "Cập nhật trạng thái lô hàng thất bại",
                    null
                ));
            }



            final String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            final ListResponse resultCreatingNewTask = shipperService.assignNewTasks(orderIds, staffId, postalCode);

            return ResponseEntity.status(HttpStatus.OK).body(new Response<ListResponse>(
                false,
                "Giao việc thành công",
                resultCreatingNewTask
            ));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }
    }
    
    @PostMapping("/get_tasks")
    public ResponseEntity<Response<List<ShipperTask>>> getTasks(
        @RequestBody GetShipperTaskDto criteria,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "role") Role userRole
    ) {
        if (!Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<ListResponse>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            if (criteria.staffId != null) {
                String[] shipperIdSubPart = criteria.staffId.split("_");
                String[] staffIdSubPart = userId.split("_");
                if(!shipperIdSubPart[0].equals(staffIdSubPart[0]) || !shipperIdSubPart[1].equals(staffIdSubPart[1])) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                        true,
                        String.format("Nhân viên %s không thuộc quyền kiểm soát của bưu cục/đại lý %s", staffId, agencyId),
                        null
                    ));
                }
            }

            final String postalCode = shipperService.getPostalCodeFromAgencyId(userId);

            if (Set.of(Role.SHIPPER).contains(userRole)) {
                criteria.staff_id = userId;
            }

            final List<ShipperTask> resultGettingTasks = shipperService.getTask(criteria, postalCode);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<ShipperTask>>(
                false,
                "Lấy công việc thành công",
                resultGettingTasks
            ));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }
    }
    
    @PatchMapping("/confirm_completed")
    public ResponseEntity<Response<ShipperTask>> confirmCompleteTask(
        @RequestParam(name = "id") Integer taskId,
        @RequestHeader(name = "userId") String staffId
    ) {
        if (!Set.of(Role.SHIPPER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<ListResponse>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }
        
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String completedTime = currentTime.format(formatter);

            String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            final boolean resultConfirmingCompleteTask = shipperService.confirmCompleteTask(taskId, staffId, completedTime, postalCode);

            if (!resultConfirmingCompleteTask) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Cập nhật công việc thất bại",
                    null
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                true,
                "Cập nhật công việc thành công",
                null
            ));

        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }                                                   
    }

    @PostMapping("/history/get")
    public ResponseEntity<Response<List<ShipperTask>>> getHistory(
        @RequestBody GetHistoryDto criteria,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId", required = false) String agencyId,
        @RequestHeader(name = "role") Role userRole
    ) {
        if (!Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.SHIPPER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<ListResponse>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            Map<String, Object> mapCriteria = new HashMap<String, Object>();

            if (criteria.staffId != null) {
                String[] shipperIdSubPart = criteria.staffId.split("_");
                String[] staffIdSubPart = userId.split("_");
                if(!shipperIdSubPart[0].equals(staffIdSubPart[0]) || !shipperIdSubPart[1].equals(staffIdSubPart[1])) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                        true,
                        String.format("Nhân viên %s không thuộc quyền kiểm soát của bưu cục/đại lý %s", staffId, agencyId),
                        null
                    ));
                }

                mapCriteria.put("staff_id", criteria.staffId);
            }

            final String postalCode = shipperService.getPostalCodeFromAgencyId(userId);
            if (Set.of(Role.SHIPPER).contains(userRole)) {
                mapCriteria.put("staff_id", userId);
            }

            final List<ShipperTask> resultGettingHistory = shipperService.getHistory(mapCriteria, postalCode);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Lấy công việc thành công",
                resultGettingHistory
            ));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<ShipperTask>> deleteTask(
        @RequestParam(name = "id") Integer id,
        @RequestHeader(name = "userId") String userId
    ) {
        if (!Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<ListResponse>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }
        
        try {
            String postalCode = shipperService.getPostalCodeFromAgencyId(userId);
            final boolean resultDeletingTask = shipperService.deleteTask(id, postalCode);

            if(!resultDeletingTask) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Xóa công việc thất bại",
                    null
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                true,
                "Xóa công việc thành công",
                null
            ));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }                                                   
    }
}
