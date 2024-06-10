// package project.tdlogistics.orders.services;

// import java.util.HashMap;
// import java.util.List;

// import org.springframework.amqp.core.AmqpTemplate;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import project.tdlogistics.orders.entities.Request;
// import project.tdlogistics.orders.entities.Response;
// // import project.tdlogistics.orders.entities.Staff;
// // import project.tdlogistics.orders.entities.UnitRequest;

// @Service
// public class AdministrativeService {
//     @Autowired
//     private AmqpTemplate amqpTemplate;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private final String exchange = "rpc-direct-exchange";
//     private final String routingKey = "rpc.administrative";

//     public UnitRequest checkExistWard(UnitRequest criteria) throws JsonProcessingException {
//         String jsonRequestGettingAdministrative = objectMapper.writeValueAsString(new Request<UnitRequest>("checkExistWard", null, criteria));
//         String jsonResponseGettingAdministrative = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestGettingAdministrative);
//         if (jsonResponseGettingAdministrative == null) {
//             throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
//         }

//         Response<UnitRequest> responseGettingAdministrative = objectMapper.readValue(jsonResponseGettingAdministrative, new TypeReference<Response<UnitRequest>>() {});
            
//         if (responseGettingAdministrative.getError()) {
//             throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
//         }

//         return responseGettingAdministrative.getData();
//     }

//     public List<UnitRequest> findWards(UnitRequest criteria) throws JsonProcessingException {
//         String jsonRequestGettingAdministrative = objectMapper.writeValueAsString(new Request<UnitRequest>("findWards", null, criteria));
//         String jsonResponseGettingAdministrative = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestGettingAdministrative);
//         if (jsonResponseGettingAdministrative == null) {
//             throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
//         }

//         Response<List<UnitRequest>> responseGettingAdministrative = objectMapper.readValue(jsonResponseGettingAdministrative, new TypeReference<Response<List<UnitRequest>>>() {});
            
//         if (responseGettingAdministrative.getError()) {
//             throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
//         }

//         return responseGettingAdministrative.getData();
//     }

//     public void updateAdministrativeUnit(HashMap<String, Object> criteria, UnitRequest payload) throws JsonProcessingException {
//         String jsonRequestUpdatingUnitRequest = new ObjectMapper().writeValueAsString(new Request<UnitRequest>("updateAdministrativeUnit", criteria, payload));
//         amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestUpdatingUnitRequest);
//     }
// }
