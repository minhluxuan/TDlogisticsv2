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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import jakarta.transaction.Transactional;
import project.tdlogistics.shipments.entities.ListResponse;
import project.tdlogistics.shipments.entities.Shipment;
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

    // public Optional<Shipment> checkExistShipment(Shipment criteria, String postalCode) {
    //     ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
    //     Example<Shipment> example = Example.of(criteria, matcher);
    //     List<Shipment> shipments = shipmentRepository.findAll(example);
    //     if (shipments.size() == 1) {
    //         final Shipment shipment = shipments.get(0);
    //         return Optional.of(shipment);
    //     } else {
    //         return Optional.empty();
    //     }
    // }

    public boolean checkExistShipment(String shipmentId, String postalCode) {
        Shipment shipment = shipmentRepositoryImplement.getOneShipment(shipmentId, postalCode);
        if(shipment != null) {
            return true;
        }
        return false;
    }


    public Shipment getOneShipment(String shipemtId, String postalCode) {
        return shipmentRepositoryImplement.getOneShipment(shipemtId, postalCode);
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

    public ListResponse decomposeShipment (List<String> orderIds, String shipmentId, String agencyId, String postalCode) {
        int updatedNumber = 0;
        List<String> updatedArray = new ArrayList<>();
        Set<String> orderIdsSet = new HashSet<>(orderIds);
        
        // Format the current date and time
        String formattedTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        // Fetch agency details
        // Agency Service
        // Map<String, Object> agency = findOneIntersect("agency", Collections.singletonList("agency_id"), Collections.singletonList(agencyId));
        // if (agency == null || !agency.containsKey("agency_name")) {
        //     throw new Exception("Agency not found");
        // }
        // String agencyName = agency.get("agency_name").toString();
        // String orderMessage = formattedTime + ": Đơn hàng đã đến " + agencyName;

        String agencyName = "Bưu cục Quận 1";
        String orderMessage = formattedTime + ": Đơn hàng đã đến " + agencyName;
        if(postalCode == null) {

            // Order service
            // Update orders
            // for (String orderId : orderIdsSet) {
            //     int resultUpdatingOneOrder = updateOne("orders", Collections.singletonList("parent"), Collections.singletonList(null), Collections.singletonList("order_id"), Collections.singletonList(orderId));
            //     boolean resultUpdatingOneOrderStatus = orderService.setJourney(orderId, orderMessage, "enter_agency");
        
            //     if (resultUpdatingOneOrder > 0 && resultUpdatingOneOrderStatus) {
            //         updatedNumber++;
            //         updatedArray.add(orderId);
            //     }
            // }

            for (String orderId : orderIdsSet) {
                if (true) {
                    updatedNumber++;
                    updatedArray.add(orderId);
                }
            }
            updateShipmentStatus(shipmentId, 6, null);

            return new ListResponse(updatedNumber, updatedArray, 0, null); 
        }
        // Order service
        // Update orders
        // for (String orderId : orderIdsSet) {
        //     int resultUpdatingOneOrder = updateOne("orders", Collections.singletonList("parent"), Collections.singletonList(null), Collections.singletonList("order_id"), Collections.singletonList(orderId));
        //     boolean resultUpdatingOneOrderStatus = orderService.setJourney(orderId, orderMessage, "enter_agency");
        //*Set order message in agency table */
        //     if (resultUpdatingOneOrder > 0 && resultUpdatingOneOrderStatus) {
        //         updatedNumber++;
        //         updatedArray.add(orderId);
        //     }
        // }

        for (String orderId : orderIdsSet) {
            if (true) {
                updatedNumber++;
                updatedArray.add(orderId);
            }
        }
        updateShipmentStatus(shipmentId, 6, null);

        return new ListResponse(updatedNumber, updatedArray, 0, null);
    }

    public boolean confirmCreateShipment(Shipment shipment, String postalCode) {
        String shipmentTable = (postalCode == null) ? "shipment" : (postalCode + "_shipment");
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();
    
        // Manually add fields and corresponding values
        fields.add("shipment_id");
        values.add(shipment.getShipmentId());
    
        if (shipment.getAgencyId() != null) {
            fields.add("agency_id");
            values.add(shipment.getAgencyId());
        }
    
        if (shipment.getAgencyIdDest() != null) {
            fields.add("agency_id_dest");
            values.add(shipment.getAgencyIdDest());
        }
    
        if (shipment.getLongSource() != null) {
            fields.add("long_source");
            values.add(shipment.getLongSource());
        }
    
        if (shipment.getLatSource() != null) {
            fields.add("lat_source");
            values.add(shipment.getLatSource());
        }
    
        if (shipment.getCurrentAgencyId() != null) {
            fields.add("current_agency_id");
            values.add(shipment.getCurrentAgencyId());
        }
    
        if (shipment.getCurrentLat() != null) {
            fields.add("current_lat");
            values.add(shipment.getCurrentLat());
        }
    
        if (shipment.getCurrentLong() != null) {
            fields.add("current_long");
            values.add(shipment.getCurrentLong());
        }
    
        if (shipment.getLongDestination() != null) {
            fields.add("long_destination");
            values.add(shipment.getLongDestination());
        }
    
        if (shipment.getLatDestination() != null) {
            fields.add("lat_destination");
            values.add(shipment.getLatDestination());
        }
    
        if (shipment.getTransportPartnerId() != null) {
            fields.add("transport_partner_id");
            values.add(shipment.getTransportPartnerId());
        }
    
        if (shipment.getStaffId() != null) {
            fields.add("staff_id");
            values.add(shipment.getStaffId());
        }
    
        if (shipment.getVehicleId() != null) {
            fields.add("vehicle_id");
            values.add(shipment.getVehicleId());
        }
    
        if (shipment.getMass() != null) {
            fields.add("mass");
            values.add(shipment.getMass());
        }
    
        if (shipment.getOrderIds() != null) {
            fields.add("order_ids");
            values.add(shipment.getOrderIds());
        }
    
        if (shipment.getParent() != null) {
            fields.add("parent");
            values.add(shipment.getParent());
        }
    
        if (shipment.getStatus() != null) {
            fields.add("status");
            values.add(shipment.getStatus());
        }
    
        if (shipment.getCreatedAt() != null) {
            fields.add("created_at");
            values.add(shipment.getCreatedAt());
        }
    
        if (shipment.getLastUpdate() != null) {
            fields.add("last_update");
            values.add(shipment.getLastUpdate());
        }
    
        if (shipment.getJourney() != null) {
            fields.add("journey");
            values.add(shipment.getJourney());
        }
    
        return dbUtils.insert(shipmentTable, fields, values) > 0;
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

        // Use reflection to get fields and values
        for (Field field : Shipment.class.getDeclaredFields()) {
            field.setAccessible(true); // Ensure we can access private fields
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
        
        return dbUtils.find(shipmentTable, fields, values, false, null, null, Shipment.class);
    }

}
