package project.tdlogistics.shipments.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Optional;

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
import project.tdlogistics.shipments.entities.ListResponse;
import project.tdlogistics.shipments.entities.Request;
import project.tdlogistics.shipments.services.ShipmentService;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.amqp.core.AmqpTemplate;


@RestController
@RequestMapping("/v2/shipments")
public class ShipmentRestController {
    @Autowired
    private ShipmentService shipmentService;
    
    @PostMapping(value = "/create")
    public ResponseEntity<Response<Shipment>> createShipment(@RequestBody Shipment info) throws Exception {
        try {

            // Validate request (assuming you have a validator method)
            // String validationError = validateCreatingShipment(info);
            // if (validationError != null) {
            //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            //             .body(new Response<>(true, validationError, null));
            // }
            info.setAgencyId("TD_71000_089204006685");
            System.out.println(info);
            shipmentService.createNewShipment(info, "AGENCY_MANAGER");
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(false, "Tạo lô hàng mới thành công", info));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
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
    public ResponseEntity<Response<List<String>>> addOrdersToShipment(@RequestParam Map<String, String> queryParams,
                                                @RequestBody Shipment info) {
        try {
            // Validate request
            // Assuming validation methods throw exceptions if invalid
            // shipmentRequestValidation.validateQueryUpdatingShipment(queryParams);
            // shipmentRequestValidation.validateOperationWithOrder(requestBody);

            String userRole = "AGENCY_MANAGER";
            if (List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                // String agencyId = user.getAgencyId();
                String agencyId = "TD_71000_089204006685";
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                

                // Shipment result = shipmentService.addOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
                ListResponse resultAddingOrders = shipmentService.addOrders(shipment, info.getOrderIds(), postalCode);
                List<String> acceptedArray = resultAddingOrders.getAcceptedArray();
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Thêm đơn hàng vào lô thành công", 
                    acceptedArray
                ));
            } else if (List.of("MANAGER", "TELLER").contains(userRole)) {
                // String agencyId = user.getAgencyId();
                String agencyId = "TD_00000_089204006685";
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                

                // Shipment result = shipmentService.addOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
                shipmentService.addOrders(shipment, info.getOrderIds(), postalCode);
                ListResponse resultAddingOrders = shipmentService.addOrders(shipment, info.getOrderIds(), null);
                List<String> acceptedArray = resultAddingOrders.getAcceptedArray();
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Thêm đơn hàng vào lô thành công", 
                    acceptedArray
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
            

            // Shipment result = shipmentService.addOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
            ListResponse resultAddingOrders = shipmentService.addOrders(shipment, info.getOrderIds(), postalCode);
            List<String> acceptedArray = resultAddingOrders.getAcceptedArray();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                false, 
                "Thêm đơn hàng vào lô thành công", 
                acceptedArray
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }

    }
   
