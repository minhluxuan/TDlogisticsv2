package project.tdlogistics.shipments.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import project.tdlogistics.shipments.entities.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import project.tdlogistics.shipments.entities.Shipment;
import project.tdlogistics.shipments.entities.Response;
import project.tdlogistics.shipments.entities.Agency;
import project.tdlogistics.shipments.entities.ListResponse;
import project.tdlogistics.shipments.entities.Request;
import project.tdlogistics.shipments.services.ShipmentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.amqp.core.AmqpTemplate;


@RestController
@RequestMapping("/v2/shipments")
public class ShipmentRestController {
    @Autowired
    private ShipmentService shipmentService;
    
    @PostMapping(value = "/create")
    public ResponseEntity<Response<Shipment>> createShipment
        (@RequestBody Shipment info,
        @RequestHeader(value = "userId") String userId,
        @RequestHeader(value = "agencyId") String agencyId,
        @RequestHeader(value = "role") Role role) throws Exception {
        try {

            info.setAgencyId(agencyId);
            final Shipment result = shipmentService.createNewShipment(info, role);
            if(result == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(false, "Tạo lô hàng mới thành công", result));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, e.getMessage(), null));
        }
    }
    
    @PostMapping("/check")
    public ResponseEntity<Response<Shipment>> checkExistShipment(@RequestBody Shipment criteria) throws Exception {
        try {
            final Optional<Shipment> ShipmentOptional = shipmentService.checkExistShipment(criteria);
            if (ShipmentOptional.isPresent()) {
                final Response response = new Response<>(false, "Lô hàng đã tồn tại.", ShipmentOptional.get());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(false, "Lô hàng không tồn tại.", null));

            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
    
    @PostMapping("/add_orders")
    public ResponseEntity<Response<ListResponse>> addOrdersToShipment
        (@RequestParam Map<String, String> queryParams,
        @RequestBody Shipment info,
        @RequestHeader(value = "agencyId") String agencyId,
        @RequestHeader(value = "userId") String userId,
        @RequestHeader(value = "role") Role role) {
        try {

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(role)) {     
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipmentId"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                

                // Shipment result = shipmentService.addOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
                ListResponse resultAddingOrders = shipmentService.addOrders(shipment, info.getOrderIds(), postalCode);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Thêm đơn hàng vào lô thành công", 
                    resultAddingOrders
                ));
            } else if (Set.of(Role.MANAGER, Role.TELLER).contains(role)) {
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                
                shipmentService.addOrders(shipment, info.getOrderIds(), postalCode);
                ListResponse resultAddingOrders = shipmentService.addOrders(shipment, info.getOrderIds(), null);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Thêm đơn hàng vào lô thành công", 
                    resultAddingOrders
                ));
            }

            String postalCode = queryParams.get("shipment_id").split("_")[1];
            Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true, 
                    "Không tìm thấy lô hàng.", 
                    null
                ));
            }
            ListResponse resultAddingOrders = shipmentService.addOrders(shipment, info.getOrderIds(), postalCode);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                false, 
                "Thêm đơn hàng vào lô thành công", 
                resultAddingOrders
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }

    }
   
    @PostMapping("/remove_orders")
    public ResponseEntity<Response<ListResponse>> removeOrdersFromShipment
        (@RequestParam Map<String, String> queryParams,
        @RequestBody Shipment info,
        @RequestHeader(value = "agencyId") String agencyId,
        @RequestHeader(value = "userId") String userId,
        @RequestHeader(value = "role") Role role) {
        try {

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(role)) {
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipmentId"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                

                // Shipment result = shipmentService.addOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
                ListResponse resultRemoveOrders = shipmentService.removeOrders(shipment, info.getOrderIds(), postalCode);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Xóa đơn hàng vào lô thành công", 
                    resultRemoveOrders
                ));
            } else if (Set.of(Role.MANAGER, Role.TELLER).contains(role)) {
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                shipmentService.removeOrders(shipment, info.getOrderIds(), postalCode);
                ListResponse resultRemoveOrders = shipmentService.removeOrders(shipment, info.getOrderIds(), null);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Xóa đơn hàng vào lô thành công", 
                    resultRemoveOrders
                ));
            }

            String postalCode = queryParams.get("shipment_id").split("_")[1];
            Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true, 
                    "Không tìm thấy lô hàng.", 
                    null
                ));
            }
            ListResponse resultRemoveOrders = shipmentService.removeOrders(shipment, info.getOrderIds(), postalCode);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                false, 
                "Xóa đơn hàng vào lô thành công", 
                resultRemoveOrders
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }

    }
    
    @PostMapping("/undertake")
    public ResponseEntity<Response<List<Object>>> undertakeShipment(@RequestBody Shipment info) throws Exception {
        try {
            
            final String staffId = "TD_71000_089204006685";
            final String postalCode = shipmentService.getPostalCodeFromAgencyId(staffId);
            Shipment shipment = shipmentService.getOneShipment(info.getShipmentId(), postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true, 
                    "Không tìm thấy lô hàng.", 
                    null
                ));
            }

            final boolean resultAddingOneShipment = shipmentService.addOneShipmentToVehicle(shipment.getShipmentId(), staffId);
            if(!resultAddingOneShipment) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true, 
                    "Nhân viên không có phương tiện nào để có thể tiếp nhận lô hàng này.", 
                    null
                ));
            }

            ListResponse resultUpdatingOrders = shipmentService.updateOrders(info.getOrderIds(), staffId, postalCode);
            ListResponse resultAssigningTask = shipmentService.assignTaskToShipper(info.getOrderIds(), staffId, postalCode);

            List<Object> result = new ArrayList<>();
            result.add(resultUpdatingOrders);
            result.add(resultAssigningTask);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                false, 
                "Thêm đơn hàng vào lô thành công", 
                result
            ));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }                
    }
    
    @PostMapping("/update_journey")
    public ResponseEntity<Response<Shipment>> updateJourney(@RequestParam Map<String, String> queryParams, @RequestBody Map<String, String> body) {
        try {
            Date createdTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formattedTime = formatter.format(createdTime);

            String shipmentId = queryParams.get("shipment_id");
            if (shipmentId == null || shipmentId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "Thiếu thông tin lô hàng.",
                    null
                ));
            }

            String message = body.get("message");
            if (message == null || message.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "Thiếu thông hành trình.",
                    null
                ));
            }
            
            final boolean resultUpdatingJourney = shipmentService.setJourney(shipmentId, formattedTime, message, null);
            if (!resultUpdatingJourney) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "Đã xảy ra lỗi. Vui lòng thử lại.",
                    null
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Cập nhật hành trình thành công!",
                null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                "Đã xảy ra lỗi. Vui lòng thử lại.",
                null
            ));
        }
    }

    @PostMapping("/get_journey")
    public ResponseEntity<Response<List<Map<String, String>>>> getJourney(@RequestParam Map<String, String> queryParams) {
        try {

            String shipmentId = queryParams.get("shipment_id");
            List<Map<String, String>> journey = shipmentService.getJourney(shipmentId);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<> (
                false,
                "Lấy thông tin hành trình thành công!",
                journey
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                "Đã xảy ra lỗi. Vui lòng thử lại.",
                null
            ));
        }
    }

    @PostMapping("/decompose")
    public ResponseEntity<Response<ListResponse>> decomposeShipment
        (@RequestParam Map<String, String> queryParams, 
        @RequestBody Map<String, Object> body,
        @RequestHeader(value = "agencyId") String agencyId,
        @RequestHeader(value = "userId") String userId,
        @RequestHeader(value = "role") Role role) {
        try {
            Date createdTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formattedTime = formatter.format(createdTime);
            final String shipmentId = queryParams.get("shipmentId");
            final String postalCode = shipmentService.getPostalCodeFromAgencyId(userId);
            if(Set.of(Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(role)) {
                if(!shipmentService.checkExistShipment(shipmentId, postalCode)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true,
                        "Lô hàng không tồn tại trong bưu cục",
                        null
                    ));
                }
            } 
            else if(Set.of(Role.MANAGER, Role.TELLER).contains(role)) {
                if(!shipmentService.checkExistShipment(shipmentId, postalCode)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true,
                        "Lô hàng không tồn tại trong trung tâm chia chọn.",
                        null
                    ));
                }
            }

            final Shipment shipment = shipmentService.getOneShipment(shipmentId, postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true,
                        "Lô hàng không tồn tại.",
                        null
                    ));
            }
            int shipmentStatus = shipment.getStatus();

            if(shipmentStatus < 5) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Lô hàng không thể được phân rã vào thời điểm này.\nYêu cầu trạng thái trước đó phải là \"Đã đến bưu cục đích\"",
                    null
                ));
            }

            if(shipmentStatus >= 6) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Lô hàng đã được phân rã từ trước.",
                    null
                ));
            }

            List<String> shipmentOrderIds = shipment.getOrderIds();
            if(shipmentOrderIds.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không có đơn hàng nào để phân rã.",
                    null
                )); 
            }

            
            Object requestOrderIdsObject = body.get("orderIds");
            List<String> requestOrderIds = new ArrayList<>();
            if(requestOrderIdsObject instanceof List<?>) {
                List<?> requestOrderIdsList =  (List<?>) requestOrderIdsObject;
                for(Object item : requestOrderIdsList) {
                    if(item instanceof String) {
                        requestOrderIds.add((String) item);
                    }
                }

            }

            final ListResponse resultComparingOrdersInRequestWithOrdersInShipment = shipmentService.compareOrdersInRequestWithOrdersInShipment(requestOrderIds, shipmentOrderIds);
            if (Set.of(Role.MANAGER, Role.TELLER).contains(role)) {
                shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), agencyId, null);
        
                String sourcePostalCode = shipment.getShipmentId().split("_")[1];
                if (!sourcePostalCode.substring(0, 4).equals("0000")) {
                    shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), shipment.getAgencyId(), sourcePostalCode);
                }
        
                shipmentService.updateShipmentStatus(shipmentId, 6, postalCode);
            } else if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(role)) {
                shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), agencyId, null);
                shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), agencyId, postalCode);
            }
        
            if (shipment.getAgencyId() != null) {
                shipmentService.updateShipmentStatus(shipmentId, 5, shipmentService.getPostalCodeFromAgencyId(shipment.getAgencyId()));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Phân rã lô hàng thành công",
                resultComparingOrdersInRequestWithOrdersInShipment
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                "Đã xảy ra lỗi. Vui lòng thử lại.",
                null
            ));
        }
    }

    @PostMapping("/confirm_create")
    public ResponseEntity<Response<ListResponse>> confirmCreateShipment
        (@RequestBody Shipment info,
        @RequestHeader(value = "agencyId") String agencyId,
        @RequestHeader(value = "userId") String userId,
        @RequestHeader(value = "role") Role role) throws Exception {
        try {
            
            final String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);

            final Shipment shipment = shipmentService.getOneShipment(info.getShipmentId(), postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không tồn taị trong bưu cục",
                    null
                ));
            }

            if(shipment.getStatus() != 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng đã được xác nhận tạo trên tổng cục từ trước.",
                    null
                ));
            }

            if(shipmentService.checkExistShipment(info.getShipmentId(), null)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng đã tồn tại trên cơ sở dữ liệu tổng cục từ trước.",
                    null
                ));
            }

            Agency resultFindingAgency = shipmentService.getOneAgency(agencyId);
            if(resultFindingAgency == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Xác nhận tạo lô hàng không thành công. Vui lòng thử lại.",
                    null
                ));
            }

            if(resultFindingAgency.getProvince() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Xác nhận tạo lô hàng không thành công. Vui lòng cập nhật thông tin Tỉnh của bưu cục để có thể tiếp tục.",
                    null
                ));
            }
                  
            final String distributionCenterId = shipmentService.getOneDistributionCenter(resultFindingAgency.getProvince());
            if(distributionCenterId == null) {
                return ResponseEntity.status(404).body(new Response<>(
                    true,
                    "Xác nhận tạo lô hàng không thành công. Bưu cục chưa thuộc quyền quản lý của trung tâm điều phối nào.",
                    null
                ));
            }

            if(shipmentService.getOneAgency(distributionCenterId) == null) {
                return ResponseEntity.status(404).body(new Response<>(
                    true,
                    "Trung tâm điều phối quản lý bưu cục của bạn không tồn tại.",
                    null
                ));
            }

            final String postalCodeOfDistributionCenter = shipmentService.getPostalCodeFromAgencyId(distributionCenterId);
            final boolean resultConfirmingCreateShipment = shipmentService.confirmCreateShipment(shipment, postalCodeOfDistributionCenter);
            if(!resultConfirmingCreateShipment) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(
                    true,
                    "Đã xảy ra lỗi. Vui lòng thử lại.",
                    null
                ));
            }

            shipmentService.updateShipmentStatus(shipment.getShipmentId(), 1, postalCode);
            ListResponse resultUpdatingParentForGlobalOrders = shipmentService.updateParentForGlobalOrders(shipment.getOrderIds(), shipment.getShipmentId());
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Xác nhận tạo lô hàng trên tổng cục thành công",
                resultUpdatingParentForGlobalOrders
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }
    }

    @PutMapping("/accept")
    public ResponseEntity<Response<Shipment>> approveNewShipment(@RequestParam Map<String, String> queryParams) throws Exception {
        Date approveTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTime = formatter.format(approveTime);

        final String shipmentId = queryParams.get("shipmentId");
        Shipment shipment = shipmentService.getOneShipment(shipmentId, null);
        
        // req.user
        final String agencyId = "BC_71000_089204006685";
        final String staffId = "TD_71000_089204006685";
        final String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);

        if(shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                true,
                "Lô hàng không tồn tại",
                null 
            ));
        }

        if(shipment.getStatus() >= 2) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                true,
                "Lô hàng đã được phê duyệt từ trước",
                null 
            ));
        }

        shipmentService.updateShipmentStatus(shipmentId, 2, null);
        shipmentService.updateShipmentStatus(shipmentId, 2, postalCode);
    
        String[] agencyIdSubParts = shipment.getAgencyId().split("_");

        String journeyMessage = formattedTime + ": Lô hàng được phê duyệt tại Trung tâm điều phối " + agencyId + " bởi nhân viên " + staffId;
        shipmentService.setJourney(shipmentId, formattedTime, journeyMessage, null);
        shipmentService.setJourney(shipmentId, formattedTime, journeyMessage, postalCode);

        // Check the first part of the agency_id
        if (agencyIdSubParts.length > 0 && ("DL".equals(agencyIdSubParts[0]) || "BC".equals(agencyIdSubParts[0]))) {
            // Update the shipment status
            String agencySourcePostalCode = shipmentService.getPostalCodeFromAgencyId(shipment.getAgencyId());
            shipmentService.updateShipmentStatus(shipmentId, 2, agencySourcePostalCode);
            shipmentService.setJourney(shipmentId, formattedTime, journeyMessage, agencySourcePostalCode);
        }
   
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
            false,
            "Tiếp nhận lô hàng thành công",
            null
        ));
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Shipment>>> getShipments(@RequestBody Shipment criteria,
                                       @RequestParam(required = false, defaultValue = "0") int rows,
                                       @RequestParam(required = false, defaultValue = "0") int page) {
        try {
            // Validate pagination conditions
            if (rows < 0 || page < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<List<Shipment>>(
                    true,
                    "Lỗi định dạng trang",
                    null
                ));
            }

            System.out.println(criteria);
            List<Shipment> result = new ArrayList<>();
            
            final String userRole = "ADMIN";
            if (List.of("USER", "BUSINESS").contains(userRole)) {
                result = shipmentService.getShipments(criteria, null);
            } else if (List.of("AGENCY_MANAGER", "AGENCY_TELLER", "AGENCY_HUMAN_RESOURCE_MANAGER", "AGENCY_COMPLAINTS_SOLVER", "AGENCY_SHIPPER").contains(userRole)) {
                final String userId = "TD_71000_089204006685";
                final String postalCode = shipmentService.getPostalCodeFromAgencyId(userId);
                result = shipmentService.getShipments(criteria, postalCode);
            } else {
                result = shipmentService.getShipments(criteria, null);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Shipment>>(
                false,
                "Lấy thông tin đơn hàng thành công!",
                result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<List<Shipment>>(
                true,
                e.getMessage(),
                null
            ));
        }
    }

    // Order service to getOneOrder(order_id)
    // @PostMapping("/get_orders")

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Shipment>> deleteShipment(@RequestParam Map<String, String> queryParams) throws Exception {
        
        final String shipmentId = queryParams.get("shipmentId");
        final String agencyId = "BC_71000_089204006685";
        final String userRole = "MANAGER";

        if(List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
            final String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
            final Shipment shipment = shipmentService.getOneShipment(shipmentId, postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không tồn tại",
                    null
                ));
            }

            if(shipment.getStatus() > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Lô hàng không còn khả năng để xóa. Vui lòng liên hệ lên tổng cục để được hỗ trợ.",
                    null
                ));
            }

            final boolean resultDeletingShipment = shipmentService.deleteShipment(shipmentId, postalCode);
            if(!resultDeletingShipment) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không tồn tại trong cơ sở dữ liệu bưu cục",
                    null
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                true,
                "Xóa lô hàng thành công",
                null
            ));

        }
        else if(List.of("MANAGER", "TELLER").contains(userRole)) {
            final String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
            final Shipment shipment = shipmentService.getOneShipment(shipmentId, postalCode);
            if(shipment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không tồn tại",
                    null
                ));
            }

            if(shipment.getStatus() > 3) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                    true,
                    "Lô hàng không còn khả năng để xóa. Vui lòng liên hệ lên tổng cục để được hỗ trợ.",
                    null
                ));
            }

            final boolean resultDeletingShipment = shipmentService.deleteShipment(shipmentId, postalCode);
            if(!resultDeletingShipment) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                    true,
                    "Lô hàng không tồn tại trong cơ sở dữ liệu bưu cục",
                    null
                ));
            }

            final String[] shipmentIdSubPart = shipmentId.split("_");
            if(shipmentIdSubPart[0] == "BC" || shipmentIdSubPart[0] == "DL") {
                shipmentService.updateShipmentStatus(shipmentId, 0, shipmentIdSubPart[1]);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                true,
                "Xóa lô hàng thành công",
                null
            ));
        }

        final String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
        final Shipment shipment = shipmentService.getOneShipment(shipmentId, null);
        if(shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                true,
                "Lô hàng không tồn tại",
                null
            ));
        }

        if(shipment.getStatus() > 3) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(
                true,
                "Lô hàng không còn khả năng để xóa. Vui lòng liên hệ lên tổng cục để được hỗ trợ.",
                null
            ));
        }

        final boolean resultDeletingShipment = shipmentService.deleteShipment(shipmentId, null);
        if(!resultDeletingShipment) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                true,
                "Lô hàng không tồn tại trong cơ sở dữ liệu bưu cục",
                null
            ));
        }

        final String[] shipmentIdSubPart = shipmentId.split("_");
        if(shipmentIdSubPart[0] == "BC" || shipmentIdSubPart[0] == "DL") {
            shipmentService.updateShipmentStatus(shipmentId, 0, shipmentIdSubPart[1]);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
            true,
            "Xóa lô hàng thành công",
            null
        ));
    }


    
}
