package project.tdlogistics.tasks.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import project.tdlogistics.tasks.entities.Order;
import project.tdlogistics.tasks.entities.Request;
import project.tdlogistics.tasks.entities.Response;

@Controller
public class ShipperRpcController {
    @RabbitListener(queues = "rpc.tasks")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<Order> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Order>>() {});
            switch (request.getOperation()) {
                // Handle each operation here
                case "example":
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Order>(HttpStatus.BAD_REQUEST, true, "example message", null));
                
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Order>(HttpStatus.BAD_REQUEST, true, "Yêu cầu không hợp lệ", null));
            }           
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Order>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    // Implement relative methods here
}
