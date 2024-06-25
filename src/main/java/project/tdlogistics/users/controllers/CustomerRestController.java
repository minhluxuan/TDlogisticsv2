package project.tdlogistics.users.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Response;
import project.tdlogistics.users.entities.Role;
import project.tdlogistics.users.services.CustomerService;
import project.tdlogistics.users.services.FilesService;
import project.tdlogistics.users.services.ValidationService;
import project.tdlogistics.users.validations.customer.Search;
import project.tdlogistics.users.validations.customer.Update;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/v2/customers")
public class CustomerRestController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private FilesService filesService;

    @Autowired
    Validator validator;

    @Autowired
    private ValidationService validationService;

    @GetMapping("/check")
    public ResponseEntity<Response<Customer>> checkExistCustomer(
        @RequestHeader(name = "role") Role role,
        @RequestBody Customer criteria
    ) {
        if (Role.CUSTOMER.equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Customer>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            final Optional<Customer> customerOptional = customerService.checkExistCustomer(criteria);
            if (customerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Customer>(false, "Khách hàng đã tồn tại.", customerOptional.get()));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Customer>(false, "Khách hàng không tồn tại.", null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @GetMapping("/")
    public ResponseEntity<Response<Customer>> getAuthenticatedCustomerInfo (
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId
    ) {
        if (!Role.CUSTOMER.equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Customer>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }

        try {
            final Optional<Customer> customerOptional = customerService.getCustomerById(userId);
            if (customerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<Customer>(false, "Lấy thông tin thành công", customerOptional.get()));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Customer>(false, "Thông tin của quý khách không tồn tại. Vui lòng liên hệ kỹ thuật để xử lý", null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Customer>>> getCustomers(
        @RequestHeader(name = "role") Role role,
        @Validated(Search.class) @RequestBody Customer criteria
    ) throws MethodArgumentNotValidException {
        try {
            validationService.validateRequest(criteria, Search.class);

            if (Set.of(Role.CUSTOMER, Role.SHIPPER, Role.DRIVER).contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<Customer>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            final List<Customer> customers = customerService.getCustomers(criteria);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(false, "Lấy thông tin khách hàng thành công", customers));
        } catch (BindException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, e.getAllErrors().get(0).getDefaultMessage(), null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Customer>> updateCustomer(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "customerId", required = false) String customerId,
        @RequestBody @Valid Customer info
    ) {
        try {
            validationService.validateRequest(info, Update.class);

            if (!List.of(Role.ADMIN, Role.CUSTOMER).contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Customer>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            if (Role.CUSTOMER.equals(role) && !userId.equals(customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Customer>(true, "Người dùng không được phép truy cập tài nguyên này", null));
            }

            final Customer customer = customerService.updateCustomerInfo(customerId, info);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(true, "Khách hàng không tồn tại", null));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(false, "Cập nhật thông tin khách hàng thành công", customer));
        } catch (BindException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, e.getAllErrors().get(0).getDefaultMessage(), null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PutMapping("/avatar/update")
    public ResponseEntity<Response<Customer>> updateAvatar(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "customerId", required = false) String customerId,
        @RequestParam("avatar") MultipartFile file
    ) {
        if (!List.of(Role.ADMIN, Role.CUSTOMER).contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<Customer>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        }
        
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(true, "Ảnh không được để trống", null));
        }

        if (Role.CUSTOMER.equals(role)) {
            customerId = userId;
        }

        try {
            filesService.isValidImageFile(file);

            final Optional<Customer> optionalCustomer = customerService.getCustomerById(customerId);
            if (optionalCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Customer>(true, String.format("Khách hàng %s không tồn tại", customerId), null));
            }

            if (optionalCustomer.get().getAvatar() != null) {
                filesService.deleteFile("/tdlogistics/staff/image/avatar/" + optionalCustomer.get().getAvatar());
            }

            String filename = filesService.sendFile("/tdlogistics/customer/image/avatar", "default", file);
            Customer updatedCustomer = new Customer();
            updatedCustomer.setAvatar(filename);
            final Customer postUpdateCustomer = customerService.updateCustomerInfo(customerId, updatedCustomer);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<Customer>(false, "Cập nhật ảnh đại diện thành công", postUpdateCustomer));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi trong quá trình tải file. Vui lòng thử lại", null));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @GetMapping("/avatar/get")
    public ResponseEntity<byte[]> getAvatar(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "userId") String userId,
        @RequestParam(name = "customerId") String customerId
    ) {
        if (List.of(Role.SHIPPER, Role.DRIVER).contains(role) || Role.CUSTOMER.equals(role) && !userId.equals(customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            final Optional<Customer> optionalCustomer = customerService.getCustomerById(customerId);
            if (optionalCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(filesService.getFile("/tdlogistics/customer/image/avatar/" + optionalCustomer.get().getAvatar()), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null);
        }
    }
}
