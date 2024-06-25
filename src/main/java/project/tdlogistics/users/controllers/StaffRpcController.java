package project.tdlogistics.users.controllers;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.services.StaffService;

@Controller
public class StaffRpcController {
    @Autowired
    private StaffService staffService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "rpc.staffs")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            Request<Staff> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Staff>>() {});
            switch (request.getOperation()) {
                case "checkExistStaff":
                    return checkExistStaff(request.getPayload());
                case "createNewStaff":
                    return createNewStaff(request.getPayload());
                default:
                    return objectMapper.writeValueAsString(new Response<Account>(400, true, "Yêu cầu không hợp lệ", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistStaff(Staff criteria) throws Exception {
        try {
            Optional<Staff> staffOptional = staffService.checkExistStaff(criteria);
            if (staffOptional.isPresent()) {
                return objectMapper.writeValueAsString(new Response<Staff>(200, false, "Nhân viên đã tồn tại", staffOptional.get()));
            } else {
                return objectMapper.writeValueAsString(new Response<Staff>(200, false, "Nhân viên không tồn tại", null));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String createNewStaff(Staff info) throws Exception {
        try {
            System.out.println(info);
            final Staff newStaff = staffService.createNewStaff(info);
            return objectMapper.writeValueAsString(new Response<Staff>(200, false, "Tạo nhân viên thành công", newStaff));
        } catch (Exception e) {
            e.printStackTrace();
            return objectMapper.writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
