package project.tdlogistics.shipments.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.shipments.entities.ListResponse;
import project.tdlogistics.shipments.entities.Shipment;
import project.tdlogistics.shipments.repositories.DBUtils;
import project.tdlogistics.shipments.repositories.ShipmentRepository;
import project.tdlogistics.shipments.repositories.ShipmentRepositoryImplement;
import project.tdlogistics.shipments.repositories.dbUtils;

@Service
public class ShipmentService {
    // Implement relative methods here

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentRepositoryImplement shipmentRepositoryImplement;

    @Autowired
    private DBUtils dbUtils;

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

        Map<String, Object> journeyInfo = new HashMap<>();

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
        List<Map<String, Object>> journeyInfoList = new ArrayList<>();
        journeyInfoList.add(journeyInfo);
        
        // Convert journeyInfoList to JSON string
        ObjectMapper objectMapper = new ObjectMapper(); // Assuming you have Jackson ObjectMapper
        String journeyInfoJson = objectMapper.writeValueAsString(journeyInfoList);
        shipment.setStatus(0);
        shipment.setJourney(journeyInfoJson);


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


}
