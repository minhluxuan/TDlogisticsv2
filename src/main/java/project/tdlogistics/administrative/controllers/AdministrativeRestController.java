package project.tdlogistics.administrative.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.tdlogistics.administrative.services.AdministrativeService;
import project.tdlogistics.administrative.entities.Response;
import project.tdlogistics.administrative.entities.UnitRequest;

import java.util.List;

@RestController
public class AdministrativeRestController {

    private final AdministrativeService administrativeService;

    public AdministrativeRestController(AdministrativeService administrativeService) {
        this.administrativeService = administrativeService;
    }

    @GetMapping("/v2/administrative/search")
    public ResponseEntity<Response<List<String>>> getUnits(@RequestBody UnitRequest request) {
        try {
            Response<List<String>> response;
            if (request.getProvince() == null && request.getDistrict() == null && request.getWard() == null) {
                response = administrativeService.getUnits(1, null, null);
            } else if (request.getProvince() != null && request.getDistrict() == null && request.getWard() == null) {
                response = administrativeService.getUnits(2, request.getProvince(), null);
            } else if (request.getProvince() != null && request.getDistrict() != null && request.getWard() == null) {
                response = administrativeService.getUnits(3, request.getProvince(), request.getDistrict());
            } else {
                return ResponseEntity.badRequest().body(new Response<>(false, "Thông tin không hợp lệ", null));
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage(), null));
        }
    }
}