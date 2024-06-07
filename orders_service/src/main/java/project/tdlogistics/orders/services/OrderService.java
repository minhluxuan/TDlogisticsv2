package project.tdlogistics.orders.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.repositories.OrderRepository;
import project.tdlogistics.orders.repositories.OrderRepositoryImplement;



@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRepositoryImplement orderRepositoryImplement;

    @Autowired
    private ObjectMapper objectMapper;

    // Implement relative methods here
    public Optional<Order> checkExistOrder(Order criteria) {
        // ExampleMatcher matcher = ExampleMatcher.matching()
        //                             .withIgnorePaths("id")
        //                             .withMatcher("orderId", ExampleMatcher.GenericPropertyMatchers.exact())
        //                             .withIgnoreNullValues();
        // Example<Order> example = Example.of(criteria, matcher);
        // List<Order> orders = orderRepository.findAll(example);
        Optional<Order> matchingOrder = orderRepository.findByOrderId(criteria.getOrderId());
        // System.out.printf("Check gone %d", orders.size());
        // if (orders.size() == 1) {
        //     System.out.println("Check gone here 1");
        //     final Order order = orders.get(0);
        //     return Optional.of(order);
        // } else {
        //     System.out.println("Check gone here 2");
        //     return Optional.empty();
        // }
        
        return Optional.ofNullable(matchingOrder.get());
        
    }

    public List<Order> getOrders(Order criteria, int rows, int page) {  
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Order> example = Example.of(criteria, matcher);
         // Create a PageRequest object to specify the page number and page size
        Pageable pageable = PageRequest.of(page, rows);
        
        // Use the example and pageable object to find all matching orders in the repository
        Page<Order> orderPage = orderRepository.findAll(example, pageable);
        return orderPage.getContent();    
    }

    public Order createNewOrder(Order info) {
        // Agency managedAgency = orderService.findingManagedAgency(info.getWardSource(), info.getDistrictSource(), info.getProvinceSource());

        // Shipper shipper = shipperService.getOneStaff(managedAgency.getShipper());
        // if (shipper == null) {
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(true, "Xin lỗi quý khách. Khu vực của quý khách hiện chưa có shipper nào phục vụ."));
        // }
        Date createdTime = new Date();
        SimpleDateFormat setDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTime = setDateFormat.format(createdTime);

        String agencyId = "TD_71000_089204006685";
        info.setJourney("[]");
        // String[] areaAgencyIdSubParts = managedAgency.getAgencyId().split("_");
        // info.setAgencyId(managedAgency.getAgencyId());
        String[] areaAgencyIdSubParts = agencyId.split("_");
        info.setAgencyId(agencyId);

        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(createdTime);
        info.setOrderId(areaAgencyIdSubParts[0] + "_" + areaAgencyIdSubParts[1] + "_" + orderCode);

        System.out.println(info.getProvinceSource());
        String provinceSource = info.getProvinceSource().replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();
        String provinceDest = info.getProvinceDest().replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();
        
        // info.setFee(serviceFeeCalculator.calculateFee(info.getServiceType(), provinceSource, provinceDest, info.getMass(), 0.15, false));
        info.setFee(30000);
        info.setPaid(false);

        // String orderCodeRandom = randomStringGenerator.generate(15, RandomStringGenerator.Numeric);
        Long orderCodeRandom = (long) 123456789;
        info.setOrderCode(orderCodeRandom);
        // PaymentServiceResult paymentResult = paymentService.createPaymentService(Long.parseLong(orderCodeRandom), info.getFee(), "THANH TOAN DON HANG");
        
        // if (paymentResult == null || paymentResult.getQrCode() == null) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(true, "Lỗi khi tạo hóa đơn thanh toán. Vui lòng thử lại."));
        // }

        // info.setQrCode(paymentResult.getQrCode());
        info.setQrcode("123456");
        info.setCreatedAt(createdTime);
        final Order resultCreatingOrder = orderRepository.save(info);
        if (resultCreatingOrder == null) {
            return null;
        }

        // boolean createdOrderInAgency = orderService.createOrderInAgencyTable(orderRequest, managedAgency.getPostalCode());
        // if (!createdOrderInAgency) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(true, "Tạo đơn hàng thất bại."));
        // }

        // String postalCode = utils.getPostalCodeFromAgencyID(managedAgency.getShipper());
        // String postalCode = getPostalCodeFromAgencyId(agencyId);
        // List<String> acceptedOrders = shipperService.assignNewTasks(Collections.singletonList(orderRequest.getOrderId()), managedAgency.getShipper(), postalCode);

        // for (String orderId : acceptedOrders) {
        //     String orderMessage;
        //     int orderStatusCode;
        //     String formattedTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        //     Order order = orderService.getOneOrder(orderId);

        //     if (order.getStatusCode() == serviceStatus.getProcessingCode()) {
        //         orderMessage = formattedTime + ": Đơn hàng đang được bưu tá đến nhận";
        //         orderStatusCode = serviceStatus.getTakingCode();
        //         orderService.setJourney(orderId, orderMessage, orderStatusCode);
        //         orderService.setJourney(orderId, orderMessage, orderStatusCode, orderRequest.getOrderId().split("_")[1]);
        //     } else if (order.getStatusCode() == serviceStatus.getReceivedCode()) {
        //         orderMessage = formattedTime + ": Đơn hàng đã được bưu cục tiếp nhận";
        //         orderStatusCode = serviceStatus.getEnterAgencyCode();
        //         orderService.setJourney(orderId, orderMessage, orderStatusCode);
        //         orderService.setJourney(orderId, orderMessage, orderStatusCode, orderRequest.getOrderId().split("_")[1]);
        //     }
        // }

        if (info.getStatusCode() == OrderStatus.PROCESSING.getCode()) {
            String orderMessage = formattedTime + ": Đơn hàng đang được bưu tá đến nhận";
            OrderStatus orderStatusCode = OrderStatus.TAKING;
            setJourney(info.getOrderId(), orderMessage, orderStatusCode);
            // setJourney(orderId, orderMessage, orderStatusCode, orderRequest.getOrderId().split("_")[1]);
        } else if (info.getStatusCode() == OrderStatus.RECEIVED.getCode()) {
            String orderMessage = formattedTime + ": Đơn hàng đã được bưu cục tiếp nhận";
            OrderStatus orderStatusCode = OrderStatus.ENTER_AGENCY;
            setJourney(info.getOrderId(), orderMessage, orderStatusCode);
            // setJourney(orderId, orderMessage, orderStatusCode, orderRequest.getOrderId().split("_")[1]);
        }

        // userService.updateUserInfo(new UserUpdateInfo(orderRequest.getProvinceSource(), orderRequest.getDistrictSource(), orderRequest.getWardSource(), orderRequest.getDetailSource()), user.getPhoneNumber());
        return info;
    }

    public Order updateOrder(Order info, Map<String, String> conditions) {
        Optional<Order> optionalOrder = orderRepository.findByOrderIdAndAgencyId(conditions.get("orderId"), conditions.get("agencyId"));
        if(optionalOrder == null) {
            return null;
        }
        if(optionalOrder.isPresent()) {
            if (info.getMass() != 0) {
                optionalOrder.get().setMass(info.getMass());
            }
            if (info.getOrderCode() != null) {
                optionalOrder.get().setOrderCode(info.getOrderCode());
            }
            if (info.getQrcode() != null) {
                optionalOrder.get().setQrcode(info.getQrcode());
            }
        }

        orderRepository.save(optionalOrder.get());
        return optionalOrder.get();
    }

    public enum OrderStatus {
        DELIVERED_SUCCESS(1, "Giao hàng thành công"),
        PROCESSING(2, "Đang được xử lí"),
        TAKING(3, "Chờ lấy hàng"),
        TAKEN_SUCCESS(4, "Lấy hàng thành công"),
        TAKEN_FAIL(5, "Lấy hàng thất bại"),
        DELIVERING(6, "Đang giao tới người nhận"),
        DELIVERED_CANCEL(7, "Đã hủy yêu cầu giao hàng"),
        DELIVERED_FAIL(8, "Giao hàng thất bại"),
        REFUNDING(9, "Đang hoàn hàng"),
        REFUNDED_SUCCESS(10, "Hoàn hàng thành công"),
        REFUNDED_FAIL(11, "Hoàn hàng thất bại"),
        ENTER_AGENCY(12, "Đã tới bưu cục"),
        LEAVE_AGENCY(13, "Đã rời bưu cục"),
        THIRD_PARTY_DELIVERY(14, "Kiện hàng được chuyển cho đối tác thứ ba giao"),
        RECEIVED(15, "Đã được tiếp nhận.");
    
        private final int code;
        private final String message;
    
        OrderStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }
    
        public int getCode() {
            return code;
        }
    
        public String getMessage() {
            return message;
        }
    
        public static String getMessageByCode(int code) {
            for (OrderStatus status : OrderStatus.values()) {
                if (status.getCode() == code) {
                    return status.getMessage();
                }
            }
            return "Mã không xác định";
        }
    }
    
    public String getPostalCodeFromAgencyId(String agencyId) {
        String[] agencyIdSubParts = agencyId.split("_");
        return agencyIdSubParts[1];
    }

    public int cancelOrderWithTimeConstraint(Map<String, Object> conditions) {
        return orderRepositoryImplement.cancelOrderWithTimeConstraint(conditions);
    }

    public int cancelOrderWithoutTimeConstrain(Map<String, Object> conditions) {
        return orderRepositoryImplement.cancelOrderWithoutTimeConstraint(conditions);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void setJourney(String orderId, String orderMessage, OrderStatus orderStatus) {
        // String orderTable = postalCode != null ? postalCode + "_orders" : "orders";

        Optional<Order> optionalOrder = orderRepository.findByOrderId(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            List<String> journey;
            try {
                journey = order.getJourney() != null ? objectMapper.readValue(order.getJourney(), List.class) : new ArrayList<>();
            } catch (JsonProcessingException e) {
                journey = new ArrayList<>();
            }

            journey.add(orderMessage);

            try {
                order.setJourney(objectMapper.writeValueAsString(journey));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            order.setStatusCode(orderStatus.getCode());
            orderRepository.save(order);
        }
    }



}
