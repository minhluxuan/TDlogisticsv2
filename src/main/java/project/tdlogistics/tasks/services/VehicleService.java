package project.tdlogistics.tasks.services;

import project.tdlogistics.tasks.entities.Request;
import project.tdlogistics.tasks.entities.Response;
import project.tdlogistics.tasks.entities.Vehicle;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; 

@Service
public class VehicleService {
    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public Vehicle findOneVehicle(Vehicle criteria) throws JsonProcessingException {
        String jsonRequestFindingOneVehicle = objectMapper.writeValueAsString(new Request<Vehicle>("findOneVehicle", null, criteria));
        String jsonResponseFindingOneVehicle = (String) amqpTemplate.convertSendAndReceive(jsonRequestFindingOneVehicle);

        if (jsonResponseFindingOneVehicle == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<Vehicle> responseFindingOneVehicle = objectMapper.readValue(jsonResponseFindingOneVehicle, new TypeRefere);

        if (responseFindingOneVehicle.getError()) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại"); 
        }

        return responseFindingOneVehicle.getData();
    }
}
