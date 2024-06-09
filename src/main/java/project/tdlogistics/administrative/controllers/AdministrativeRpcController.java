package project.tdlogistics.administrative.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import project.tdlogistics.administrative.entities.District;
import project.tdlogistics.administrative.entities.Request;
import project.tdlogistics.administrative.entities.Response;
import project.tdlogistics.administrative.entities.UnitRequest;
import project.tdlogistics.administrative.entities.Ward;
import project.tdlogistics.administrative.services.AdministrativeService;

@Controller
public class AdministrativeRpcController {
    @Autowired
    private AdministrativeService administrativeService;

    @RabbitListener(queues = "rpc.administrative")
    private String handleRpcRequest(String jsonRequest) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            Request<UnitRequest> request = objectMapper.readValue(jsonRequest, new TypeReference<Request<UnitRequest>>() {});
            switch (request.getOperation()) {
                case "checkExistDistrict":
                    return checkExistDistrict(request.getPayload());
                case "checkExistWard":
                    return checkExistWard(request.getPayload());
                case "findWards":
                    return findWards(request.getPayload());
                case "updateAdministrativeUnit":
                    return updateAdministrativeUnit(request.getParams(), request.getPayload());
                default:
                    return (new ObjectMapper().registerModule(new JavaTimeModule())).writeValueAsString(new Response<>(400, true, "Yêu cầu không hợp lệ", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistDistrict(UnitRequest criteria) throws JsonProcessingException {
        try {
            if (criteria.getDistrict() == null || criteria.getProvince() == null) {
                return (new ObjectMapper()).writeValueAsString(new Response<District>(400, false, "Thông tin không hợp lệ", null));
            }

            final District district = new District(criteria.getProvince(), criteria.getDistrict());
            final Optional<District> optionalDistrict = administrativeService.checkExistDistrict(district);
            if (optionalDistrict.isEmpty()) {
                return (new ObjectMapper()).writeValueAsString(new Response<District>(200, false, String.format("%s, %s không tồn tại", criteria.getDistrict(), criteria.getProvince()), null));
            }

            return (new ObjectMapper()).writeValueAsString(new Response<District>(200, false, String.format("%s, %s đã tồn tại", criteria.getDistrict(), criteria.getProvince()), optionalDistrict.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String checkExistWard(UnitRequest criteria) throws JsonProcessingException {
        try {
            if (criteria.getWard() == null || criteria.getDistrict() == null || criteria.getProvince() == null) {
                return (new ObjectMapper()).writeValueAsString(new Response<District>(400, false, "Thông tin không hợp lệ", null));
            }

            final Ward ward = new Ward(criteria.getProvince(), criteria.getDistrict(), criteria.getWard());
            final Optional<Ward> optionalWard = administrativeService.checkExistWard(ward);
            if (optionalWard.isEmpty()) {
                return (new ObjectMapper()).writeValueAsString(new Response<District>(200, false, String.format("%s, %s, %s không tồn tại", criteria.getWard(), criteria.getDistrict(), criteria.getProvince()), null));
            }

            return (new ObjectMapper()).writeValueAsString(new Response<Ward>(200, false, String.format("%s, %s, %s đã tồn tại", criteria.getWard(), criteria.getDistrict(), criteria.getProvince()), optionalWard.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    private String findWards(UnitRequest criteria) throws JsonProcessingException {
        try {
            final Ward ward = new Ward(criteria.getWard(), criteria.getDistrict(), criteria.getProvince(), criteria.getPostalCode(), criteria.getAgencyId(), criteria.getShipper());
            List<Ward> wards = administrativeService.findWards(ward);
            return (new ObjectMapper()).writeValueAsString(new Response<List<Ward>>(200, false, "Lấy thông tin thành công", wards));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null)); 
        }
    }

    private String updateAdministrativeUnit(Map<String, Object> map, UnitRequest payload) throws JsonProcessingException {
        try {
            if (map.get("province") == null || map.get("district") == null || map.get("ward") == null) {
                return (new ObjectMapper()).writeValueAsString(new Response<District>(400, true, "Thông tin không hợp lệ", null));
            }
            final Ward wardCriteria = new Ward(map.get("province").toString(), map.get("district").toString(), map.get("ward").toString());
            System.out.println("ward criteria:");
            System.out.println(wardCriteria);
            final Ward wardPayload = new Ward(payload.getWard(), payload.getDistrict(), payload.getProvince(), payload.getPostalCode(), payload.getAgencyId(), payload.getShipper());
            System.out.println("ward payload:");
            System.out.println(wardPayload);
            final Ward updatedWard = administrativeService.updateOneAdministrativeUnit(wardCriteria, wardPayload, (Boolean) (map.get("allowShipperNull") == null ? false : map.get("allowShipperNull")));
            System.out.println("ward updated:");
            System.out.println(updatedWard);
            if (updatedWard == null) {
                return (new ObjectMapper()).writeValueAsString(new Response<Ward>(404, true, String.format("%s, %s, %s không tồn tại", payload.getWard(), payload.getDistrict(), payload.getProvince()), null));
            }
            
            return (new ObjectMapper()).writeValueAsString(new Response<Ward>(201, false, "Cập nhật thành công", updatedWard));
        } catch (Exception e) {
            e.printStackTrace();
            return (new ObjectMapper()).writeValueAsString(new Response<>(500, true, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

}
