package project.tdlogistics.agency_company.controllers;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.Request;
import project.tdlogistics.agency_company.entities.Response;
import project.tdlogistics.agency_company.services.AgencyService;

@Controller
public class AgencyRpcController {
    @Autowired
    private AgencyService agencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "rpc.agency")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            Request<Agency> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Agency>>() {});
            switch (request.getOperation()) {
                case "checkExistAgency":
                    return checkExistAgency(request.getPayload());
                case "findOneAgency":
                    return getOneAgency(request.getPayload());
                default:
                    return objectMapper.writeValueAsString(new Response<>(400, true, "Yêu cầu không hợp lệ", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistAgency(Agency criteria) throws JsonProcessingException {
        try {
            final Optional<Agency> optionalAgency = agencyService.checkExistAgency(criteria.getAgencyId());
            if (optionalAgency.isPresent()) {
                return objectMapper.writeValueAsString(new Response<Agency>(false, String.format("Bưu cục %s đã tồn tại.", criteria.getAgencyId()), optionalAgency.get()));
            }
            return objectMapper.writeValueAsString(new Response<Agency>(false, String.format("Bưu cục %s không tồn tại.", criteria.getAgencyId()), null));
        } catch (Exception e) {
            return objectMapper.writeValueAsString(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String getOneAgency(Agency criteria) throws JsonProcessingException {
        try {
            final Optional<Agency> optionalAgency = agencyService.getOneAgency(criteria);
            if (optionalAgency.isPresent()) {
                return objectMapper.writeValueAsString(new Response<Agency>(false, String.format("Lấy thông tin bưu cục %s thành công.", criteria.getAgencyId()), optionalAgency.get()));
            }
            return objectMapper.writeValueAsString(new Response<Agency>(true, String.format("Bưu cục %s không tồn tại.", criteria.getAgencyId()), null));
        } catch (Exception e) {
            return objectMapper.writeValueAsString(new Response<Agency>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
