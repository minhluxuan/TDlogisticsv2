package project.tdlogistics.vehicle.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.vehicle.entities.Request;
import project.tdlogistics.vehicle.entities.Response;
import project.tdlogistics.vehicle.entities.TransportPartnerStaff;

@Service
public class TransportPartnerStaffService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.partner_staff";

    public TransportPartnerStaff checkExistTransportPartnerStaff(TransportPartnerStaff criteria) throws JsonProcessingException {
        final String jsonRequestCheckingExistPartnerStaff = objectMapper.writeValueAsString(new Request<TransportPartnerStaff>("checkExistPartnerStaff", null, criteria));
        final String jsonResponseCheckingExistPartnerStaff = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCheckingExistPartnerStaff);

        if (jsonResponseCheckingExistPartnerStaff == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<TransportPartnerStaff> responseCheckingExistPartnerStaff = objectMapper.readValue(jsonResponseCheckingExistPartnerStaff, new TypeReference<Response<TransportPartnerStaff>>() {});
        if (responseCheckingExistPartnerStaff.getError()) {
            throw new InternalError(responseCheckingExistPartnerStaff.getMessage());
        }

        return responseCheckingExistPartnerStaff.getData();
    }

    public TransportPartnerStaff createTransportPartnerStaff(TransportPartnerStaff payload) throws JsonProcessingException {
        final String jsonRequestCreatingStaff = objectMapper.writeValueAsString(new Request<TransportPartnerStaff>("createNewPartnerStaff", null, payload));
        final String jsonResponseCreatingStaff = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCreatingStaff);

        if (jsonResponseCreatingStaff == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<TransportPartnerStaff> responseCreatingStaff = objectMapper.readValue(jsonResponseCreatingStaff, new TypeReference<Response<TransportPartnerStaff>>() {});
        if (responseCreatingStaff.getError()) {
            throw new InternalError(responseCreatingStaff.getMessage());
        }

        return responseCreatingStaff.getData();
    }
}
