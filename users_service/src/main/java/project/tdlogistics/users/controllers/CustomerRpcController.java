// package project.tdlogistics.users.controllers;

// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Set;

// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Controller;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// import jakarta.validation.ConstraintViolation;
// import jakarta.validation.Validator;
// import project.tdlogistics.users.entities.Customer;
// import project.tdlogistics.users.entities.Request;
// import project.tdlogistics.users.entities.Response;
// import project.tdlogistics.users.services.CustomerService;

// @Controller
// public class CustomerRpcController {
//     @Autowired
//     CustomerService customerService;

//     @Autowired
//     Validator validator;

//     @RabbitListener(queues = "rpc.users")
//     private String handleRpcRequest(String jsonRequest) throws Exception {
//         try {
//             ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//             Request<Customer> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<Customer>>() {});
//             switch (request.getOperation()) {
//                 case "checkExistCustomer":
//                     return checkExistCustomer(request.getPayload());
//                 case "createNewCustomer":
//                     return createNewCustomer(request.getPayload());
//                 case "getCustomers":
//                     return getCustomers(request.getPayload());
//                 case "updateCustomer":
//                     return updateCustomer(request.getParams(), request.getPayload());
//                 default:
//                     return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Customer>(HttpStatus.BAD_REQUEST, true, "Yêu cầu không hợp lệ", null));
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
//         }
//     }

//     private String checkExistCustomer(Customer criteria) throws Exception {
//         try {
//             Optional<Customer> customerOptional = customerService.checkExistCustomer(criteria);
//             if (customerOptional.isPresent()) {
//                 Customer customer = customerOptional.get();
//                 return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Customer>(HttpStatus.OK, false, "Khách hàng đã tồn tại", customer));
//             } else {
//                 return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Customer>(HttpStatus.OK, false, "Khách hàng không tồn tại", null));
//             }
//         } catch (JsonProcessingException e) {
//             e.printStackTrace();
//             return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
//         }
//     }

//     private String createNewCustomer(Customer info) throws Exception {
//         try {
//             Set<ConstraintViolation<Customer>> violations = validator.validate(info);

//             if (!violations.isEmpty()) {
//                 return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.CREATED, true, "Thông tin không hợp lệ", null));
//             }

//             final Customer tempCustomer = new Customer();
//             tempCustomer.setPhoneNumber(info.getPhoneNumber());
//             Optional<Customer> customerOptional = customerService.checkExistCustomer(tempCustomer);
//             if (customerOptional.isPresent()) {
//                 return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.BAD_REQUEST, true, "Khách hàng đã tồn tại", null));
//             }

//             final Customer customer = customerService.createNewCustomer(info);
//             return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Customer>(HttpStatus.CREATED, true, "Tạo khách hàng mới thành công", customer));
//         } catch (Exception e) {
//             e.printStackTrace();
//             return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
//         }
//     }

//     private String getCustomers(Customer criteria) throws Exception {
//         try {
//             final List<Customer> customers = customerService.getCustomers(criteria);
//             return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<List<Customer>>(HttpStatus.OK, false, "Lấy thông tin khách hàng thành công", customers));
//         } catch (Exception e) {
//             e.printStackTrace();
//             return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
//         }
//     }

//     private String updateCustomer(Map<String, Object> params, Customer info) throws Exception {
//         try {
//             if (params.get("id") == null) {
//                 return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.BAD_REQUEST, true, "Trường id không được phép để trống", null));
//             }

//             final Customer customer = customerService.updateCustomerInfo((Long) params.get("id"), info);
//             if (customer == null) {
//                 return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.NOT_FOUND, true, "Khách hàng không tồn tại", null));
//             }

//             return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<Customer>(HttpStatus.CREATED, false, "Cập nhật thông tin khách hàng thành công", customer));
//         } catch (Exception e) {
//             e.printStackTrace();
//             return (new ObjectMapper()).writeValueAsString(new Response<Customer>(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
//         }
//     }
// }
