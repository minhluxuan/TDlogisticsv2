package project.tdlogistics.shipments.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import project.tdlogistics.shipments.entities.ListResponse;
import project.tdlogistics.shipments.entities.Shipment;
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

    // @Autowired
    // private AgencyRepository agencyRepository;

    public Shipment createNewShipment(Shipment shipment, String userRole) throws JsonProcessingException {
        // Set created time and format it
        Date createdTime = new Date();
        SimpleDateFormat setDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTime = setDateFormat.format(createdTime);

        String agencyIdSource = shipment.getAgencyId();
        String shipmentId = generateShipmentId(agencyIdSource, createdTime);
        shipment.setShipmentId(shipmentId);
        shipment.setCreatedAt(createdTime);
        shipment.setLastUpdate(createdTime);
        if(shipment.getAgencyIdDest() != null) {
            // Agency agency = agencyRepository.findOneByAgencyId(shipment.getAgencyIdDest());
            // shipment.setLatDestination(agency.getLatitude());
            // shipment.setLongDestination(agency.getLongitude());
        }

        Map<String, String> journeyInfo = new HashMap<>();

        if(userRole.equals("AGENCY_MANAGER") || userRole.equals("AGENCY_TELLER")) {
            // const postalCode = utils.getPostalCodeFromAgencyID(req.user.agency_id);
            final String postalCode = "71000"; 
            journeyInfo.put("time", formattedTime);
            journeyInfo.put("message", String.format("Lô hàng được tạo tại Bưu cục/Đại lý %s bởi nhân viên %s.", "TD_71000_089204006685", "TD_71000_0123456789"));
        } 
        else if(userRole.equals("MANAGER") || userRole.equals("TELLER")) {
            // const postalCode = utils.getPostalCodeFromAgencyID(req.user.agency_id);
            final String postalCode = "00001";
            journeyInfo.put("time", formattedTime);
            journeyInfo.put("message", String.format("Lô hàng được tạo tại trung tâm chia chọn %s bởi nhân viên %s.", "TD_00001_089204006685", "TD_00001_0123456789"));
        } 
        else if(userRole.equals("ADMIN")) {
            journeyInfo.put("time", formattedTime);
            journeyInfo.put("message", String.format("Lô hàng được tạo tại Tổng cục %s bởi nhân viên %s.", "TD_00001_089204006685", "TD_00001_0123456789"));      
        }
        // Create a list to hold the journey info maps
        List<Map<String, String>> journeyInfoList = new ArrayList<>();
        journeyInfoList.add(journeyInfo);
        
        // Without converter
        // Convert journeyInfoList to JSON string
        // ObjectMapper objectMapper = new ObjectMapper(); // Assuming you have Jackson ObjectMapper
        // String journeyInfoJson = objectMapper.writeValueAsString(journeyInfoList);

        // With converter
        shipment.setStatus(0);
        shipment.setJourney(journeyInfoList);


        shipmentRepository.save(shipment);
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

    public Optional<Shipment> getOneShipment(String shipemtId) {
        return shipmentRepository.findOneByShipmentId(shipemtId);
    }

    public Optional<Shipment> getOneShipment(String shipemtId, String postalCode) {
        String tableName = postalCode + "_shipment";
        return shipmentRepository.findOneByShipmentId(shipemtId, tableName);
    }
 
    public ListResponse addOrders(Shipment shipment, List<String> orderIds, String postalCode) {

        
        int acceptedNumber = 0;
        List<String> acceptedArray = new ArrayList<>();
        int notAcceptedNumber = 0;
        List<String> notAcceptedArray = new ArrayList<>();
        
        List<String> prevOrderIds = shipment.getOrderIds();
        if (prevOrderIds.size() == 0) {
            prevOrderIds = new ArrayList<>();
        }

        for (String orderId : orderIds) {
            if (!prevOrderIds.contains(orderId) && updateParentAndIncreaseMass(shipment, orderId, postalCode)) {
                prevOrderIds.add(orderId);
                acceptedNumber++;
                acceptedArray.add(orderId);
            } else {
                notAcceptedNumber++;
                notAcceptedArray.add(orderId);
            }
        }

        shipment.setOrderIds(prevOrderIds);
        if(postalCode == null) {
            shipmentRepository.save(shipment);
        } else {
            final String shipmentTable = postalCode + "_shipment";
            List<String> fields = Arrays.asList("order_ids");
            List<Object> values = Arrays.asList(shipment.getOrderIds());
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

        shipment.setOrderIds(prevOrderIds);
        if(postalCode == null) {
            shipmentRepository.save(shipment);
        } else {
            final String shipmentTable = postalCode + "_shipment";
            List<String> fields = Arrays.asList("order_ids");
            List<Object> values = Arrays.asList(shipment.getOrderIds());
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

        // Without converter

        // List<Map<String, String>> journey = new ArrayList<>();
        // try {
        //     journey = new ObjectMapper().readValue(shipment.getJourney(), new TypeReference<List<Map<String, String>>>(){});
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        
        // With coverter
        List<Map<String, String>> journey = shipment.getJourney();

        Map<String, String> newJourneyEntry = new HashMap<>();
        newJourneyEntry.put("time", updatedTime);
        newJourneyEntry.put("message", message);
        journey.add(newJourneyEntry);

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

    public List<Map<String, String>> getJourney(String shipmentId) {
        Shipment shipment = shipmentRepositoryImplement.getOneShipment(shipmentId, null);
    
        if (shipment == null) {
            System.out.println("Shipment with shipment_id " + shipmentId + " not found");
            return new ArrayList<>(); // Return an empty list
        }
    
        return shipment.getJourney();
    }

}
