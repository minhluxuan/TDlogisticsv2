package project.tdlogistics.shipments.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import project.tdlogistics.shipments.entities.Shipment;
import project.tdlogistics.shipments.services.ShipmentService;
import project.tdlogistics.shipments.entities.Request;
import project.tdlogistics.shipments.entities.Response;

@Controller
public class ShipmentRpcController {

    @Autowired
    private ShipmentService shipmentService;

    @RabbitListener(queues = "rpc.shipments")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<Shipment> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Shipment>>() {});
            switch (request.getOperation()) {
                // Handle each operation here
                case "findOneShipment":
                    return getOneShipment(request.getPayload());
                case "setStatus":
                    return setStatus(request.getPayload());
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Shipment>(400, true, "Yêu cầu không hợp lệ", null));
            }           
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Shipment>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String getOneShipment(Shipment shipment) throws JsonProcessingException {
        try {
            final String shipmentId = shipment.getShipmentId();
            final String agencyId = shipment.getAgencyId();
            final Shipment resultFindingShipment = shipmentService.getOneShipment(shipmentId, null);
            if(resultFindingShipment == null) {
                return (new ObjectMapper()).writeValueAsString(new Response<Shipment>(404, true, "Lô hàng không tồn tại.", null));
            }

            if(agencyId != null && (agencyId != resultFindingShipment.getAgencyId())) {
                return (new ObjectMapper()).writeValueAsString(new Response<Shipment>(404, true, "Lô hàng không tồn tại.", null));
            }

            return (new ObjectMapper()).writeValueAsString(new Response<Shipment>(200, false, "Tìm kiếm lô hàng thành công", resultFindingShipment));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String setStatus(Shipment shipment) throws JsonProcessingException {
        try {
            final String shipmentId = shipment.getShipmentId();
            final int statusCode = shipment.getStatus();
            final int resultUpdatingStatus = shipmentService.updateShipmentStatus(shipmentId, statusCode, null);
            if(resultUpdatingStatus == 0) {
                return (new ObjectMapper()).writeValueAsString(new Response<>(404, true, "Cập nhật trạng thái lô hàng thất bại", null));
            }

            String[] shipmentIdSubPart = shipmentId.split("_");
            if(shipmentIdSubPart[0].equals("BC") || shipmentIdSubPart[0].equals("DL")) {
                final int resultUpdatingStatusInAgency = shipmentService.updateShipmentStatus(shipmentId, statusCode, shipmentIdSubPart[1]);
                if(resultUpdatingStatusInAgency == 0) {
                    return (new ObjectMapper()).writeValueAsString(new Response<>(404, true, "Cập nhật trạng thái lô hàng thất bại", null));
                }
            }

            return (new ObjectMapper()).writeValueAsString(new Response<>(200, false, "Tìm kiếm lô hàng thành công", resultUpdatingStatus));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
