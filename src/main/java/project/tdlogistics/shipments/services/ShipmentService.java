package project.tdlogistics.shipments.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;


import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import project.tdlogistics.shipments.entities.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import jakarta.transaction.Transactional;
import project.tdlogistics.shipments.entities.Agency;
import project.tdlogistics.shipments.entities.ListResponse;
import project.tdlogistics.shipments.entities.Order;
import project.tdlogistics.shipments.entities.Request;
import project.tdlogistics.shipments.entities.Response;
import project.tdlogistics.shipments.entities.Shipment;
import project.tdlogistics.shipments.entities.UnitRequest;
import project.tdlogistics.shipments.repositories.ColumnNameMapper;
import project.tdlogistics.shipments.repositories.DBUtils;
import project.tdlogistics.shipments.repositories.ShipmentRepository;
import project.tdlogistics.shipments.repositories.ShipmentRepositoryImplement;

@Service
public class ShipmentService {
    // Implement relative methods here

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentRepositoryImplement shipmentRepositoryImplement;

    @Autowired
    private DBUtils dbUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";


    public Shipment createNewShipment(Shipment shipment, Role userRole) throws JsonProcessingException {
        
        Date createdTime = new Date();
        SimpleDateFormat setDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTime = setDateFormat.format(createdTime);

        String agencyIdSource = shipment.getAgencyId();
        String shipmentId = generateShipmentId(agencyIdSource, createdTime);
        shipment.setShipmentId(shipmentId);
        shipment.setCreatedAt(createdTime);
        shipment.setLastUpdate(createdTime);
        shipment.setOrderIds(new ArrayList<>());
        String postalCode = null;

        if(shipment.getAgencyIdDest() != null) {
            Agency agency = getOneAgency(shipment.getAgencyIdDest());
            shipment.setLatDestination(agency.getLatitude());
            shipment.setLongDestination(agency.getLongitude());
        }

        List<Map<String, String>> journeyInfo = new ArrayList<>();
        Map<String, String> journeyNode = new HashMap<>();
        boolean resultCreatingShipment = false;
        if(Set.of(Role.AGENCY_MANAGER, Role.AGENCY_TELLER).contains(userRole)) {
            
            postalCode = getPostalCodeFromAgencyId(agencyIdSource);  
            journeyNode.put("time", formattedTime);
            journeyNode.put("message", String.format("%s: Lô hàng được tạo tại Bưu cục/Đại lý %s bởi nhân viên %s.", formattedTime, "TD_71000_089204006685", "TD_71000_0123456789"));
            journeyInfo.add(journeyNode);
            shipment.setStatus(0);
            shipment.setJourney(journeyInfo);
            resultCreatingShipment = shipmentRepositoryImplement.createNewShipment(shipment, postalCode);
            shipment.setJourney(journeyInfo);
            setJourney(shipmentId, formattedTime, shipmentId, postalCode);
        } 
        else if(Set.of(Role.MANAGER, Role.TELLER).contains(userRole)) {
           
            postalCode = getPostalCodeFromAgencyId(agencyIdSource);
            journeyNode.put("time", formattedTime);
            journeyNode.put("message", String.format("%s: Lô hàng được tạo tại trung tâm chia chọn %s bởi nhân viên %s.", formattedTime, "TD_00001_089204006685", "TD_00001_0123456789"));
            journeyInfo.add(journeyNode);
            shipment.setStatus(2);
            shipment.setJourney(journeyInfo);
            shipmentRepositoryImplement.createNewShipment(shipment, postalCode);
            resultCreatingShipment = shipmentRepositoryImplement.createNewShipment(shipment, null);
            
            setJourney(shipmentId, formattedTime, shipmentId, postalCode);
            setJourney(shipmentId, formattedTime, shipmentId, null);
        } 
        else if(Set.of(Role.ADMIN).contains(userRole)) {
            journeyNode.put("time", formattedTime);
            journeyNode.put("message", String.format("%s: Lô hàng được tạo tại Tổng cục %s bởi nhân viên %s.", formattedTime, "TD_00001_089204006685", "TD_00001_0123456789"));
            journeyInfo.add(journeyNode);      
            shipment.setStatus(2);
            shipment.setJourney(journeyInfo);
            resultCreatingShipment = shipmentRepositoryImplement.createNewShipment(shipment, null);
            shipment.setJourney(journeyInfo);
            setJourney(shipmentId, formattedTime, shipmentId, null);
        }
        if(!resultCreatingShipment) {
            return null;
        }
        return shipment;
    }

