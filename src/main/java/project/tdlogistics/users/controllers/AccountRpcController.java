package project.tdlogistics.users.controllers;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.validation.Validator;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.services.AccountService;

@Controller
public class AccountRpcController {
    @Autowired
    AccountService accountService;

    @Autowired
    Validator validator;

    @RabbitListener(queues = "rpc.users")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<Account> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Account>>() {});
            switch (request.getOperation()) {
                case "checkExistStaffAccount":
                    return checkExistStaffAccount(request.getPayload());
                case "createNewAccount":
                    return createNewAccount(request.getPayload());
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Account>(400, true, "Yêu cầu không hợp lệ", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistStaffAccount(Account criteria) throws Exception {
        try {
            Optional<Account> accountOptional = accountService.checkExistStaffAccount(criteria);
            if (accountOptional.isPresent()) {
                return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Account>(200, false, "Tài khoản đã tồn tại", accountOptional.get()));
            } else {
                return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Account>(200, false, "Tài khoản không tồn tại", null));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String createNewAccount(Account info) throws Exception {
        try {
            final Account newAccount = accountService.createNewAccount(info);
            return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Account>(200, false, "Tài khoản đã tồn tại", newAccount));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<Account>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
