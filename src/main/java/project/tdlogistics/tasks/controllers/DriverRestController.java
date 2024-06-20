package project.tdlogistics.tasks.controllers;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.tdlogistics.tasks.entities.DriverTask;
import project.tdlogistics.tasks.entities.ListResponse;
import project.tdlogistics.tasks.entities.Response;
import project.tdlogistics.tasks.entities.Role;
import project.tdlogistics.tasks.entities.Shipment;
import project.tdlogistics.tasks.entities.ShipperTask;
import project.tdlogistics.tasks.services.DriverService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/v2/drivers")
public class DriverRestController {
    
    @Autowired 
    private DriverService driverService;

    @GetMapping("/get_objects")
    public ResponseEntity<Response<List<Map<String, Object>>>> getObjectHandleTask(@RequestHeader(name = "agencyId") String agencyId,
                                                                                   @RequestHeader(name = "role") Role role) throws Exception{
        try {
            List<Map<String, Object>> resultGettingObject =  null;
            if(Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                resultGettingObject = driverService.getObjectCanHandleTaskForAdmin();
            }
            else {
                resultGettingObject = driverService.getObjectCanHandleTaskForAgency(agencyId);
            }

            if(resultGettingObject == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                    true,
                    "Lỗi truy xuất dữ liệu. Vui lòng thực hiện lại.",
                    null
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                true,
                "Lấy thông tin đối tượng thành công",
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
    
    @PostMapping("/create_tasks")
    public ResponseEntity<Response<ListResponse>> createNewTask(@RequestBody Map<String, Object> criteria, 
                                                                @RequestHeader(name = "agencyId") String agencyId,
                                                                @RequestHeader(name = "role") Role role) throws Exception {
        try {
            
            Date createdtime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String createdFormattedTime = simpleDateFormat.format(createdtime);

            //Vehicle service
            //Get one vehicle

            String vehicleId = "TD_00001_42H124768";

            String[] vehicleIdSubPart = vehicleId.split("_");
            if(!vehicleIdSubPart[0].equals("TD")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Phương tiện không thuộc quyền quản lý của tổng công ty.",
                    null
                ));
            }

            String driverId = "TD_00001_089204006686"; //get from vehicle
            String vehicleAgencyId = "TD_00001_089204006689"; //get from vehicle
            // uncomment when have vehicle rpc
            // if(driverId == null) {
            //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
            //         true,
            //         "Phương tiện có mã không được sở hữu bởi bất kỳ nhân viên nào.",
            //         null
            //     ));
            // }

            
            List<String> shipmentIds = (List<String>) criteria.get("shipmentIds");
            if (shipmentIds == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "shipmentIds not provided",
                    null
                ));
            }

            List<Map<String, String>> shipmentInfo = new ArrayList<>();
            Shipment shipmentCriteria = new Shipment();
            for(String shipmentId : shipmentIds) {
                shipmentCriteria.setShipmentId(shipmentId);
                Shipment shipment = driverService.findShipment(shipmentCriteria);
                if(shipment == null) {
                    throw new IllegalArgumentException("Lô hàng không tồn tại");
                }
                Map<String, String> shipmentKey = new HashMap<>();
                shipmentKey.put("shipmentId", shipmentId);
                shipmentKey.put("agencyId", agencyId);
                shipmentInfo.add(shipmentKey);
            }

            ListResponse resultAddingShipmentsToVehicle = driverService.addShipmentsToVehicle(shipmentIds, vehicleId, agencyId);
            ListResponse resultAssigningNewTask;
            if (Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                resultAssigningNewTask = driverService.assignNewTasks(resultAddingShipmentsToVehicle.getAcceptedArray(), driverId, vehicleId, null);
            } else {
                resultAssigningNewTask = driverService.assignNewTasks(resultAddingShipmentsToVehicle.getAcceptedArray(), driverId, vehicleId, driverService.getPostalCodeFromAgencyId(agencyId));
            }

            if (resultAssigningNewTask == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                    true,
                    "Đã xảy ra lỗi. Vui lòng thử lại.",
                    null
                ));    
            }

            List<String> notAcceptedArray = new ArrayList<>();
            notAcceptedArray.addAll(resultAddingShipmentsToVehicle.getNotAcceptedNumber(), resultAssigningNewTask.getNotAcceptedArray());
            int notAcceptedNumber = resultAddingShipmentsToVehicle.getNotAcceptedNumber() + resultAssigningNewTask.getNotAcceptedNumber();

            List<Map<String, String>> acceptedShipmentsInfo = new ArrayList<>();
            for(Map<String, String> oneShipmentInfo : shipmentInfo) {
                if(resultAddingShipmentsToVehicle.getAcceptedArray().contains(oneShipmentInfo.get("shipmentId"))) {
                    acceptedShipmentsInfo.add(oneShipmentInfo);
                }
            }

            // shipment service
            // use acceptedShipmentsInfo to update shipment journey

            for(Map<String, String> oneShipmentInfo : acceptedShipmentsInfo) {
                shipmentCriteria.setShipmentId(oneShipmentInfo.get("shipmentId"));
                Shipment resultGettingShipment = driverService.findShipment(shipmentCriteria);
                for(String orderId : resultGettingShipment.getOrderIds()) {
                    //order service
                    //set journey
                }
            }

            ListResponse resultCreatingTask = new ListResponse(resultAddingShipmentsToVehicle.getAcceptedNumber(), resultAddingShipmentsToVehicle.getAcceptedArray(), notAcceptedNumber, notAcceptedArray);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                    true,
                    "Giao việc cho tài xế thành công",
                    resultCreatingTask
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
    public ResponseEntity<Response<List<DriverTask>>> getTasks(@RequestBody Map<String, Object> criteria,
                                                                @RequestHeader(name = "staffId") String staffId,
                                                                @RequestHeader(name = "role") Role userRole) throws Exception {
        try {
            
            final String postalCode = driverService.getPostalCodeFromAgencyId(staffId);
            if(!Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(userRole)) {
                criteria.put("staffId", staffId);
            }

            final List<DriverTask> resultGettingTasks = driverService.getTasks(criteria, postalCode);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
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

    @PatchMapping("/confirm_complete")
    public ResponseEntity<Response<ShipperTask>> confirmCompleteTask(@RequestParam Map<String, String> params,
                                                                     @RequestHeader(name = "staffId") String staffId) throws Exception {               
        try {
            int idValue = -1;
            try {
                idValue = Integer.parseInt(params.get("id"));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + params.get("id"));
            }

            
            boolean resultConfirmingCompleteTask = false;
            String[] staffIdSubPart = staffId.split("_");
            if(staffIdSubPart[0].equals("TD")) {
                resultConfirmingCompleteTask = driverService.confirmCompleteTask(idValue, null);
            } 
            else {
                resultConfirmingCompleteTask = driverService.confirmCompleteTask(idValue, staffIdSubPart[1]);
            }
            

            if(!resultConfirmingCompleteTask) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Công việc đã hoàn thành trước đó hoặc không tồn tại.",
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

    @DeleteMapping("/delete")
    public ResponseEntity<Response<ShipperTask>> deleteTask(@RequestParam Map<String, String> params,
                                                            @RequestHeader(name = "userId") String userId,
                                                            @RequestHeader(name = "role") Role role) throws Exception {               
        try {
            int idValue = -1;
            try {
                idValue = Integer.parseInt(params.get("id"));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + params.get("id"));
            }

            boolean resultDeletingTask = false;
            if(Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                resultDeletingTask = driverService.deleteTask(idValue, null);
            }
            else {
                String postalCode = driverService.getPostalCodeFromAgencyId(userId);
                resultDeletingTask = driverService.deleteTask(idValue, postalCode);
            }

            

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
