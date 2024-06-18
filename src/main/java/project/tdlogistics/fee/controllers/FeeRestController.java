package project.tdlogistics.fee.controllers;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import project.tdlogistics.fee.entities.Response;
import project.tdlogistics.fee.services.FeeService;


@RestController
@RequestMapping("/v2/fee")
public class FeeRestController {

   @PostMapping("/calculate")
    public ResponseEntity<Response<Double>> postMethodName(@RequestBody Map<String, Object> criteria,
                                                           @RequestHeader(value = "role") String role) throws Exception {
        try {
            String provinceSource = ((String) criteria.get("provinceSource")).replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();
            String provinceDest = ((String) criteria.get("provinceDest")).replaceAll("^(Thành phố\\s*|Tỉnh\\s*)", "").trim();

            double resultCalculatingFee = FeeService.calculateFee((String) criteria.get("serviceCode"), provinceSource, provinceDest, (double) criteria.get("mass") , 0.15, false);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(
                false,
                "Tính phí thành công",
                resultCalculatingFee
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(
                true,
                e.getMessage(),
                null
            ));
        }
    }
   

}
