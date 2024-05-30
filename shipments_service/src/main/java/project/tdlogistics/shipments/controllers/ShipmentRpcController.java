package project.tdlogistics.shipments.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import project.tdlogistics.shipments.entities.Shipment;
import project.tdlogistics.shipments.entities.Request;
import project.tdlogistics.shipments.entities.Response;

@Controller
public class ShipmentRpcController {
    @RabbitListener(queues = "rpc.Shipments")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<Shipment> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Shipment>>() {});
            switch (request.getOperation()) {
                // Handle each operation here
                case "example":
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Shipment>(HttpStatus.BAD_REQUEST, true, "example message", null));
                
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Shipment>(HttpStatus.BAD_REQUEST, true, "Yêu cầu không hợp lệ", null));
            }           
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Shipment>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    // Implement relative methods here
}
