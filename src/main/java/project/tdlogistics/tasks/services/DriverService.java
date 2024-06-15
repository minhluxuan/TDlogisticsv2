package project.tdlogistics.tasks.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.tasks.entities.DriverTask;
import project.tdlogistics.tasks.entities.ListResponse;
import project.tdlogistics.tasks.entities.Request;
import project.tdlogistics.tasks.entities.Response;
import project.tdlogistics.tasks.entities.Shipment;
import project.tdlogistics.tasks.repositories.DBUtils;
import project.tdlogistics.tasks.repositories.DriverRepositoryImplement;

@Service
public class DriverService {
    @Autowired
    private DriverRepositoryImplement driverRepositoryImplement;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private DBUtils dbUtils;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    public List<Map<String, Object>> getObjectCanHandleTaskForAdmin() {
        return driverRepositoryImplement.getObjectsCanHandleTaskByAdmin();
    }

    public List<Map<String, Object>> getObjectCanHandleTaskForAgency(String agencyId) {
        return driverRepositoryImplement.getObjectsCanHandleTaskByAgency(agencyId);
    }

    public ListResponse addShipmentsToVehicle(List<String> shipmentIds, String vehicleId, String agencyId) throws JsonProcessingException {
        //vehicle service
        //add shipment to vehicle
        // ListResponse resultAddingShipmentsToVehicle = await vehicleService.addShipmentToVehicle(resultGettingOneVehicle[0], req.body.shipment_ids);
        
        ListResponse resultAddingShipmentsToVehicle = new ListResponse(shipmentIds.size(), shipmentIds, 0, new ArrayList<>());
        return resultAddingShipmentsToVehicle;
    }

    public Shipment findShipment(Shipment criteria) throws JsonProcessingException {
        final String jsonRequestFindingShipment = objectMapper.writeValueAsString(new Request<Shipment>("findShipment", null, criteria));
        final String jsonResponseFindingShipment = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.shipments", jsonRequestFindingShipment);
    
        final Response<Shipment> response = objectMapper.readValue(jsonResponseFindingShipment, new TypeReference<Response<Shipment>>() {});
        if(response.getError()) {
            System.out.println(response.getMessage());
            return null;
        }
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return null;
        }
    }

    public int setShipmentStatus(Shipment criteria) throws JsonProcessingException {
        final String jsonRequestSettingStatus = objectMapper.writeValueAsString(new Request<Shipment>("setStatus", null, criteria));
        final String jsonResponseSettingStatus = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.shipments", jsonRequestSettingStatus);
    
        final Response<Integer> response = objectMapper.readValue(jsonResponseSettingStatus, new TypeReference<Response<Integer>>() {});
        if(response.getError()) {
            System.out.println(response.getMessage());
            return 0;
        }
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return 0;
        }
    }

    public ListResponse assignNewTasks(List<String> shipmentIds, String staffId, String vehicleId, String postalCode) {
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();

        String tasksTable = (postalCode == null) ? "driver_tasks" : (postalCode + "_driver_tasks");

        List<String> fields = Arrays.asList("shipment_id", "staff_id", "vehicle_id");
        for(String shipemntId : shipmentIds) {
            List<Object> values = Arrays.asList(shipemntId, staffId, vehicleId);
            int resultCreateNewTaks = dbUtils.insert(tasksTable, fields, values);
            if(resultCreateNewTaks > 0) {
                acceptedNumber++;
                acceptedArray.add(shipemntId);
            } 
            else {
                notAcceptedNumber++;
                notAcceptedArray.add(shipemntId);
            }
        }
        return new ListResponse(acceptedNumber, acceptedArray, notAcceptedNumber, notAcceptedArray);
    }

    public String getPostalCodeFromAgencyId(String agencyId) {
        String[] agencyIdSubParts = agencyId.split("_");
        return agencyIdSubParts[1];
    }

    public List<DriverTask> getTasks(Map<String, Object> criteria, String postalCode) {
        return driverRepositoryImplement.getTasks(criteria, postalCode);
    }

    public boolean confirmCompleteTask(int id, String postalCode) {
        final String driverTasksTable = (postalCode == null) ? "driver_tasks" : postalCode + "_driver_tasks";
        List<String> conditionFields = Arrays.asList("id");
        List<Object> conditionValues = Arrays.asList(id);

        return dbUtils.deleteOne(driverTasksTable, conditionFields, conditionValues) > 0;
    }

    public boolean deleteTask(int id, String postalCode) {
        final String driverTasksTable = (postalCode == null) ? "driver_tasks" : postalCode + "_driver_tasks";
        List<String> fields = Arrays.asList("id");
        List<Object> values = Arrays.asList(id);
        return dbUtils.deleteOne(driverTasksTable, fields, values) > 0;
    }


}
