package project.tdlogistics.users.controllers;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.PartnerStaff;
import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.services.PartnerStaffService;

@Controller
public class TransportPartnerStaffRpcController {
    @Autowired
    private PartnerStaffService partnerStaffService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "rpc.partner_staff")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            final Request<PartnerStaff> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<PartnerStaff>>() {});
            switch (request.getOperation()) {
                case "checkExistPartnerStaff":
                    return checkExistPartnerStaff(request.getPayload());
                case "createNewPartnerStaff":
                    return createNewPartnerStaff(request.getPayload());
                default:
                    return objectMapper.writeValueAsString(new Response<PartnerStaff>(400, true, "Yêu cầu không hợp lệ", null));
            }    
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistPartnerStaff(PartnerStaff criteria) throws JsonProcessingException {
        try {
            Optional<PartnerStaff> optionalPartnerStaff = partnerStaffService.checkExistPartnerStaff(criteria);
            if (optionalPartnerStaff.isEmpty()) {
                return objectMapper.writeValueAsString(new Response<PartnerStaff>(200, false, "Nhân viên không tồn tại", null));
            }

            return objectMapper.writeValueAsString(new Response<PartnerStaff>(200, false, "Nhân viên đã tồn tại", optionalPartnerStaff.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<PartnerStaff>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    private String createNewPartnerStaff(PartnerStaff payload) throws JsonProcessingException {
        try {
            final PartnerStaff createdPartnerStaff = partnerStaffService.createNewPartnerStaff(payload);
            createdPartnerStaff.getAccount().setPassword(null);
            return objectMapper.writeValueAsString(new Response<PartnerStaff>(201, false, "Tạo nhân viên thành công", createdPartnerStaff));
        } catch (Exception e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<PartnerStaff>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }
}