    public Optional<Shipment> checkExistShipment(Shipment criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Shipment> example = Example.of(criteria, matcher);
        List<Shipment> shipments = shipmentRepository.findAll(example);
        if (shipments.size() == 1) {
            final Shipment shipment = shipments.get(0);
            return Optional.of(shipment);
        } else {
            return Optional.empty();
        }
    }

    public boolean checkExistShipment(String shipmentId, String postalCode) {
        Shipment shipment = shipmentRepositoryImplement.getOneShipment(shipmentId, postalCode);
        if(shipment != null) {
            return true;
        }
        return false;
    }

    public Shipment getOneShipment(String shipmentId, String postalCode) {
        final String table = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        List<String> fields = Arrays.asList("shipment_id");
        List<Object> values = Arrays.asList(shipmentId);
        return dbUtils.findOneIntersect(table, fields, values, Shipment.class);
    }
 
    public ListResponse addOrders(Shipment shipment, List<String> orderIds, String postalCode) {   
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();
        

        System.out.println(shipment.getOrderIds().toString());
        for (String orderId : orderIds) {
            if (!shipment.getOrderIds().contains(orderId) && updateParentAndIncreaseMass(shipment, orderId, postalCode)) {
                shipment.getOrderIds().add(orderId);
                acceptedNumber++;
                acceptedArray.add(orderId);
            } else {
                notAcceptedNumber++;
                notAcceptedArray.add(orderId);
            }
        }


        shipment.setOrderIds(shipment.getOrderIds());
        if(postalCode == null) {
            shipmentRepository.save(shipment);
        } else {
            final String shipmentTable = postalCode + "_shipment";
            String stringifyOrderIds;
            try {
                stringifyOrderIds = objectMapper.writeValueAsString(shipment.getOrderIds());
            } catch (JsonProcessingException e) {
                System.out.println("Error converting OrderIds to JSON: " + e.getMessage());
                return null;
            }
            List<String> fields = Arrays.asList("order_ids");
            List<Object> values = Arrays.asList(stringifyOrderIds);
            List<String> conditionFields = Arrays.asList("shipment_id");
            List<Object> conditionValues = Arrays.asList(shipment.getShipmentId());
            dbUtils.update(shipmentTable, fields, values, conditionFields, conditionValues);
        }

        return new ListResponse(acceptedNumber, acceptedArray, notAcceptedNumber, notAcceptedArray);
    }

    public boolean updateParentAndIncreaseMass(Shipment shipment, String orderId, String postalCode) {
        return shipmentRepositoryImplement.updateParentAndIncreaseMass(shipment, orderId, postalCode);
    }

    public ListResponse removeOrders(Shipment shipment, List<String> orderIds, String postalCode) {     
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();
        
        List<String> prevOrderIds = shipment.getOrderIds();
        if (prevOrderIds.size() == 0) {
            prevOrderIds = new ArrayList<>();
        }

        for (String orderId : orderIds) {
            if (prevOrderIds.contains(orderId) && updateParentAndDecreaseMass(shipment, orderId, postalCode)) {
                prevOrderIds.remove(orderId);
                acceptedNumber++;
                acceptedArray.add(orderId);
            } else {
                notAcceptedNumber++;
                notAcceptedArray.add(orderId);
            }
        }
        System.out.println(prevOrderIds.toString());
        shipment.setOrderIds(prevOrderIds);
        if(postalCode == null) {
            shipmentRepository.save(shipment);
        } else {
            final String shipmentTable = postalCode + "_shipment";
            String stringifyOrderIds;
            try {
                stringifyOrderIds = objectMapper.writeValueAsString(shipment.getOrderIds());
            } catch (JsonProcessingException e) {
                System.out.println("Error converting OrderIds to JSON: " + e.getMessage());
                return null;
            }
            List<String> fields = Arrays.asList("order_ids");
            List<Object> values = Arrays.asList(stringifyOrderIds);
            List<String> conditionFields = Arrays.asList("shipment_id");
            List<Object> conditionValues = Arrays.asList(shipment.getShipmentId());
            dbUtils.update(shipmentTable, fields, values, conditionFields, conditionValues);
        }

        return new ListResponse(acceptedNumber, acceptedArray, notAcceptedNumber, notAcceptedArray);
    }