    @PostMapping("/remove_orders")
    public ResponseEntity<Response<List<String>>> removeOrdersFromShipment(@RequestParam Map<String, String> queryParams,
                                                @RequestBody Shipment info) {
        try {
            // Validate request
            // Assuming validation methods throw exceptions if invalid
            // shipmentRequestValidation.validateQueryUpdatingShipment(queryParams);
            // shipmentRequestValidation.validateOperationWithOrder(requestBody);

            String userRole = "AGENCY_MANAGER";
            if (List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                // String agencyId = user.getAgencyId();
                String agencyId = "TD_71000_089204006685";
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
                

                // Shipment result = shipmentService.addOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
                ListResponse resultAddingOrders = shipmentService.removeOrders(shipment, info.getOrderIds(), postalCode);
                List<String> acceptedArray = resultAddingOrders.getAcceptedArray();
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Thêm đơn hàng vào lô thành công", 
                    acceptedArray
                ));
            } else if (List.of("MANAGER", "TELLER").contains(userRole)) {
                // String agencyId = user.getAgencyId();
                String agencyId = "TD_00000_089204006685";
                String postalCode = shipmentService.getPostalCodeFromAgencyId(agencyId);
                Shipment shipment = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(shipment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }

                // Shipment result = shipmentService.removeOrdersFromShipment(shipment, requestBody.getOrderIds(), postalCode);
                shipmentService.removeOrders(shipment, info.getOrderIds(), postalCode);
                ListResponse resultAddingOrders = shipmentService.removeOrders(shipment, info.getOrderIds(), null);
                List<String> acceptedArray = resultAddingOrders.getAcceptedArray();
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                    false, 
                    "Thêm đơn hàng vào lô thành công", 
                    acceptedArray
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
            

            // Shipment result = shipmentService.removeOrdersToShipment(shipment, requestBody.getOrderIds(), postalCode);
            ListResponse resultAddingOrders = shipmentService.removeOrders(shipment, info.getOrderIds(), postalCode);
            List<String> acceptedArray = resultAddingOrders.getAcceptedArray();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                false, 
                "Thêm đơn hàng vào lô thành công", 
                acceptedArray
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }

    }
    
    @PostMapping("/undertake")
    public ResponseEntity<Response<List<String>>> undertakeShipment(@RequestBody Shipment info) throws Exception {
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
            // TODO
            // Shipper service: assign task to shipper

            List<String> acceptedArray = resultUpdatingOrders.getAcceptedArray();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Response<>(
                false, 
                "Thêm đơn hàng vào lô thành công", 
                acceptedArray
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
            // if (shipmentId == null || shipmentId.isEmpty()) {
            //     return ResponseEntity.badRequest().body(Map.of(
            //         "error", true,
            //         "message", "Shipment ID is required"
            //     ));
            // }

            String message = body.get("message");
            // if (message == null || message.isEmpty()) {
            //     return ResponseEntity.badRequest().body(Map.of(
            //         "error", true,
            //         "message", "Message is required"
            //     ));
            // }
            

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
    public ResponseEntity<Response<ListResponse>> decomposeShipment(@RequestParam Map<String, String> queryParams, @RequestBody Map<String, Object> body) {
        try {
            Date createdTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formattedTime = formatter.format(createdTime);

            final String userRole = "AGENCY_MANAGER";
            final String shipmentId = "TD_71000_2406223456";
            final String agencyId = "BC_71000_089204006685";
            // final String postalCode = shipmentService.getPostalCodeFromAgencyId(userId);
            final String postalCode = "71000";
            if(List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                if(!shipmentService.checkExistShipment(shipmentId, postalCode)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true,
                        "Lô hàng không tồn tại trong bưu cục",
                        null
                    ));
                }
            } 
            else if(List.of("MANAGER", "TELLER").contains(userRole)) {
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

            // Object to List<String> convert
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
            if (List.of("MANAGER", "TELLER").contains(userRole)) {
                final ListResponse resultDecomposingShipment = shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), agencyId, null);
        
                String sourcePostalCode = shipment.getShipmentId().split("_")[1];
                if (!sourcePostalCode.substring(0, 4).equals("0000")) {
                    shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), shipment.getAgencyId(), sourcePostalCode);
                }
        
                shipmentService.updateShipmentStatus(shipmentId, 6, postalCode);
            } else if (List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                final ListResponse resultDecomposingShipment = shipmentService.decomposeShipment(shipment.getOrderIds(), shipment.getShipmentId(), agencyId, null);
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
    public ResponseEntity<Response<ListResponse>> confirmCreateShipment(@RequestBody Shipment info) throws Exception {
        try {
            //Validation

            final String agencyId = "BC_71000_089204006685";
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

            // Agency service
            // const resultGettingOneAgency = await agencyService.getOneAgency({ agency_id: req.user.agency_id });
            // if (!resultGettingOneAgency || resultGettingOneAgency.length === 0) {
            //     return res.status(409).json({
            //         error: true,
            //         message: "Xác nhận tạo lô hàng không thành công. Vui lòng thử lại.",
            //     });
            // }

            // if (!resultGettingOneAgency[0].province) {
            //     return res.status(409).json({
            //         error: true,
            //         message: "Xác nhận tạo lô hàng không thành công. Vui lòng cập nhật thông tin Tỉnh của bưu cục để có thể tiếp tục.",
            //     });
            // }

            // Administrative service
            // const distributionCenterId = await administrativeService.getOneDistributionCenter(resultGettingOneAgency[0].province);
            // if (!distributionCenterId) {
            //     return res.status(404).json({
            //         error: true,
            //         message: `Xác nhận tạo lô hàng không thành công.
            //         Bưu cục ${req.user.agency_id} chưa thuộc quyền quản lý của trung tâm điều phối nào.`,
            //     });
            // }
            
            // Agency service
            // if (!(await agencyService.getOneAgency({ agency_id: distributionCenterId }))) {
            //     return res.status(404).json({
            //         error: true,
            //         message: `Trung tâm điều phối ${distributionCenterId} quản lý bưu cục của bạn không tồn tại.`
            //     });
            // }

            final String distributionCenterId = "TD_00001_089204006684";
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
                "Đã xảy ra lỗi. Vui lòng thử lại.",
                null
            ));
        }
    }

}
