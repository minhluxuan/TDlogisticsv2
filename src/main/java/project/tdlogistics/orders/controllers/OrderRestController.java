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

import project.tdlogistics.orders.services.FeeService;
import project.tdlogistics.orders.services.OrderService;
import project.tdlogistics.orders.services.OrderService.OrderStatus;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.entities.Response;
import project.tdlogistics.orders.entities.Role;
import project.tdlogistics.orders.entities.Ward;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v2/orders")
public class OrderRestController {
    @Autowired
    private OrderService orderService;

   @PostMapping("/check")
    public ResponseEntity<Response<Order>> checkExistOrder(@RequestBody Order criteria) throws Exception {
        try {
            Optional<Order> optionalOrder = orderService.checkExistOrder(criteria);
            if (optionalOrder.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Order>(false, "Đơn hàng không tồn tại", null));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new Response<Order>(false, "Đơn hàng đã tồn tại", optionalOrder.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
    
    @PostMapping(value = "/search")
    public ResponseEntity<Response<List<Order>>> getOrders(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId", required = false) String agencyId,
        @RequestBody Order criteria,
        @RequestParam(required = false, defaultValue = "0") int rows,
        @RequestParam(required = false, defaultValue = "0") int page) {
        try {
            if (Set.of(Role.CUSTOMER, Role.BUSINESS).contains(role)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Order>>(
                    false,
                    "Lấy thông tin thành công", 
                    orderService.getOrders(criteria, null)
                ));
            } 
            
            if (Set.of(
                Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER,
                Role.AGENCY_TELLER, Role.AGENCY_COMPLAINTS_SOLVER,
                Role.SHIPPER).contains(role)
            ) {
                final String postalCode = orderService.getPostalCodeFromAgencyId(agencyId);
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Order>>(
                    false,
                    "Lấy thông tin thành công", 
                    orderService.getOrders(criteria, postalCode)
                ));
            } 

            if (Set.of(
                Role.ADMIN,Role.MANAGER,
                Role.HUMAN_RESOURCE_MANAGER, Role.TELLER,
                Role.COMPLAINTS_SOLVER, Role.SHIPPER).contains(role)
            ) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<List<Order>>(
                    false,
                    "Lấy thông tin thành công", 
                    orderService.getOrders(criteria, null)
                ));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<Order>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<List<Order>>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Order>> createNewOrder(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId", required = false) String agencyId,
        @RequestBody Order payload
    ) throws Exception {
        try {

            // Check exist agency and shipper serving here\
            Ward resultFindingManageAgency = orderService.findManagedAgency(payload.getWardSource(), payload.getDistrictSource(), payload.getProvinceSource());
            if(resultFindingManageAgency == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                    true,
                    String.format("Xin lỗi quý khách. Dịch vụ chúng tôi chưa có mặt ở %s, %s, %s.", payload.getWardSource(), payload.getDistrictSource(), payload.getProvinceSource()),
                    null
                ));
            }

            if(resultFindingManageAgency.getShipper() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                    true,
                    "Xin lỗi quý khách. Khu vực của quý khách hiện chưa có shipper nào phục vụ.",
                    null
                ));
            }



            String agencyIdWillServer = resultFindingManageAgency.getAgencyId();
            payload.setAgencyId(agencyIdWillServer);

            if (Set.of(Role.CUSTOMER).contains(role)) {
                payload.setUserId(userId);
                payload.setPhoneNumberSender(payload.getPhoneNumberSender());
                OrderStatus orderStatus = OrderStatus.PROCESSING;
                payload.setStatusCode(orderStatus.getCode());
            } else if (Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(role)) {
                OrderStatus orderStatus = OrderStatus.RECEIVED;
                payload.setStatusCode(orderStatus.getCode());
            }

            if ("NNT".equals(payload.getServiceType()) && !payload.getProvinceSource().equals(payload.getProvinceDest())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<Order>(
                    true,
                    "Đơn hàng phải được giao nội tỉnh!",
                    null
                ));
            }
            
            final Order createdOrder = orderService.createNewOrder(payload, userId);
            if (createdOrder == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                    true,
                    "Tạo đơn không thành công. Vui lòng thử lại!",
                    null
                ));
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Order>(
                true,
                "Tạo đơn hàng thành công.",
                createdOrder
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                true,
                e.getMessage(),
                null
            ));
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<Response<Order>> updateOrder(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "agencyId", required = false) String agencyId,
        @RequestParam(name = "orderId") String orderId,
        @RequestBody Order payload
    ) {
        try {
            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(role)) {
                final Order updatedOrder = orderService.updateOrder(payload, orderId, agencyId);

                if (updatedOrder == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        String.format("Đơn hàng %s không tồn tại", orderId),
                        null
                    ));
                }
                
                return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Order>(
                    true,
                    "Cập nhật đơn hàng thành công",
                    updatedOrder
                ));
            }
            
            if (Set.of(Role.SHIPPER).contains(role)) {
                String postalCode = orderService.getPostalCodeFromAgencyId(agencyId);
                
                // Check exist task here

                final Order updatedOrder = orderService.updateOrder(payload, orderId, agencyId);
                if (updatedOrder == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        false,
                        String.format("Đơn hàng %s không tồn tại", orderId),
                        null
                    ));
                }
                
                return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Order>(
                    false,
                    "Cập nhật đơn hàng thành công",
                    updatedOrder
                ));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Order>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Order>(
                true,
                e.getMessage(),
                null
            ));
        }                                             
    }
    
    @DeleteMapping("/cancel")
    public ResponseEntity<Response<Order>> cancelOrder(@RequestParam Map<String, Object> conditions) throws Exception {
        try {
            final String userRole = "USER";
            final String userId = "123mlnq3456";
            // Map<String, Object> processedCondition = new HashMap<>(conditions);
            System.out.println(conditions.toString());
            if(List.of("USER").contains(userRole)) {
                conditions.put("userId", userId);
                final int resultCancelingOrder = orderService.cancelOrderWithTimeConstraint(conditions);
                if(resultCancelingOrder == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        "Đơn hàng có mã đơn hàng " + conditions.get("orderId") + " không tồn tại hoặc quá hạn để hủy.",
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
                final int resultCancelingOrder = orderService.cancelOrderWithTimeConstraint(conditions);
                if(resultCancelingOrder == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        "Đơn hàng có mã đơn hàng " + conditions.get("orderId") + " không tồn tại hoặc quá hạn để hủy.",
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
                final int resultCancelingOrder = orderService.cancelOrderWithoutTimeConstrain(conditions);
                if(resultCancelingOrder == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Order>(
                        true,
                        "Đơn hàng có mã đơn hàng " + conditions.get("orderId") + " không tồn tại hoặc quá hạn để hủy.",
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

    @PostMapping("/calculate_fee")
    public ResponseEntity<Response<Double>> postMethodName(@RequestBody Map<String, Object> criteria) throws Exception {
        try {
            String provinceSource = ((String) criteria.get("provinceSource")).replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();
            String provinceDest = ((String) criteria.get("provinceDest")).replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();

            double resultCalculatingFee = FeeService.calculateFee((String) criteria.get("serviceType"), provinceSource, provinceDest, (double) criteria.get("mass") , 0.15, false);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Tính phí thành công",
                resultCalculatingFee
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
}
