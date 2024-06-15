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
import project.tdlogistics.vehicle.entities.Task;

@Service
public class DriverService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.driver";

    public Optional<Task> getOneTask(String shipmentId, String staffId) throws JsonProcessingException {
        String jsonRequestGettingTask = objectMapper
                .writeValueAsString(new Request<Task>("getOneTask", null, new Task(shipmentId, staffId)));
        String jsonResponseGettingTask = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey,
                jsonRequestGettingTask);
        if (jsonResponseGettingTask == null) {
            throw new InternalError("An error occurred. Please try again");
        }

        Response<Task> responseGettingTask = objectMapper.readValue(jsonResponseGettingTask,
                new TypeReference<Response<Task>>() {
                });

        if (responseGettingTask.getError()) {
            throw new InternalError("An error occurred. Please try again");
        }

        return Optional.ofNullable(responseGettingTask.getData());
    }
}