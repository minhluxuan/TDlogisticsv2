package project.tdlogistics.orders.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import project.tdlogistics.orders.entities.Order;
import project.tdlogistics.orders.entities.Request;
import project.tdlogistics.orders.entities.Response;

@Controller
public class OrderRpcController {
    @RabbitListener(queues = "rpc.orders")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<Order> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Order>>() {});
            switch (request.getOperation()) {
                // Handle each operation here
                case "example":
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Order>(400, true, "example message", null));
                
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Order>(400, true, "Yêu cầu không hợp lệ", null));
            }           
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Order>(404, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    // Implement relative methods here
}
