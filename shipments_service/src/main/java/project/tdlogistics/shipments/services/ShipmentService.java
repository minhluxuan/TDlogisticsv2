package project.tdlogistics.shipments.services;

import java.util.ArrayList;
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

import project.tdlogistics.shipments.entities.Shipment;
import project.tdlogistics.shipments.repositories.ShipmentRepository;

@Service
public class ShipmentService {
    // Implement relative methods here

    @Autowired
    private ShipmentRepository shipmentRepository;

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
