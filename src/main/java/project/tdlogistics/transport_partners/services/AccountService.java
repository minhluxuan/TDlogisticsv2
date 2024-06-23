package project.tdlogistics.transport_partners.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.transport_partners.entities.Account;
import project.tdlogistics.transport_partners.entities.Request;
import project.tdlogistics.transport_partners.entities.Response;

@Service
public class AccountService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.users";

    public Account checkExistAccount(Account criteria) throws JsonProcessingException {
        final String jsonRequestCheckingExistAccount = objectMapper.writeValueAsString(new Request<Account>("checkExistStaffAccount", null, criteria));
        final String jsonResponseCheckingExistAccount = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCheckingExistAccount);
        if (jsonResponseCheckingExistAccount == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<Account> responseCheckingExistAccount = objectMapper.readValue(jsonResponseCheckingExistAccount, new TypeReference<Response<Account>>() {});
        if (responseCheckingExistAccount.getError()) {
            throw new InternalError(responseCheckingExistAccount.getMessage());
        }

        return responseCheckingExistAccount.getData();
    }

    public Account createNewAccount(Account payload) throws JsonProcessingException{
        final String jsonRequestCreatingNewAccount = objectMapper.writeValueAsString(new Request<Account>("createNewAccount", null, payload));
        final String jsonResponseCreatingNewAccount = (String) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestCreatingNewAccount);
        if (jsonResponseCreatingNewAccount == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<Account> responseCreatingNewAccount = objectMapper.readValue(jsonResponseCreatingNewAccount, new TypeReference<Response<Account>>() {});
        if (responseCreatingNewAccount.getError()) {
            System.out.println(responseCreatingNewAccount);
            throw new InternalError(responseCreatingNewAccount.getMessage());
        }

        return responseCreatingNewAccount.getData();
    }
}
