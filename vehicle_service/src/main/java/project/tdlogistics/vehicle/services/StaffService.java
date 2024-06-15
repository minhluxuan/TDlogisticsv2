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
import project.tdlogistics.vehicle.entities.Staff;

@Service
public class StaffService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.staff";

    public Optional<Staff> getOneStaffById(String staffId) throws JsonProcessingException {
        String jsonRequestGettingStaff = objectMapper
                .writeValueAsString(new Request<Staff>("getOneStaffById", null, new Staff(staffId)));
        String jsonResponseGettingStaff = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey,
                jsonRequestGettingStaff);
        if (jsonResponseGettingStaff == null) {
            throw new InternalError("An error occurred. Please try again");
        }

        Response<Staff> responseGettingStaff = objectMapper.readValue(jsonResponseGettingStaff,
                new TypeReference<Response<Staff>>() {
                });

        if (responseGettingStaff.getError()) {
            throw new InternalError("An error occurred. Please try again");
        }

        return Optional.ofNullable(responseGettingStaff.getData());
    }
}