    public boolean updateParentAndDecreaseMass(Shipment shipment, String orderId, String postalCode) {     
        return shipmentRepositoryImplement.updateParentAndDecreaseMass(shipment, orderId, postalCode);
    }

    public String generateShipmentId(String agencyId, Date createdTime) {
        if (agencyId == null || agencyId.isEmpty() || !agencyId.contains("_")) {
            throw new IllegalArgumentException("Invalid agencyId format. It must contain underscores.");
        }
        String[] agencyIdSubParts = agencyId.split("_");
        SimpleDateFormat setDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return agencyIdSubParts[0] + '_' + agencyIdSubParts[1] + '_' + setDateFormat.format(createdTime);
    }

    public String getPostalCodeFromAgencyId(String agencyId) {
        String[] agencyIdSubParts = agencyId.split("_");
        return agencyIdSubParts[1];
    }

    public boolean addOneShipmentToVehicle(String shipmentId, String staffId) {
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            shipmentId = objectMapper.writeValueAsString(Collections.singletonList(shipmentId));
        } catch (JsonProcessingException e) {
           return false;
        }
        
        List<String> fields = Arrays.asList("shipment_ids");
        List<Object> values = Arrays.asList(shipmentId);
        List<String> conditionFields = Arrays.asList("staff_id");
        List<Object> conditionValues = Arrays.asList(staffId);
        final int affectedRows = dbUtils.updateOne("vehicle", fields, values, conditionFields, conditionValues);
        return (affectedRows > 0);
    }

    public ListResponse updateOrders(List<String> orderIds, String staffId, String postalCode) {
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();

        List<String> fields = Arrays.asList("shipper");
        List<Object> values = Arrays.asList(staffId);
        List<String> conditionFields = Arrays.asList("order_id");

        for (String orderId : orderIds) { 
            List<Object> conditionValues = Arrays.asList(orderId);

            int resultAssigningShipperToOrderInAgency = dbUtils.updateOne(postalCode + "_orders", fields, values, conditionFields, conditionValues);
            if (resultAssigningShipperToOrderInAgency > 0) {
                int resultAssignShipperToDatabase = dbUtils.updateOne("orders", fields, values, conditionFields, conditionValues);

                if (resultAssignShipperToDatabase > 0) {
                    acceptedNumber++;
                    acceptedArray.add(orderId);
                } else {
                    notAcceptedNumber++;
                    notAcceptedArray.add(orderId);
                }
            } else {
                notAcceptedNumber++;
                notAcceptedArray.add(orderId);
            }
        }

        return new ListResponse(acceptedNumber, acceptedArray, notAcceptedNumber, notAcceptedArray);
    }

    public boolean setJourney(String shipmentId, String updatedTime, String message, String postalCode) {
         
        Shipment shipment = shipmentRepositoryImplement.getOneShipment(shipmentId, postalCode);

        if (shipment == null) {
            System.out.println("Shipment with shipment_id " + shipmentId + " not found");
            return false;
        }

        message = updatedTime + ": " + message;

        // With coverter
        List<Map<String, String>> journey = shipment.getJourney();
        if(journey == null) {
            journey = new ArrayList<>();
        }

        Map<String, String> journeyNode = new HashMap<>();
        journeyNode.put("time", updatedTime);
        journeyNode.put("message", message);

        journey.add(journeyNode);
        String stringifyJourney;
        try {
            stringifyJourney = objectMapper.writeValueAsString(journey);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting journey to JSON: " + e.getMessage());
            return false;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("journey", stringifyJourney);

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("shipment_id", shipmentId);


        List<String> fields = Arrays.asList("journey");
        List<Object> values = Arrays.asList(stringifyJourney);
        List<String> conditionFields = Arrays.asList("shipment_id");
        List<Object> conditionValues = Arrays.asList(shipmentId);

        if(postalCode != null) {
            dbUtils.updateOne(postalCode + "_shipment", fields, values, conditionFields, conditionValues);
        }

        final int affectedRows = dbUtils.updateOne("shipment", fields, values, conditionFields, conditionValues);

        return affectedRows > 0;

    }

    public List<Map<String,String>> getJourney(String shipmentId) {
        Shipment shipment = shipmentRepositoryImplement.getOneShipment(shipmentId, null);
    
        if (shipment == null) {
            System.out.println("Shipment with shipment_id " + shipmentId + " not found");
            return new ArrayList<>(); // Return an empty list
        }
    
        return shipment.getJourney();
    }


    public ListResponse compareOrdersInRequestWithOrdersInShipment(List<String> requestOrderIds, List<String> shipmentOrderIds) {
        Set<String> requestOrderIdsSet = new HashSet<>(requestOrderIds);
        Set<String> shipmentOrderIdsSet = new HashSet<>(shipmentOrderIds);

        int hitNumber = 0;
        List<String> hitArray = new ArrayList<>();
        int missNumber = 0;
        List<String> missArray = new ArrayList<>();

        for (String orderId : shipmentOrderIdsSet) {
            if (requestOrderIdsSet.contains(orderId)) {
                hitNumber++;
                hitArray.add(orderId);
            } else {
                missNumber++;
                missArray.add(orderId);
            }
        }

        return new ListResponse(hitNumber, hitArray, missNumber, missArray);
    }

    public int updateShipmentStatus(String shipmentId, int status, String postalCode) {
        return shipmentRepositoryImplement.updateStatus(shipmentId, status, postalCode);
    }

    public ListResponse decomposeShipment (List<String> orderIds, String shipmentId, String agencyId, String postalCode) throws Exception {
        int updatedNumber = 0;
        List<String> updatedArray = new ArrayList<>();
        int notUpdatedNumber = 0;
        List<String> notUpdatedArray = new ArrayList<>();
        Set<String> orderIdsSet = new HashSet<>(orderIds);
        
        // Format the current date and time
        String formattedTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        Agency agency = getOneAgency(agencyId);
        if(agency == null) {
            throw new Exception("Agency not found");
        }

        String agencyName = agency.getAgencyName();
        String orderMessage = formattedTime + ": Đơn hàng đã đến " + agencyName;

        Order criteria = new Order();
        for (String orderId : orderIdsSet) {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("orderId", orderId);

            Order resultGettingOrder = getOneOrder(orderId);
            if(resultGettingOrder == null) {
                notUpdatedNumber++;
                notUpdatedArray.add(orderId);
            }

            List<String> journey = resultGettingOrder.getJourney();
            if(journey == null) {
                journey = new ArrayList<>();
            }

            journey.add(orderMessage);
        
            criteria.setJourney(journey);
            criteria.setStatusCode(12);

            final boolean resultUpdatingOrder = updateOneOrder(criteria, conditions, postalCode);
            if(resultUpdatingOrder) {
                updatedNumber++;
                updatedArray.add(orderId);
            } else {
                notUpdatedNumber++;
                notUpdatedArray.add(orderId);
            }

        }
        updateShipmentStatus(shipmentId, 6, null);

        return new ListResponse(updatedNumber, updatedArray, notUpdatedNumber, notUpdatedArray); 
       
    }

    public boolean confirmCreateShipment(Shipment shipment, String postalCode) {
        if(!shipmentRepositoryImplement.createNewShipment(shipment, null)) {
            return false;
        }
        return shipmentRepositoryImplement.createNewShipment(shipment, postalCode);
    }
    

    public ListResponse updateParentForGlobalOrders(List<String> orderIds, String shipmentId) {
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();

        final String orderTable = "orders";
        List<String> fields = Arrays.asList("parent");
        List<Object> values = Arrays.asList(shipmentId);
        List<String> conditionFields = Arrays.asList("order_id");
        for(String orderId : orderIds) {
            List<Object> conditionValues = Arrays.asList(orderId);
            int resultUpdatingParent = dbUtils.updateOne(orderTable, fields, values, conditionFields, conditionValues);
            if(resultUpdatingParent > 0) {
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

    public List<Shipment> getShipments (Shipment criteria, String postalCode) {
        String shipmentTable = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        
        for (Field field : Shipment.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(criteria);
                if (value != null) {
                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println(values.toString());

        return dbUtils.find(shipmentTable, fields, values, false, null, null, Shipment.class);
    }

    public boolean deleteShipment(String shipmentId, String postalCode) {
        final String shipmentTable = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        List<String> fields = Arrays.asList("shipment_id");
        List<Object> values = Arrays.asList(shipmentId);

        return dbUtils.deleteOne(shipmentTable, fields, values) > 0;
    }

    @SuppressWarnings("unchecked")
    public boolean pasteShipmentToAgency(Shipment shipment, String postalCode) {
        if(postalCode == null) {
            return false;
        }

        String shipmentTable = (postalCode + "_shipment");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : Shipment.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(shipment);
                if (value != null) {
                    if (field.getName().equals("journey")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListMapToJson((List<Map<String, String>>) value));
                        continue;
                    }
                    else if (field.getName().equals("orderIds")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListToJson((List<String>) value));
                        continue;
                    }
                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return dbUtils.insert(shipmentTable, fields, values) > 0;
        
    }

    public Agency getOneAgency(String agencyid) throws JsonProcessingException {
        if(agencyid == null) {
            throw new IllegalArgumentException(String.format("Thiếu thông tin bưu cục"));
        }
        Agency agency = new Agency();
        agency.setAgencyId(agencyid);   

        final String jsonRequestFindOneShipment = objectMapper.writeValueAsString(new Request<Agency>("findOneAgency", null, agency));
        final String jsonResponseFindOneShipment = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.agency", jsonRequestFindOneShipment);

        final Response<Agency> response = objectMapper.readValue(jsonResponseFindOneShipment, new TypeReference<Response<Agency>>() {});
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return null;
        }
        
    }

    public boolean updateOneOrder(Order criteria, Map<String, Object> conditions, String postalCode) throws JsonProcessingException{
        if(criteria == null) {
            throw new IllegalArgumentException(String.format("Thiếu thông tin đơn hàng"));
        }

        if(postalCode != null) {
            conditions.put("postalCode", postalCode);
        }

        HashMap<String, Object> conditionsAsHashMap = new HashMap<>(conditions);

        final String jsonRequestUpdatingOneOrder = objectMapper.writeValueAsString(new Request<Order>("updateOneOrder", conditionsAsHashMap, criteria));
        final String jsonResponseUpdatingOneOrder = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.orders", jsonRequestUpdatingOneOrder);

        final Response<Integer> response = objectMapper.readValue(jsonResponseUpdatingOneOrder, new TypeReference<Response<Integer>>() {});
        if (response != null && response.getData() != null) {
            return response.getData() > 0;
        } else {
            return false;
        }

    }

    public Order getOneOrder(String orderId) throws JsonProcessingException{
        if(orderId == null) {
            throw new IllegalArgumentException(String.format("Thiếu thông tin đơn hàng"));
        }

        Order criteria = new Order();
        criteria.setOrderId(orderId);

        final String jsonRequestFindingOneOrder = objectMapper.writeValueAsString(new Request<Order>("findOneOrder", null, criteria));
        final String jsonResponseFindingOneOrder = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.orders", jsonRequestFindingOneOrder);

        final Response<Order> response = objectMapper.readValue(jsonResponseFindingOneOrder, new TypeReference<Response<Order>>() {});
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return null;
        }

    } 

    public ListResponse cloneOrdersFromGlobalToAgency(List<String> orderIds, String postalCode) throws JsonProcessingException {
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();
        
        for(String orderId: orderIds) {
            try {
                Order order = getOneOrder(orderId);
                if(order != null) {
                    boolean resultCloningOrder = cloneOrderToAgency(order, postalCode);
                    if(resultCloningOrder) {
                        acceptedNumber++;
                        acceptedArray.add(orderId);
                    } else {
                        notAcceptedNumber++;
                        notAcceptedArray.add(orderId);
                    }
                } else {
                    notAcceptedNumber++;
                    notAcceptedArray.add(orderId);
                }
            } catch(Exception e) {
                e.printStackTrace();
                notAcceptedNumber++;
                notAcceptedArray.add(orderId);
            }
            
        }
        return new ListResponse(acceptedNumber, acceptedArray, notAcceptedNumber, notAcceptedArray);
    }

    public boolean cloneOrderToAgency(Order order, String postalCode) throws JsonProcessingException {
        if(postalCode == null) {
            return false;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("postalCode", postalCode);
    
        final String jsonRequestCloningOrder = objectMapper.writeValueAsString(new Request<>("cloneOrderToAgency", params, order));
        final String jsonResponseCloningOrder = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.orders", jsonRequestCloningOrder);

        final Response<Boolean> response = objectMapper.readValue(jsonResponseCloningOrder, new TypeReference<Response<Boolean>>() {});
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return false;
        }

    }

    @SuppressWarnings("unchecked")
    public boolean updateShipment(Shipment criteria, String shipmentId, String postalCode) {
        final String shipmentTable = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : Shipment.class.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object value = field.get(criteria);
                if (value != null) {
                    if (field.getName().equals("journey")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListMapToJson((List<Map<String, String>>) value));
                        continue;
                    }
                    else if (field.getName().equals("orderIds")) {
                        fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                        values.add(JsonUtils.convertListToJson((List<String>) value));
                        continue;
                    }
                    fields.add(ColumnNameMapper.mappingColumn(field.getName()));
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        List<String> conditionFields = Arrays.asList("shipment_id");
        List<Object> conditionValues = Arrays.asList(shipmentId);

        return dbUtils.updateOne(shipmentTable, fields, values, conditionFields, conditionValues) > 0;
    }

    public ListResponse assignTaskToShipper (List<String> orderIds, String staffId, String postalCode) throws JsonProcessingException {
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderIds", orderIds);
        params.put("staffId", staffId);
        params.put("postalCode", postalCode);

        final String jsonRequestAssigningTask = objectMapper.writeValueAsString(new Request<>("assignTask", params, null));
        final String jsonResponseAssigningTask = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.tasks", jsonRequestAssigningTask);

        final Response<ListResponse> response = objectMapper.readValue(jsonResponseAssigningTask, new TypeReference<Response<ListResponse>>() {});
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return null;
        }
    }

    public Agency findOneAgency (Agency criteria) throws JsonProcessingException {

        final String jsonRequestFindingOneAgency = objectMapper.writeValueAsString(new Request<Agency>("findOneAgency", null, criteria));
        final String jsonResponseFindingOneAgency = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.agency", jsonRequestFindingOneAgency);

        final Response<Agency> response = objectMapper.readValue(jsonResponseFindingOneAgency, new TypeReference<Response<Agency>>() {});
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return null;
        }
    }

    public String getOneDistributionCenter (String province) throws JsonProcessingException {

        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("province", province);

        final String jsonRequestFindingOneAgency = objectMapper.writeValueAsString(new Request<UnitRequest>("getOneDistributionCenter", conditions, null));
        final String jsonResponseFindingOneAgency = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestFindingOneAgency);

        final Response<String> response = objectMapper.readValue(jsonResponseFindingOneAgency, new TypeReference<Response<String>>() {});
        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return null;
        }
    }

    public List<Order> getOrdersFromShipment(List<String> orderIds) throws JsonProcessingException {
        List<Order> orders = new ArrayList<>();
        for(String orderId: orderIds) {
            Order order = getOneOrder(orderId);
            if(order != null) {
                orders.add(order);
            }
        }
        return orders;
    }

}
