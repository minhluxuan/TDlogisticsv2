package project.tdlogistics.orders.services;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;


import org.apache.commons.text.RandomStringGenerator;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.orders.entities.Customer;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.entities.Request;
import project.tdlogistics.orders.entities.Response;
import project.tdlogistics.orders.entities.Ward;
import project.tdlogistics.orders.repositories.ColumnNameMapper;
import project.tdlogistics.orders.repositories.DBUtils;
import project.tdlogistics.orders.repositories.OrderRepository;
import project.tdlogistics.orders.repositories.OrderRepositoryImplement;
import project.tdlogistics.orders.configurations.MyBeanUtils;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRepositoryImplement orderRepositoryImplement;
   
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private DBUtils dbUtils;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    public Optional<Order> checkExistOrder(Order criteria) {
        Optional<Order> matchingOrder = orderRepository.findByOrderId(criteria.getOrderId());
        return Optional.ofNullable(matchingOrder.get());
    }

    public Order createNewOrder(Order info, String userId) throws JsonProcessingException {
        Date createdTime = new Date();
        SimpleDateFormat setDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTime = setDateFormat.format(createdTime);

        String agencyId = info.getAgencyId();
        String postalCode = getPostalCodeFromAgencyId(agencyId);
        
        String[] areaAgencyIdSubParts = agencyId.split("_");

        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(createdTime);
        info.setOrderId(areaAgencyIdSubParts[0] + "_" + areaAgencyIdSubParts[1] + "_" + orderCode);

        String provinceSource = info.getProvinceSource().replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();
        String provinceDest = info.getProvinceDest().replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();
        
        final double serviceFee = FeeService.calculateFee(info.getServiceType(), provinceSource, provinceDest, info.getMass(), 0.15, false);
        info.setFee((float) serviceFee);
        info.setPaid(false);

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build();
        String randomNumericString = generator.generate(15);
        Long orderCodeRandom = Long.parseLong(randomNumericString);
        info.setOrderCode(orderCodeRandom);

        // PaymentServiceResult paymentResult = paymentService.createPaymentService(Long.parseLong(orderCodeRandom), info.getFee(), "THANH TOAN DON HANG");
        
        // if (paymentResult == null || paymentResult.getQrCode() == null) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(true, "Lỗi khi tạo hóa đơn thanh toán. Vui lòng thử lại."));
        // }

        // info.setQrCode(paymentResult.getQrCode());

        info.setQrcode("123456");
        info.setCreatedAt(createdTime);

        
        System.out.println("HERE");
        orderRepository.save(info);

        final boolean resultCreatingOrderInAgency = createNewOrderInAgency(info, postalCode);
        if(!resultCreatingOrderInAgency) {
            return null;
        }

        if (info.getStatusCode() == OrderStatus.PROCESSING.getCode()) {
            String orderMessage = formattedTime + ": Đơn hàng đang được bưu tá đến nhận";
            OrderStatus orderStatusCode = OrderStatus.TAKING;
            setJourney(info.getOrderId(), orderMessage, orderStatusCode, null);
            setJourney(info.getOrderId(), orderMessage, orderStatusCode, postalCode);
        } else if (info.getStatusCode() == OrderStatus.RECEIVED.getCode()) {
            String orderMessage = formattedTime + ": Đơn hàng đã được bưu cục tiếp nhận";
            OrderStatus orderStatusCode = OrderStatus.ENTER_AGENCY;
            setJourney(info.getOrderId(), orderMessage, orderStatusCode, null);
            setJourney(info.getOrderId(), orderMessage, orderStatusCode, postalCode);
        }

        Customer customer = new Customer();
        customer.setProvince(info.getProvinceSource());
        customer.setDistrict(info.getDistrictSource());
        customer.setWard(info.getWardSource());
        customer.setDetailAddress(info.getDetailSource());
        updateUserInfo(userId, customer);

        final Optional<Order> optionalCreatedOrder = orderRepository.findByOrderId(info.getOrderId());
        return optionalCreatedOrder.isPresent() ? optionalCreatedOrder.get() : null;
    }

    @SuppressWarnings("unchecked")
    public boolean createNewOrderInAgency(Order info, String postalCode) {
        String ordersTable = (postalCode == null) ? "orders" : (postalCode + "_orders");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        
        for (Field field : Order.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(info);
                if (value != null) {
                    if (field.getName().equals("journey")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListToJson((List<String>) value));
                        continue;
                    }

                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                } else {
                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return dbUtils.insert(ordersTable, fields, values) > 0;
    }

    public Order updateOrder(Order info, String orderId, String agencyId) {
        Optional<Order> optionalOrder = orderRepository.findByOrderIdAndAgencyId(orderId, agencyId);
        
        if (optionalOrder.isEmpty()) {
            return null;
        }
        
        
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("orderId", orderId);
        conditions.put("agencyId", agencyId);
        final String postalCode = getPostalCodeFromAgencyId(agencyId);
        if(!postalCode.equals("00000")) {
            final int resultUpdatingInAgency = updateOrderInAgency(info, conditions, postalCode);
            if(resultUpdatingInAgency == 0) {
                return null;
            }
        }

        MyBeanUtils.copyNonNullProperties(info, optionalOrder.get());

        orderRepository.save(optionalOrder.get());
        return optionalOrder.get();
    }

    @SuppressWarnings("unchecked")
    public int updateOrderInAgency(Order criteria, Map<String, Object> conditions, String postalCode) {
        if(postalCode == null) {
            return 0;
        }

        String ordersTable = (postalCode == null) ? "orders" : (postalCode + "_orders");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        
        for (Field field : Order.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(criteria);
                if (value != null) {
                    if (field.getName().equals("journey")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListToJson((List<String>) value));
                        continue;
                    }
                    
                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        
        List<String> conditionFields = new ArrayList<>();
        List<Object> conditionValues = new ArrayList<>();

        for (Entry<String, Object> entry : conditions.entrySet()) {
            conditionFields.add(entry.getKey());
            conditionValues.add(entry.getValue());
        }

        return dbUtils.updateOne(ordersTable, fields, values, conditionFields, conditionValues);
    }

    @SuppressWarnings("unchecked")
    public int updateOrderForRpcController(Order criteria, Map<String, Object> conditions, String postalCode) {
        String ordersTable = (postalCode == null) ? "orders" : (postalCode + "_orders");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        
        for (Field field : Order.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(criteria);
                if (value != null) {
                    if (field.getName().equals("journey")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListToJson((List<String>) value));
                        continue;
                    } 
                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        
        List<String> conditionFields = new ArrayList<>();
        List<Object> conditionValues = new ArrayList<>();

        for (Entry<String, Object> entry : conditions.entrySet()) {
            conditionFields.add(entry.getKey());
            conditionValues.add(entry.getValue());
        }

        return dbUtils.updateOne(ordersTable, fields, values, conditionFields, conditionValues);
    }

    @SuppressWarnings("unchecked")
    public List<Order> getOrders (Order criteria, String postalCode) {
        String ordersTable = (postalCode == null) ? "orders" : (postalCode + "_orders");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

       
        for (Field field : Order.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(criteria);
                if (value != null) {
                    if (field.getName().equals("journey")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListToJson((List<String>) value));
                        continue;
                    }

                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        List<Order> orders = dbUtils.find(ordersTable, fields, values, false, null, null, Order.class);
        return orders;
    
    }

    public Order getOneOrder (String orderId, String postalCode) {
        final String ordersTable = (postalCode == null) ? "orders" : (postalCode + "_orders");

        List<String> fields = Arrays.asList("order_id");
        List<Object> values = Arrays.asList(orderId);
        return dbUtils.findOneIntersect(ordersTable, fields, values, Order.class);
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

    public boolean setJourney(String orderId, String orderMessage, OrderStatus orderStatus, String postalCode) {
        String orderTable = postalCode != null ? postalCode + "_orders" : "orders";

        Order order = getOneOrder(orderId, postalCode);
        if(order == null) {
            return false;
        }

        List<String> journey = order.getJourney();
        if(journey == null) {
            journey = new ArrayList<>();
        }

        journey.add(orderMessage);
    
        String journeyAsString = null;
        try {
            journeyAsString = objectMapper.writeValueAsString(journey);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); 
            return false;
        }

        List<String> fields = Arrays.asList("journey", "status_code");
        List<Object> values = Arrays.asList(journeyAsString, orderStatus.getCode());
        List<String> conditionFields = Arrays.asList("order_id");
        List<Object> conditionValues = Arrays.asList(orderId);
        
        return dbUtils.update(orderTable, fields, values, conditionFields, conditionValues) > 0;
    }

    public Ward findManagedAgency(String ward, String district, String province) throws JsonProcessingException {
        if(ward == null || district == null || province == null) {
            throw new IllegalArgumentException(String.format("Thiếu thông tin đơn vị hành chính"));
        }

        final String jsonRequestCheckingExistDistrict = objectMapper.writeValueAsString(new Request<Ward>("findWards", null, new Ward(province, district, ward)));
        final String jsonResponseCheckingExistDistrict = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestCheckingExistDistrict);

        final Response<List<Ward>> response = objectMapper.readValue(jsonResponseCheckingExistDistrict, new TypeReference<Response<List<Ward>>>() {});
        if (response != null && response.getData() != null) {
            return response.getData().get(0);
        } else {
            return null;
        }

    }

    public boolean updateUserInfo(String id, Customer info) throws JsonProcessingException {
        if(id == null) {
            throw new IllegalArgumentException(String.format("Thiếu thông tin khách hàng"));
        }
        
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("id", id);

        final String jsonRequestUpdatingUserInfo = objectMapper.writeValueAsString(new Request<Customer>("updateCustomer", conditions, info));
        final String jsonResponseUpdatingUserInfo = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.users", jsonRequestUpdatingUserInfo);

        final Response<Customer> response = objectMapper.readValue(jsonResponseUpdatingUserInfo, new TypeReference<Response<Customer>>() {});
        if (response != null && response.getData() != null) {
            return false;
        } else {
            return true;
        }
    }
}
