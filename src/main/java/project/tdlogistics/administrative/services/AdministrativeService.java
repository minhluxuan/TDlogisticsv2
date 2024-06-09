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
import project.tdlogistics.administrative.configurations.MyBeanUtils;

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

    public Optional<District> checkExistDistrict(District criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<District> example = Example.of(criteria, matcher);
        return districtRepository.findOne(example);
    }

    public Optional<Ward> checkExistWard(Ward criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Ward> example = Example.of(criteria, matcher);
        return wardRepository.findOne(example);
    }

    public List<Ward> findWards(Ward criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Ward> example = Example.of(criteria, matcher);
        return wardRepository.findAll(example);
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

    public Ward updateOneAdministrativeUnit(Ward condition, Ward info, Boolean allowShipperNull) {
        System.out.println(allowShipperNull);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Ward> example = Example.of(condition, matcher);
        Optional<Ward> wardOptional = wardRepository.findOne(example);
        
        if (wardOptional.isPresent()) {
            MyBeanUtils.copyNonNullProperties(info, wardOptional.get());
            if (allowShipperNull) {
                wardOptional.get().setShipper(info.getShipper());
            }

            System.out.println("Get Shipper: ");
            System.out.println(wardOptional.get().getShipper());
            return wardRepository.save(wardOptional.get());
        } else {
            return null;
        }
    }
}
