package project.tdlogistics.users.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.TransportPartner;

@Service
public class TransportPartnerService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.partner";

    public TransportPartner checkExistTransportPartner(TransportPartner criteria) throws JsonProcessingException {
        final String jsonRequestCheckingExistPartner = objectMapper.writeValueAsString(new Request<TransportPartner>("checkExistTransportPartner", null, criteria));
        final String jsonResponseCheckingExistPartner = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCheckingExistPartner);

        if (jsonResponseCheckingExistPartner == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<TransportPartner> responseCheckingExistPartner = objectMapper.readValue(jsonResponseCheckingExistPartner, new TypeReference<Response<TransportPartner>>() {});
        if (responseCheckingExistPartner.getError()) {
            throw new InternalError(responseCheckingExistPartner.getMessage());
        }

        return responseCheckingExistPartner.getData();
    }
}
