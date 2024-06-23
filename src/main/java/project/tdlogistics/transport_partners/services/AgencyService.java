package project.tdlogistics.transport_partners.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.transport_partners.entities.Agency;
import project.tdlogistics.transport_partners.entities.Request;
import project.tdlogistics.transport_partners.entities.Response;

@Service
public class AgencyService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.agency";

    public Agency checkExistAgency(Agency criteria) throws JsonProcessingException {
        String jsonRequestGettingAgency = objectMapper.writeValueAsString(new Request<Agency>("checkExistAgency", null, criteria));
        String jsonResponseGettingAgency = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestGettingAgency);
        if (jsonResponseGettingAgency == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        Response<Agency> responseGettingAgency = objectMapper.readValue(jsonResponseGettingAgency, new TypeReference<Response<Agency>>() {});
            
        if (responseGettingAgency.getError()) {
            throw new InternalError(responseGettingAgency.getMessage());
        }

        return responseGettingAgency.getData();
    }
}
