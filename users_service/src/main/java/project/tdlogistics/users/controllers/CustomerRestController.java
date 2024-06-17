package project.tdlogistics.users.controllers;

import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Request;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.services.AccountService;
import project.tdlogistics.users.services.CustomerService;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/v2/customers")
public class CustomerRestController {
    @Autowired
    CustomerService customerService;

    @Autowired
    AccountService accountService;

    @Autowired
    Validator validator;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    @GetMapping("/check")
    public ResponseEntity<Response<Customer>> checkExistCustomer(@RequestHeader HttpHeaders headers, @RequestBody Customer criteria) throws Exception {
        try {
            final String role = headers.getFirst("role"); // thay role bằng bất kỳ giá trị nào để test
            if (!List.of("CUSTOMER").contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            final Optional<Customer> customerOptional = customerService.checkExistCustomer(criteria);
            if (customerOptional.isPresent()) {
                final Response response = new Response<>(false, "Khách hàng đã tồn tại.", customerOptional.get());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(false, "Khách hàng không tồn tại.", null));

            // String jsonRequest = new ObjectMapper().writeValueAsString(new Request<Customer>("checkExistCustomer", null, criteria));
            // String jsonResponse = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.users", jsonRequest);
            // if (jsonResponse != null) {
            //     Response<Customer> response = ((new ObjectMapper()).registerModule(new JavaTimeModule())).readValue(jsonResponse, new TypeReference<Response<Customer>>(){});
                
            //     if (response.getError()) {
            //         return ResponseEntity.status(response.getStatus()).body(new Response<Customer>(response.getError(), response.getMessage(), response.getData()));
            //     }
                
            //     if (response.getData() == null) {
            //         return ResponseEntity.status(HttpStatus.OK).body(new Response<Customer>(false, "Khách hàng không tồn tại.", null));
            //     }

            //     return ResponseEntity.status(HttpStatus.OK).body(new Response<Customer>(false, "Khách hàng đã tồn tại.", response.getData()));
            // }

            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Customer>(false, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Customer>>> getCustomers(@RequestBody Customer criteria) throws Exception {
        try {
            final List<Customer> customers = customerService.getCustomers(criteria);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(false, "Lấy thông tin khách hàng thành công", customers));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Customer>> updateCustomer(@RequestParam String id, @RequestBody Customer info) throws Exception {
        try {
            final Customer customer = customerService.updateCustomerInfo(id, info);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(true, "Khách hàng không tồn tại", null));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(false, "Cập nhật thông tin khách hàng thành công", customer));
        
            // HashMap<String, Object> params = new HashMap<String, Object>();
            // params.put("id", id);
            // final String jsonRequest = (new ObjectMapper()).writeValueAsString(new Request<Customer>("updateCustomer", params, info));
            // final String jsonResponse = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc", jsonRequest);
            // if (jsonResponse != null) {
            //     final Response<Customer> response = (new ObjectMapper().registerModule(new JavaTimeModule())).readValue(jsonResponse, new TypeReference<Response<Customer>>() {});
            //     if (response.getError() || response.getData() == null) {
            //         return ResponseEntity.status(response.getStatus()).body(new Response<Customer>(response.getError(), response.getMessage(), response.getData()));
            //     }

            //     return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Customer>(false, "Cập nhật thông tin khách hàng thành công", response.getData()));
            // }

            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<Customer>(false, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }
}
