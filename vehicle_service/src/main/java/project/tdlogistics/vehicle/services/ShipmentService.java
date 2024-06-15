package project.tdlogistics.vehicle.services;

import java.util.Optional;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.vehicle.entities.Request;
import project.tdlogistics.vehicle.entities.Response;
import project.tdlogistics.vehicle.entities.Shipment;

@Service
public class ShipmentService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.shipment";

    public Optional<Shipment> getOneShipment(String shipmentId) throws JsonProcessingException {
        String jsonRequestGettingShipment = objectMapper
                .writeValueAsString(new Request<Shipment>("getOneShipment", null, new Shipment(shipmentId)));
        String jsonResponseGettingShipment = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey,
                jsonRequestGettingShipment);
        if (jsonResponseGettingShipment == null) {
            throw new InternalError("An error occurred. Please try again");
        }

        Response<Shipment> responseGettingShipment = objectMapper.readValue(jsonResponseGettingShipment,
                new TypeReference<Response<Shipment>>() {
                });

        if (responseGettingShipment.getError()) {
            throw new InternalError("An error occurred. Please try again");
        }

        return Optional.ofNullable(responseGettingShipment.getData());
    }
}