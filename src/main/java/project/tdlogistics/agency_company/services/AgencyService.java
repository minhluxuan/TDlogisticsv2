package project.tdlogistics.agency_company.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.agency_company.configurations.MyBeanUtils;
import project.tdlogistics.agency_company.entities.Account;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.AgencyCompany;
import project.tdlogistics.agency_company.entities.District;
import project.tdlogistics.agency_company.entities.Ward;
import project.tdlogistics.agency_company.entities.Province;
import project.tdlogistics.agency_company.entities.Request;
import project.tdlogistics.agency_company.entities.Response;
import project.tdlogistics.agency_company.entities.Staff;
import project.tdlogistics.agency_company.entities.UnitRequest;
import project.tdlogistics.agency_company.repositories.*;

import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AgencyService {
    final String regexPersonnel = "^(TD|BC|DL)_\\d{5}_\\d{12}$";
    private final AgencyRepository agencyRepository;
    private final AgencyCompanyRepository agencyCompanyRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AgencyService(AgencyRepository agencyRepository, AgencyCompanyRepository agencyCompanyRepository,  ObjectMapper objectMapper) {
        this.agencyRepository = agencyRepository;
        this.agencyCompanyRepository = agencyCompanyRepository;
        this.objectMapper = objectMapper;
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    private String exchange = "rpc-direct-exchange";

    public Optional<Agency> checkExistAgency(String agencyId) {
        return agencyRepository.findById(agencyId);
    }

    public Optional<Agency> checkExistAgency(Agency criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Agency> example = Example.of(criteria, matcher);
        List<Agency> result = agencyRepository.findAll(example);
        if (result.size() > 0) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Agency> getOneAgency(Agency criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Agency> example = Example.of(criteria, matcher);
        return agencyRepository.findOne(example);
    }

    public List<Agency> getManyAgencies(Agency criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Agency> example = Example.of(criteria, matcher);
        return agencyRepository.findAll(example);
    }

    public void checkPostalCode(String province, String district, String postalCode) throws JsonProcessingException {
        Agency agencyTemp = new Agency();
        agencyTemp.setPostalCode(postalCode);

        if (checkExistAgency(agencyTemp).isPresent()) {
            throw new IllegalArgumentException(String.format("Bưu cục có mã bưu chính %s đã tồn tại", postalCode));
        }

        if (agencyRepository.checkExistTableWithPostalCode(postalCode)) {
            throw new IllegalStateException(String.format("Bảng với tiền tố %s đã tồn tại. Vui lòng liên hệ kỹ thuật để hỗ trợ.", postalCode));
        }

        final String jsonRequestCheckingExistDistrict = objectMapper.writeValueAsString(new Request<District>("checkExistDistrict", null, new District(province, district)));
        final String jsonResponseCheckingExistDistrict = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestCheckingExistDistrict);

        if (jsonResponseCheckingExistDistrict == null) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<District> responseCheckingExistDistrict = objectMapper.readValue(jsonResponseCheckingExistDistrict, new TypeReference<Response<District>>() {});
        if (responseCheckingExistDistrict.getError()) {
            throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        if (responseCheckingExistDistrict.getData() == null) {
            throw new IllegalArgumentException(String.format("%s, %s không tồn tại.", district, province));
        }

        String postalCodeOfDistrictFromDatabase = responseCheckingExistDistrict.getData().getPostalCode();
        if (postalCodeOfDistrictFromDatabase == null || !postalCodeOfDistrictFromDatabase.substring(0, 4).equals(postalCode.substring(0, 4))) {
            throw new IllegalArgumentException(
                    String.format("%s, %s không khớp với mã bưu chính %s.", district, province, postalCode));
        }
    }

    public void checkWardsOcupation(String province, String district, List<String> wards) throws JsonProcessingException {
        for (final String ward : wards) {
            final String jsonRequestCheckingExistWard = objectMapper.writeValueAsString(new Request<Ward>("checkExistWard", null, new Ward(province, district, ward)));
            final String jsonResponseCheckingExistWard = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestCheckingExistWard);
            if (jsonResponseCheckingExistWard == null) {
                throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
            }

            final Response<Ward> responseCheckingExistWard = objectMapper.readValue(jsonResponseCheckingExistWard, new TypeReference<Response<Ward>>() {});
            if (responseCheckingExistWard.getError()) {
                throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
            }

            if (responseCheckingExistWard.getData() == null) {
                throw new IllegalArgumentException(String.format("%s, %s, %s không tồn tại.", ward, district, province));
            }
            System.out.println(responseCheckingExistWard.getData());
            if (responseCheckingExistWard.getData().getAgencyId() != null && Pattern.matches(regexPersonnel, responseCheckingExistWard.getData().getAgencyId())) {
                throw new IllegalStateException(String.format("%s, %s, %s đã được quản lý bởi đại lý/bưu cục %s", ward, district, province, responseCheckingExistWard.getData().getAgencyId()));
            }
        }
    }

    public void locateAgencyInArea(String province, String district, List<String> wards, String postalCode, String agencyId) throws JsonProcessingException {
        for (final String ward : wards) {
            HashMap<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("province", province);
            criteria.put("district", district);
            criteria.put("ward", ward);

            final String jsonRequestUpdatingWard = objectMapper.writeValueAsString(new Request<UnitRequest>("updateAdministrativeUnit", criteria, new UnitRequest(province, district, ward, postalCode, agencyId)));
            final String jsonResponseUpdatingWard = (String) amqpTemplate.convertSendAndReceive(exchange, "rpc.administrative", jsonRequestUpdatingWard);
            if (jsonResponseUpdatingWard == null) {
                throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
            }

            final Response<Ward> responseUpdatingWard = objectMapper.readValue(jsonResponseUpdatingWard, new TypeReference<Response<Ward>>() {});
            if (responseUpdatingWard.getError()) {
                throw new InternalError("Đã xảy ra lỗi. Vui lòng thử lại");
            }
        }
    }

    public String createTablesForAgency(String postalCode) throws Exception {
        return agencyRepository.createTablesForAgency(postalCode);
    }

    public void createNewAgency(Agency agency) throws Exception {
        agencyRepository.save(agency);
    }

    public Agency updateAgency(String agencyId, Agency agency) throws CloneNotSupportedException {
        Optional<Agency> optionalAgency = checkExistAgency(agencyId);
        if (!optionalAgency.isPresent()) {
            return null;  // Or throw an exception as per your requirement
        }
    
        Agency existingAgency = optionalAgency.get();

        if (agency.getAgencyCompany() == null) {
            MyBeanUtils.copyNonNullProperties(agency, existingAgency);
            return agencyRepository.save(existingAgency);
        }

        MyBeanUtils.copyNonNullProperties(agency.getAgencyCompany(), existingAgency.getAgencyCompany());
        existingAgency.getAgencyCompany().setAgency(existingAgency);
        agency.setAgencyCompany(null);
        MyBeanUtils.copyNonNullProperties(agency, existingAgency);
        return agencyRepository.save(existingAgency);
    }

    public void deleteAgency(String agencyId) {
        agencyRepository.deleteById(agencyId);
    }

    public void dropTablesForAgency(String postalCode) throws Exception {
        agencyRepository.dropTablesForAgency(postalCode);
    }

    // public Optional<Agency> getOneAgency(Agency agency) {
    //     return checkExistAgency(agency);
    // }

    // public List<Agency> getManyAgencies(Agency template_agency, Integer pages, Integer rows) {
    //     try {

    //         ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
    //         Example<Agency> example = Example.of(template_agency, matcher);

    //         int limit = (rows != null && rows >= 1) ? rows : 3;
    //         int offset = (pages != null) ? pages * limit : 0;
    //         // havent implemented the pagination yet
    //         Pageable pageable = PageRequest.of(offset, limit);

    //         return agencyRepository.findAll(example);
    //     } catch (Exception e) {
    //         System.err.println("Error: " + e.getMessage());
    //         return null;

    //     }
    // }

    // //

    // /

    // // public Response updatePassword
    // public List<String> getManagedWards(String agencyId) {
    //     Optional<Agency> agency = this.checkExistAgency(agencyId);
    //     if (agency.isPresent()) {
    //         return agency.get().getManaged_wards();
    //     } else {
    //         throw new IllegalArgumentException("Agency không tồn tại");
    //     }
    // }
}
