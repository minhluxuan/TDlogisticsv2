package project.tdlogistics.orders.controllers;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.entities.Request;
import project.tdlogistics.orders.entities.Response;
import project.tdlogistics.orders.services.OrderService;

@Controller
public class OrderRpcController {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "rpc.orders")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<Order> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Order>>() {});
            switch (request.getOperation()) {
                // Handle each operation here
                case "updateOneOrder":
                    return updateOneOrder(request.getPayload(), request.getParams());
                case "findOneOrder":
                    return findOneOrder(request.getPayload());
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Order>(400, true, "Yêu cầu không hợp lệ", null));
            }           
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Order>(404, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    // Implement relative methods here
    private String updateOneOrder(Order criteria, Map<String, Object> conditions) throws JsonProcessingException{
        try {
            String postalCode = null;
            if(conditions.containsKey("postalCode")) {
                postalCode = (String) conditions.remove("postalCode");
            }
            final int resultUpdatingOrder = orderService.updateOrderForRpcController(criteria, conditions, postalCode);
            if(resultUpdatingOrder == 0) {
                return (new ObjectMapper()).writeValueAsString(new Response<Order>(400, true, "Cập nhật đơn hàng thất bại", null));
            }

            return (new ObjectMapper()).writeValueAsString(new Response<Integer>(200, false, "Cập nhật đơn hàng thành công", resultUpdatingOrder));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String findOneOrder(Order criteria) throws JsonProcessingException{
        try {
            final Order resultFindingOrder = orderService.getOneOrder(criteria.getOrderId(), null);
            if(resultFindingOrder == null) {
                return (new ObjectMapper()).writeValueAsString(new Response<Order>(200, false, "Đơn hàng không tồn tại", null));
            }

            return (new ObjectMapper()).writeValueAsString(new Response<Order>(200, false, "Lấy đơn hàng thành công", resultFindingOrder));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
