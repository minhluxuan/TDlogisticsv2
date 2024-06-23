package project.tdlogistics.transport_partners.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.transport_partners.entities.Request;
import project.tdlogistics.transport_partners.entities.Response;
import project.tdlogistics.transport_partners.entities.TransportPartnerRepresentor;

@Service
public class TransportPartnerStaffService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.partner_staff";

    public TransportPartnerRepresentor checkExiTransportPartnerRepresentor(TransportPartnerRepresentor criteria) throws JsonProcessingException {
        final String jsonRequestCheckingExistPartnerStaff = objectMapper.writeValueAsString(new Request<TransportPartnerRepresentor>("checkExistPartnerStaff", null, criteria));
        final String jsonResponseCheckingExistPartnerStaff = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCheckingExistPartnerStaff);

        if (jsonResponseCheckingExistPartnerStaff == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<TransportPartnerRepresentor> responseCheckingExistPartnerStaff = objectMapper.readValue(jsonResponseCheckingExistPartnerStaff, new TypeReference<Response<TransportPartnerRepresentor>>() {});
        if (responseCheckingExistPartnerStaff.getError()) {
            throw new InternalError(responseCheckingExistPartnerStaff.getMessage());
        }

        return responseCheckingExistPartnerStaff.getData();
    }

    public TransportPartnerRepresentor createTransportPartnerRepresentor(TransportPartnerRepresentor payload) throws JsonProcessingException {
        final String jsonRequestCreatingStaff = objectMapper.writeValueAsString(new Request<TransportPartnerRepresentor>("createNewPartnerStaff", null, payload));
        final String jsonResponseCreatingStaff = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCreatingStaff);

        if (jsonResponseCreatingStaff == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<TransportPartnerRepresentor> responseCreatingStaff = objectMapper.readValue(jsonResponseCreatingStaff, new TypeReference<Response<TransportPartnerRepresentor>>() {});
        if (responseCreatingStaff.getError()) {
            throw new InternalError(responseCreatingStaff.getMessage());
        }

        return responseCreatingStaff.getData();
    }
}
