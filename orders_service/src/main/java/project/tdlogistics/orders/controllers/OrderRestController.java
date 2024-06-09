package project.tdlogistics.orders.controllers;

import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.orders.services.OrderService;
import project.tdlogistics.orders.services.OrderService.OrderStatus;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.entities.Response;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v2/orders")
public class OrderRestController {
    @Autowired
    OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

   @PostMapping("/check")
    public ResponseEntity<Response<Order>> checkExistOrder(@RequestBody Order criteria) throws Exception {
        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            String json;
            try {
                json = objectMapper.writeValueAsString(criteria);
                System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            Optional<Order> OrderOptional;
            try {
                OrderOptional = orderService.checkExistOrder(criteria);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(false, "Đơn hàng không tồn tại.", null));
            }

            
            if (OrderOptional.isPresent()) {
                final Response response = new Response<>(false, "Đơn hàng đã tồn tại.", OrderOptional.get());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(false, "Đơn hàng không tồn tại.", null));

            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
    
    @PostMapping(value = "/search")
    public ResponseEntity<Response<List<Order>>> getOrders(@RequestBody Order info,
                                       @RequestParam(required = false, defaultValue = "0") int rows,
                                       @RequestParam(required = false, defaultValue = "0") int page) {
        try {
            // Validate pagination conditions
            if (rows < 0 || page < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<List<Order>>(
                    true,
                    "Lỗi định dạng trang",
                    null
                ));
            }

            // Convert the info map to an Order object
            Order criteria = objectMapper.convertValue(info, Order.class);

            List<Order> result = new ArrayList<>();
            
            final String userRole = "USER";
            if (List.of("USER", "BUSINESS").contains(userRole)) {
                result = orderService.getOrders(criteria, null);
            } else if (List.of("AGENCY_MANAGER", "AGENCY_TELLER", "AGENCY_HUMAN_RESOURCE_MANAGER", "AGENCY_COMPLAINTS_SOLVER", "AGENCY_SHIPPER").contains(userRole)) {
                final String userId = "TD_71000_089204006685";
                final String postalCode = orderService.getPostalCodeFromAgencyId(userId);
                result = orderService.getOrders(criteria, postalCode);
            } else {
                result = orderService.getOrders(criteria, null);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Order>>(
                false,
                "Lấy thông tin đơn hàng thành công!",
                result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<List<Order>>(
                true,
                e.getMessage(),
                null
            ));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Order>> createNewOrder(@RequestBody Order info) throws Exception {
        try {
            final String userRole = "USER";
            final String userId = "123mlnq3456";
            final String userPhone = "0787919942";
           
            if (List.of("USER").contains(userRole)) {
                // ValidationResult validationResult = orderService.validateCreatingOrder(orderRequest);
                // if (!validationResult.isValid()) {
                //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(true, validationResult.getMessage()));
                // }
                info.setUserId(userId);
                info.setPhoneNumberSender(userPhone);
                OrderStatus orderStatus = OrderStatus.PROCESSING;
                info.setStatusCode(orderStatus.getCode());
            } else if (List.of("ADMIN", "MANAGER", "TELLER", "AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                // ValidationResult validationResult = orderService.validateCreatingOrderByAdmin(info);
                // if (!validationResult.isValid()) {
                //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(true, validationResult.getMessage()));
                // }
                OrderStatus orderStatus = OrderStatus.RECEIVED;
                info.setStatusCode(orderStatus.getCode());
            }

            if ("NNT".equals(info.getServiceType()) && !info.getProvinceSource().equals(info.getProvinceDest())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Order>(
                    true,
                    "Đơn hàng phải được giao nội tỉnh!",
                    null
                ));
            }
            
            //Debug
            System.out.println(info.toString());

            final boolean resultCreatingOrder = orderService.createNewOrder(info);
            if(!resultCreatingOrder) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                    true,
                    "Tạo đơn không thành công. Vui lòng thử lại!",
                    null
                ));
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Order>(
                true,
                "Tạo đơn hàng thành công.",
                null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                true,
                e.getMessage(),
                null
            ));
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<Response<Order>> updateOrder(@RequestBody Order info,
                                                    @RequestParam Map<String, String> queryParams) throws Exception {
        try {
            //TODO: process POST request
            // Validate query parameters and body
            // String error1 = orderService.validateQueryUpdatingOrder(queryParams);
            // if (error1 != null) {
            //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Order>(
            //             true,
            //             error1,
            //             null
            //         ));
            // }

            // String error2 = orderService.validateUpdatingOrder(orderUpdateRequest);
            // if (error2 != null) {
            //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<Order>(
            //             true,
            //             error2,
            //             null
            //         ));
            // }

            final String userRole = "SHIPPER";
            final String agencyId = "TD_71000_089204006685";
            if (List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                // queryParams.put("agency_id", user.getAgencyId());
                queryParams.put("agency_id", agencyId);
            } else if (List.of("SHIPPER", "AGENCY_SHIPPER", "PARTNER_SHIPPER").contains(userRole)) {
                String postalCode = orderService.getPostalCodeFromAgencyId(agencyId);
                // boolean taskExists = shippersService.checkExistTask(new TaskRequest(queryParams.get("order_id")), postalCode);
                // if (!taskExists) {
                //     return ResponseEntity.status(404).body(Map.of("error", true, "message", "Đơn hàng " + queryParams.get("order_id") + " không tồn tại."));
                // }
                queryParams.put("agency_id", agencyId);
            }

            try {
                orderService.updateOrder(info, queryParams);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                    true,
                    e.getMessage(),
                    null
                ));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<Order>(
                false,
                "Cập nhật đơn hàng thành công",
                null
            ));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                true,
                e.getMessage(),
                null
            ));
        }                                             
        
    }
    
    @DeleteMapping("/cancel")
    public ResponseEntity<Response<Order>> cancelOrder(@RequestParam Map<String, String> conditions) throws Exception {
        try {
            final String userRole = "USER";
            final String userId = "3123uuer";
            Map<String, Object> processedCondition = new HashMap<>(conditions);
            if(List.of("USER").contains(userRole)) {
                conditions.put("userId", userId);
                final int resultCancelingOrder = orderService.cancelOrderWithTimeConstraint(processedCondition);
                if(resultCancelingOrder == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        "Đơn hàng có mã đơn hàng " + conditions.get("order_id") + " không tồn tại hoặc quá hạn để hủy.",
                        null
                    ));
                }
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Order>(
                    false,
                    "Hủy đơn hàng thành công",
                    null
                ));
            }
            else if(List.of("AGENCY_MANAGER", "AGENCY_TELLER").contains(userRole)) {
                conditions.put("agencyId", userId);
                final int resultCancelingOrder = orderService.cancelOrderWithTimeConstraint(processedCondition);
                if(resultCancelingOrder == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        "Đơn hàng có mã đơn hàng " + conditions.get("order_id") + " không tồn tại hoặc quá hạn để hủy.",
                        null
                    ));
                }
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Order>(
                    false,
                    "Hủy đơn hàng thành công",
                    null
                ));
            } 
            else {
                final int resultCancelingOrder = orderService.cancelOrderWithoutTimeConstrain(processedCondition);
                if(resultCancelingOrder == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        "Đơn hàng có mã đơn hàng " + conditions.get("order_id") + " không tồn tại hoặc quá hạn để hủy.",
                        null
                    ));
                }
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Order>(
                    false,
                    "Hủy đơn hàng thành công",
                    null
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                true,
                e.getMessage(),
                null
            ));
        } 
    }

    

}
