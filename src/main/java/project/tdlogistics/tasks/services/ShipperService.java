package project.tdlogistics.tasks.services;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.amqp.core.AmqpTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.tasks.configurations.ListToStringConverter;
import project.tdlogistics.tasks.entities.ListResponse;
import project.tdlogistics.tasks.entities.Order;
import project.tdlogistics.tasks.entities.Response;
import project.tdlogistics.tasks.entities.Request;
import project.tdlogistics.tasks.entities.Shipment;
import project.tdlogistics.tasks.entities.ShipperTask;
import project.tdlogistics.tasks.repositories.ColumnNameMapper;
import project.tdlogistics.tasks.repositories.DBUtils;
import project.tdlogistics.tasks.repositories.ShipperRepository;
import project.tdlogistics.tasks.repositories.ShipperRepositoryImplement;

@Service
public class ShipperService {

    @Autowired
    private ShipperRepositoryImplement shipperRepositoryImplement;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private DBUtils dbUtils;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";
    // Implement relative methods here

    public List<Map<String, Object>> getObjectCanHandleTask(String agencyId) {
        return shipperRepositoryImplement.getObjectsCanHandleTask(agencyId);
    }

    // public Ward findManagedAgency(String ward, String district, String province) throws JsonProcessingException {
    //     if(ward == null || district == null || province == null) {
    //         throw new IllegalArgumentException(String.format("Thiếu thông tin đơn vị hành chính"));
    //     }

    //     final String jsonRequestCheckingExistDistrict = objectMapper.writeValueAsString(new Request<Ward>("findWards", null, new Ward(province, district, ward)));
    //     final String jsonResponseCheckingExistDistrict = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestCheckingExistDistrict);

    //     final Response<List<Ward>> response = objectMapper.readValue(jsonResponseCheckingExistDistrict, new TypeReference<Response<List<Ward>>>() {});
    //     if (response != null && response.getData() != null) {
    //         return response.getData().get(0);
    //     } else {
    //         return null;
    //     }

    // }

    public Shipment findShipment(Shipment criteria) throws JsonProcessingException {
        final String jsonRequestFindingShipment = objectMapper.writeValueAsString(new Request<Shipment>("findOneShipment", null, criteria));
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

    public ListResponse assignNewTasks(List<String> orderIds, String staffId, String postalCode) {
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();

        final String tasksTable = postalCode + "_shipper_tasks";
        List<String> fields = Arrays.asList("order_id", "staff_id", "completed");
        for(String orderId : orderIds) {
            List<Object> values = Arrays.asList(orderId, staffId, false);
            int resultCreateNewTaks = dbUtils.insert(tasksTable, fields, values);
            if (resultCreateNewTaks > 0) {
                acceptedNumber++;
                acceptedArray.add(orderId);
            } 
            else {
                notAcceptedNumber++;
                notAcceptedArray.add(orderId);
            }
        }
        return new ListResponse(acceptedNumber, acceptedArray, notAcceptedNumber, notAcceptedArray);
    }

    public List<ShipperTask> getTask(Map<String, Object> criteria, String postalCode) {
        return shipperRepositoryImplement.getTasks(criteria, postalCode);
    }

    public List<ShipperTask> getHistory(Map<String, Object> criteria, String postalCode) {
        return shipperRepositoryImplement.getHistory(criteria, postalCode);
    }

    public boolean confirmCompleteTask(int id, String staffId, String completedTime, String postalCode) {
        final String shipperTasksTable = postalCode + "_shipper_tasks";
        List<String> fields = Arrays.asList("completed_at", "completed");
        List<Object> values = Arrays.asList(completedTime, true);
        List<String> conditionFields = Arrays.asList("id", "staff_id", "completed");
        List<Object> conditionValues = Arrays.asList(id, staffId, false);

        return dbUtils.updateOne(shipperTasksTable, fields, values, conditionFields, conditionValues) > 0;
    }

    public boolean deleteTask(int id, String postalCode) {
        final String shipperTasksTable = postalCode + "_shipper_tasks";
        List<String> fields = Arrays.asList("id");
        List<Object> values = Arrays.asList(id);
        return dbUtils.deleteOne(shipperTasksTable, fields, values) > 0;
    }

    public String getPostalCodeFromAgencyId(String agencyId) {
        String[] agencyIdSubParts = agencyId.split("_");
        return agencyIdSubParts[1];
    }

}
