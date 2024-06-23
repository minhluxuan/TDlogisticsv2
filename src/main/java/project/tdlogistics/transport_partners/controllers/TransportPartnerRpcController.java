package project.tdlogistics.transport_partners.controllers;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.transport_partners.services.TransportPartnerService;
import project.tdlogistics.transport_partners.entities.Account;
import project.tdlogistics.transport_partners.entities.Request;
import project.tdlogistics.transport_partners.entities.Response;
import project.tdlogistics.transport_partners.entities.TransportPartner;

@Controller
public class TransportPartnerRpcController {
    @Autowired
    private TransportPartnerService transportPartnerService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "rpc.partner")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            final Request<TransportPartner> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<TransportPartner>>() {});
            switch (request.getOperation()) {
                case "checkExistTransportPartner":
                    return checkExistTransportPartner(request.getPayload());
                default:
                    return objectMapper.writeValueAsString(new Response<TransportPartner>(400, true, "Yêu cầu không hợp lệ", null));
            }    
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistTransportPartner(TransportPartner criteria) throws JsonProcessingException {
        try {
            final Optional<TransportPartner> optionalTransportPartner = transportPartnerService.checkExistTransportPartner(criteria);
            if (optionalTransportPartner.isEmpty()) {
                return objectMapper.writeValueAsString(new Response<TransportPartner>(200, false, "Đối tác vận tải không tồn tại", optionalTransportPartner.get()));
            }

            return objectMapper.writeValueAsString(new Response<TransportPartner>(200, false, "Đối tác vận tải đã tồn tại", optionalTransportPartner.get())); 

        } catch (Exception e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<TransportPartner>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
