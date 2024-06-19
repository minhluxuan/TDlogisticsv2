package project.tdlogistics.tasks.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import project.tdlogistics.tasks.entities.ListResponse;
import project.tdlogistics.tasks.entities.Order;
import project.tdlogistics.tasks.entities.Request;
import project.tdlogistics.tasks.entities.Response;
import project.tdlogistics.tasks.entities.Shipment;
import project.tdlogistics.tasks.entities.ShipperTask;
import project.tdlogistics.tasks.services.ShipperService;

@Controller
public class ShipperRpcController {

    @Autowired
    private ShipperService shipperService;

    @RabbitListener(queues = "rpc.tasks")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<ShipperTask> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<ShipperTask>>() {});
            switch (request.getOperation()) {
                // Handle each operation here
                case "assignTask":
                    return assignTaskToShipper(request.getParams());
                
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<ShipperTask>(400, true, "Yêu cầu không hợp lệ", null));
            }           
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<ShipperTask>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    // Implement relative methods here
    private String assignTaskToShipper(Map<String, Object> conditions) throws JsonProcessingException {
        try {
            @SuppressWarnings("unchecked")
            List<String> orderIds = (List<String>) conditions.get("orderIds");
            String staffId = (String) conditions.get("staffId");
            String postalCode = (String) conditions.get("postalCode");

            final ListResponse result = shipperService.assignNewTasks(orderIds, staffId, postalCode);
            return (new ObjectMapper()).writeValueAsString(new Response<ListResponse>(200, false, "Giao việc thành công", result));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
