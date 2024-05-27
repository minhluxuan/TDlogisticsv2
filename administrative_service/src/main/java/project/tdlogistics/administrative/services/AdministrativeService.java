package project.tdlogistics.administrative.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import project.tdlogistics.administrative.entities.District;
import project.tdlogistics.administrative.entities.Province;
import project.tdlogistics.administrative.entities.Ward;
import project.tdlogistics.administrative.entities.Response;
import project.tdlogistics.administrative.repositories.DistrictRepository;
import project.tdlogistics.administrative.repositories.ProvinceRepository;
import project.tdlogistics.administrative.repositories.WardRepository;


@Service
public class AdministrativeService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @Autowired
    public AdministrativeService(ProvinceRepository provinceRepository, DistrictRepository districtRepository, WardRepository wardRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    public Optional<Province> checkExistProvince(Province criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Province> example = Example.of(criteria, matcher);
        return provinceRepository.findOne(example);
    }

    public String getOneDistributionCenter(String province) {
        return provinceRepository.findManagedByByProvince(province);
    }

    public Response<List<String>> getUnits(int level, String province, String district) {
        if (level == 1) {
            List<String> provinces = new ArrayList<>();
            for (Province p : provinceRepository.findAll()) {
                provinces.add(p.getProvince());
            }
            return new Response<>(false, "Lấy thông tin đơn vị hành chính thành công", provinces);
        } else if (level == 2) {
            if (province == null) {
                return new Response<>(true, "Lấy thông tin đơn vị hành chính thất bại", null);
            }
            List<String> districts = new ArrayList<>();
            for (District d : districtRepository.findByProvince(province)) {
                districts.add(d.getDistrict());
            }
            return new Response<>(true, "Lấy thông tin đơn vị hành chính thành công", districts);
        } else if (level == 3) {
            if (province == null || district == null) {
                return new Response<>(false, "Lấy thông tin đơn vị hành chính thất bại", null);
            }
            List<String> wards = new ArrayList<>();
            for (Ward w : wardRepository.findByProvinceAndDistrict(province, district)) {
                wards.add(w.getWard());
            }
            return new Response<>(false, "Lấy thông tin đơn vị hành chính thành công", wards);
        } else {
            return new Response<>(true, "Thông tin không hợp lệ", null);
        }
    }

    public Optional<Ward> getOneAdministrativeUnit(Ward conditions) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Ward> example = Example.of(conditions, matcher);
        return wardRepository.findOne(example);
    }

    public List<Ward> getAdministrativeUnit(Ward conditions) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Ward> example = Example.of(conditions, matcher);
        return wardRepository.findAll(example);
    }

    public Ward updateOneAdministrativeUnit(Ward condition, Ward info) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Ward> example = Example.of(condition, matcher);
        Optional<Ward> wardOptional = wardRepository.findOne(example);
        if (wardOptional.isPresent()) {
            Ward ward = wardOptional.get();
            if (info.getProvince() != null) {
                ward.setProvince(info.getProvince());
            }
            if (info.getDistrict() != null) {
                ward.setDistrict(info.getDistrict());
            }
            if (info.getWard() != null) {
                ward.setWard(info.getWard());
            }
            wardRepository.save(ward);
            return ward;
        } else {
            return null;
        }
    }

    // public Optional<Customer> checkExistCustomer(Customer criteria) {
    //     ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
    //     Example<Customer> example = Example.of(criteria, matcher);
    //     return customerRepository.findFirst(example);
    // }

//    public Optional<Customer> checkExistCustomer(Customer criteria) {
//        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
//        Example<Customer> example = Example.of(criteria, matcher);
//        List<Customer> customers = customerRepository.findAll(example);
//        if (customers.size() == 1) {
//            final Customer customer = customers.get(0);
//            customer.getAccount().setPassword(null);
//            return Optional.of(customer);
//        } else {
//            return Optional.empty();
//        }
//    }
//
//    public Customer createNewCustomer(Customer info) {
//        customerRepository.save(info);
//        info.getAccount().setPassword(null);
//        return info;
//    }
//
//    public List<Customer> getCustomers(Customer criteria) {
//        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
//        Example<Customer> example = Example.of(criteria, matcher);
//        final List<Customer> customers = customerRepository.findAll(example);
//        customers.forEach((customer) -> {
//            customer.getAccount().setPassword(null);
//        });
//
//        return customers;
//    }
//
//    public Customer updateCustomerInfo(String id, Customer info) {
//        Optional<Customer> customerOptional = customerRepository.findById(id);
//        if (customerOptional.isEmpty()) {
//            return null;
//        }
//
//        Customer customer = customerOptional.get();
//        if (info.getPhoneNumber() != null) {
//            customer.setPhoneNumber(info.getPhoneNumber());
//        }
//
//        if (info.getEmail() != null) {
//            customer.setEmail(info.getEmail());
//        }
//
//        if (info.getFullname() != null) {
//            customer.setFullname(info.getFullname());
//        }
//
//        if (info.getProvince() != null) {
//            customer.setProvince(info.getProvince());
//        }
//
//        if (info.getDistrict() != null) {
//            customer.setDistrict(info.getDistrict());
//        }
//
//        if (info.getWard() != null) {
//            customer.setWard(info.getWard());
//        }
//
//        if (info.getDetailAddress() != null) {
//            customer.setDetailAddress(info.getDetailAddress());
//        }
//        customer.getAccount().setPassword(null);
//        customerRepository.save(customer);
//
//        return customer;
//    }
}
