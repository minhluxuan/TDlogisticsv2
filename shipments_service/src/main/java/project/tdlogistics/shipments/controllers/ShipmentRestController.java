package project.tdlogistics.shipments.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    
    @GetMapping("/add_orders")
    public ResponseEntity<Response<List<String>>> addOrderToShipment(@RequestParam Map<String, String> queryParams,
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
                Shipment shipment = null;
                try {
                    Optional<Shipment> optionalShipmennt = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                    if(optionalShipmennt.isPresent()) {
                        shipment = optionalShipmennt.get();
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                            true, 
                            "Không tìm thấy lô hàng.", 
                            null
                        ));
                    }
                } catch (Exception e) {
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
                Shipment shipment = null;
                try {
                    Optional<Shipment> optionalShipmennt = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                    if(optionalShipmennt.isPresent()) {
                        shipment = optionalShipmennt.get();
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                            true, 
                            "Không tìm thấy lô hàng.", 
                            null
                        ));
                    }
                } catch (Exception e) {
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
            Shipment shipment = null;
            try {
                Optional<Shipment> optionalShipmennt = shipmentService.getOneShipment(queryParams.get("shipment_id"), postalCode);
                if(optionalShipmennt.isPresent()) {
                    shipment = optionalShipmennt.get();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(
                        true, 
                        "Không tìm thấy lô hàng.", 
                        null
                    ));
                }
            } catch (Exception e) {
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
    
}
