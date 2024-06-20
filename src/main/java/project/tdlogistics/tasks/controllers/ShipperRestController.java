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
import project.tdlogistics.tasks.services.ShipperService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v2/shippers")
public class ShipperRestController {
    
    @Autowired
    private ShipperService shipperService;

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
    
    @PostMapping("/create_tasks")
    public ResponseEntity<Response<ListResponse>> createNewTask(@RequestBody Map<String, String> criteria, 
                                                                @RequestHeader(name = "staffId") String staffId) throws Exception {
        try {
            
            String shipmentId = criteria.get("shipmentId");
            String vehicleId = criteria.get("vehicleId");
            String[] staffIdSubPart = staffId.split("_");
            String[] vehicleIdSubPart = vehicleId.split("_");

            if(!staffIdSubPart[0].equals(vehicleIdSubPart[0]) || !staffIdSubPart[1].equals(vehicleIdSubPart[1])) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Phương tiện không tồn tại trong bưu cục",
                    null
                ));
            }

            // const resultGettingOneVehicle = await vehicleService.getOneVehicle({ vehicle_id: req.body.vehicle_id, agency_id: req.user.agency_id });

            Shipment shipmentCriteria = new Shipment();
            shipmentCriteria.setShipmentId(shipmentId);
            shipmentCriteria.setAgencyId(criteria.get("agencyId"));
            final Shipment shipment = shipperService.findShipment(shipmentCriteria);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không tồn tại",
                    null
                ));
            }

            // const resultAddingShipmentsToVehicle = await vehicleService.addShipmentToVehicle(resultGettingOneVehicle[0], [req.body.shipment_id]);
           
            // await shipmentService.updateShipment({ status: 3 }, { shipment_id: req.body.shipment_id });
            shipmentCriteria.setStatus(3);
            final int resultUpdatingShipmentStatus = shipperService.setShipmentStatus(shipmentCriteria);
            if(resultUpdatingShipmentStatus == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "Cập nhật trạng thái lô hàng thất bại",
                    null
                ));
            }

            final List<String> orderIds = shipment.getOrderIds();
            if(orderIds.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không có đơn hàng nào để phân việc",
                    null
                ));
            }

            final String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            final ListResponse resultCreatingNewTask = shipperService.assignNewTasks(orderIds, staffId, postalCode);
            
            
            // for (const order_id of resultCreatingNewTask.acceptedArray) {
            //     let orderMessage;
            //     let orderStatus;
            //     const formattedTime = moment(new Date()).format("DD-MM-YYYY HH:mm:ss");
            //     const order = (await ordersService.getOneOrder({ order_id }))[0];
            //     if(order.status_code === servicesStatus.processing.code) {
            //         orderMessage = `${formattedTime}: Đơn hàng đang được bưu tá đến nhận`;
            //         orderStatus = servicesStatus.taking;
            //         await ordersService.setJourney(order_id, orderMessage, orderStatus);
            //     } 
            //     else if (order.status_code === servicesStatus.enter_agency.code) {
            //         orderMessage = `${formattedTime}: Đơn hàng đang được giao đến người nhận`;
            //         orderStatus = servicesStatus.delivering;
            //         await ordersService.setJourney(order_id, orderMessage, orderStatus);
            //     } 
            // }

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
    public ResponseEntity<Response<List<ShipperTask>>> getTasks(@RequestBody Map<String, Object> criteria,
                                                                @RequestHeader(name = "staffId") String staffId,
                                                                @RequestHeader(name = "role") Role userRole) throws Exception {
        try {
            if(criteria.containsKey("staffId")) {
                String[] shipperIdSubPart = ((String) criteria.get("staffId")).split("_");
                String[] staffIdSubPart = staffId.split("_");
                if(!shipperIdSubPart[0].equals(staffIdSubPart[0]) || !shipperIdSubPart[1].equals(staffIdSubPart[1])) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                        true,
                        "Nhân viên không thuộc quyền kiểm soát của bưu cục",
                        null
                    ));
                }
            }

            final String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            if(Set.of(Role.SHIPPER).contains(userRole)) {
                criteria.put("staff_id", staffId);
            }

            final List<ShipperTask> resultGettingTasks = shipperService.getTask(criteria, postalCode);
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
    
    @PatchMapping("/confirm_completed")
    public ResponseEntity<Response<ShipperTask>> confirmCompleteTask(@RequestParam Map<String, String> params,
                                                                     @RequestHeader(name = "staffId") String staffId) throws Exception {               
        try {
            int idValue = -1;
            try {
                idValue = Integer.parseInt(params.get("id"));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + params.get("id"));
            }

            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String completedTime = currentTime.format(formatter);

            String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            final boolean resultConfirmingCompleteTask = shipperService.confirmCompleteTask(idValue, staffId, completedTime, postalCode);

            if(!resultConfirmingCompleteTask) {
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

    @PostMapping("/get_history")
    public ResponseEntity<Response<List<ShipperTask>>> getHistory(@RequestBody Map<String, Object> criteria,
                                                                @RequestHeader(name = "staffId") String staffId,
                                                                @RequestHeader(name = "role") Role userRole) throws Exception {
        try {
            if(criteria.containsKey("staffId")) {
                String[] shipperIdSubPart = ((String) criteria.get("staffId")).split("_");
                String[] staffIdSubPart = staffId.split("_");
                if(!shipperIdSubPart[0].equals(staffIdSubPart[0]) || !shipperIdSubPart[1].equals(staffIdSubPart[1])) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                        true,
                        "Nhân viên không thuộc quyền kiểm soát của bưu cục",
                        null
                    ));
                }
            }

            final String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            if(Set.of(Role.SHIPPER).contains(userRole)) {
                criteria.put("staff_id", staffId);
            }

            final List<ShipperTask> resultGettingHistory = shipperService.getHistory(criteria, postalCode);
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
    public ResponseEntity<Response<ShipperTask>> deleteTask(@RequestParam Map<String, String> params,
                                                            @RequestHeader(name = "staffId") String staffId) throws Exception {               
        try {
            int idValue = -1;
            try {
                idValue = Integer.parseInt(params.get("id"));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + params.get("id"));
            }

            String postalCode = shipperService.getPostalCodeFromAgencyId(staffId);
            final boolean resultDeletingTask = shipperService.deleteTask(idValue, postalCode);

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
