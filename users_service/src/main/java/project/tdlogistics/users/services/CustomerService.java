package project.tdlogistics.users.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import project.tdlogistics.users.configurations.MyBeanUtils;
import project.tdlogistics.users.entities.Account;
import project.tdlogistics.users.entities.Customer;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.repositories.AccountRepository;
import project.tdlogistics.users.repositories.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    // public Optional<Customer> checkExistCustomer(Customer criteria) {
    //     ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
    //     Example<Customer> example = Example.of(criteria, matcher);
    //     return customerRepository.findFirst(example);
    // }

    public Optional<Customer> checkExistCustomer(Customer criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Customer> example = Example.of(criteria, matcher);
        List<Customer> customers = customerRepository.findAll(example);
        if (customers.size() == 1) {
            final Customer customer = customers.get(0);
            // customer.getAccount().setPassword(null);
            return Optional.of(customer);
        } else {
            return Optional.empty();
        }
    }

    public Customer createNewCustomer(Customer info) {
        customerRepository.save(info);
        // info.getAccount().setPassword(null);
        return info;
    }

    public List<Customer> getCustomers(Customer criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Customer> example = Example.of(criteria, matcher);
        final List<Customer> customers = customerRepository.findAll(example);
        customers.forEach((customer) -> {
            // customer.getAccount().setPassword(null);
        });

        return customers;
    }

    public Customer updateCustomerInfo(String id, Customer info) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isEmpty()) {
            return null; // Or throw an exception if customer not found
        }

        Customer existingCustomer= customerOptional.get();

        // Copy non-null properties from info to existingStaff
        MyBeanUtils.copyNonNullProperties(info, existingCustomer);

        // Save the updated customer entity
        customerRepository.save(existingCustomer);

        // Return the updated customer entity
        return customerRepository.findById(id).get();
    }
}
