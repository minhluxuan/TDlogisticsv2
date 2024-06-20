package project.tdlogistics.vehicle.controllers;

import project.tdlogistics.vehicle.entities.Agency;
import project.tdlogistics.vehicle.entities.Response;
import project.tdlogistics.vehicle.entities.Role;
import project.tdlogistics.vehicle.entities.Shipment;
import project.tdlogistics.vehicle.entities.Staff;
import project.tdlogistics.vehicle.entities.Task;
import project.tdlogistics.vehicle.entities.TransportPartner;
import project.tdlogistics.vehicle.entities.TransportPartnerStaff;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import project.tdlogistics.vehicle.entities.Vehicle;
import project.tdlogistics.vehicle.services.AgencyService;
import project.tdlogistics.vehicle.services.DriverService;
import project.tdlogistics.vehicle.services.ShipmentService;
import project.tdlogistics.vehicle.services.StaffService;
import project.tdlogistics.vehicle.services.TransportPartnerService;
import project.tdlogistics.vehicle.services.TransportPartnerStaffService;
import project.tdlogistics.vehicle.services.VehicleService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    private final VehicleService vehicleService;

    private final AgencyService agencyService;

    private final TransportPartnerService transportPartnerService;

    private final TransportPartnerStaffService transportPartnerStaffService;

    private final StaffService staffService;

    private final ShipmentService shipmentService;

    private final DriverService driverService;

    public VehicleRestController(VehicleService vehicleService, AgencyService agencyService,
            ShipmentService shipmentService, TransportPartnerService transportPartnerService,
            TransportPartnerStaffService transportPartnerStaffService, StaffService staffService,
            DriverService driverService) {
        this.vehicleService = vehicleService;
        this.agencyService = agencyService;
        this.transportPartnerService = transportPartnerService;
        this.transportPartnerStaffService = transportPartnerStaffService;
        this.staffService = staffService;
        this.driverService = driverService;
        this.shipmentService = shipmentService;
    }

    @PostMapping("/check")
    public ResponseEntity<Response<Vehicle>> checkExistVehicle(@RequestParam String vehicleId) {
        try {
            final Optional<Vehicle> optionalVehicle = vehicleService.checkExistVehicleById(vehicleId);
            if (optionalVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<Vehicle>(false, "Phương tiện không tồn tại", null));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<Vehicle>(false, "Phương tiện đã tồn tại", optionalVehicle.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<Vehicle>(true, "Đã có lỗi xảy ra, vui lòng thử lại", null));
        }
    }

    public ResponseEntity<?> createNewVehicle(@RequestHeader(value = "role") Role role,
            @RequestHeader(value = "agencyId") String agencyId,
            @RequestHeader(value = "transportPartnerId") String transportPartnerId,
            @RequestHeader(value = "staffId") String staffId,
            @RequestBody Vehicle requestBody
        ) {
        try {
            if (Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                // validate
                Agency tempAgency = new Agency();
                tempAgency.setAgencyId(agencyId);
                final Agency agency = agencyService.checkExistAgency(tempAgency);
                if (agency == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<TransportPartner>(true, String.format("Bưu cục/Đại lý %s không tồn tại trong hệ thống", requestBody.getAgencyId()), null));
                }
            } else if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                Agency tempAgency = new Agency();
                tempAgency.setAgencyId(agencyId);
                final Agency agency = agencyService.checkExistAgency(tempAgency);
                if (agency == null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<TransportPartner>(true, "Bưu cục/Đại lý của bạn không tồn tại trong hệ thống. Vui lòng kiểm tra lại", null));
                }
                requestBody.setAgencyId(agencyId);
            }

            Optional<Vehicle> resultCheckVehicle = vehicleService
                    .checkExistVehicleByLicense(requestBody.getLicensePlate());
            if (resultCheckVehicle.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<Vehicle>(true, String.format("Phương tiện có biển số %s đã tồn tại", requestBody.getLicensePlate()), null));
            }

            String[] agencyIdSubParts = agencyId.split("_");
            String modifiedLicensePlate = requestBody.getLicensePlate().replaceAll("[-\\s]", "");
            requestBody.setVehicleId(agencyIdSubParts[0] + "_" + agencyIdSubParts[1] + "_" + modifiedLicensePlate);

            if (requestBody.getTransportPartnerId() == null) {
                Staff tempStaff = new Staff();
                tempStaff.setAgencyId(requestBody.getAgencyId());
                tempStaff.setStaffId(requestBody.getStaffId());

                Optional<Staff> optionalStaff = staffService.checkExistStaff(tempStaff);
                if (optionalStaff.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Nhân viên %s thuộc bưu cục %s không tồn tại", requestBody.getStaffId(), requestBody.getAgencyId()), null));
                }
            }
            else {
                TransportPartnerStaff tempPartnerStaff = new TransportPartnerStaff();
                tempPartnerStaff.setStaffId(requestBody.getStaffId());
                tempPartnerStaff.setPartnerId(requestBody.getTransportPartnerId());
                TransportPartnerStaff partnerStaff = transportPartnerStaffService.checkExistTransportPartnerStaff(tempPartnerStaff);
                if (partnerStaff == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Staff>(true, String.format("Nhân viên %s thuộc đối tác vận tải %s không tồn tại", requestBody.getStaffId(), requestBody.getTransportPartnerId()), null));
                }
            }

            final Vehicle resultCreateNewVehicle = vehicleService.createNewVehicle(requestBody);
            if (resultCreateNewVehicle == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response<>(true,
                                "Tạo phương tiện vận tải có mã hiệu " + requestBody.getLicensePlate() + " thất bại.",
                                null));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(false,
                            "Tạo phương tiện vận tải có mã hiệu " + requestBody.getLicensePlate() + " thành công.",
                            resultCreateNewVehicle));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, e.getMessage(), null));

        }
    }

    @PostMapping("/search")
    public ResponseEntity<Response<List<Vehicle>>> getVehicles(
        @RequestHeader(name = "role") Role role,
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(name = "userId") String userId,
        @RequestHeader(name = "transportPartnerId", required = false) String transportPartnerId,
        @RequestBody Vehicle requestBody
    ) {
        try {
            /* pagination related content */

            if (Set.of(Role.DRIVER, Role.SHIPPER, Role.AGENCY_DRIVER, Role.AGENCY_SHIPPER, Role.PARTNER_DRIVER,
                    Role.PARTNER_SHIPPER).contains(role)) {

                /* missing validation here */

                Optional<Vehicle> resultCheckVehicle = vehicleService.checkExistVehicle(requestBody);
                if (resultCheckVehicle.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<List<Vehicle>>(true, "Không tìm thấy phương tiện vận tải", null));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<List<Vehicle>>(false, "Lấy danh sách phương tiện vận tải thành công",
                                List.of(resultCheckVehicle.get())));
            }

            if (Set.of(Role.TRANSPORT_PARTNER_REPRESENTOR).contains(role)) {

                /* missing validation here */

                if (transportPartnerId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new Response<>(true, "Thiếu thông tin đối tác vận tải", null));
                }

                requestBody.setTransportPartnerId(transportPartnerId);
                List<Vehicle> resultGetVehicles = vehicleService.getManyVehicles(requestBody);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<List<Vehicle>>(false, "Lấy danh sách phương tiện vận tải thành công", resultGetVehicles));
            }

            if (Set.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER,
                    Role.AGENCY_COMPLAINTS_SOLVER).contains(role)) {

                /* missing validation here */

                if (agencyId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new Response<List<Vehicle>>(true, "Thiếu thông tin đại lý", null));
                }

                requestBody.setAgencyId(agencyId);
                List<Vehicle> resultGetVehicle = vehicleService.getManyVehicles(requestBody);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<List<Vehicle>>(false, "Lấy danh sách phương tiện vận tải thành công", resultGetVehicle));
            }

            if (Set.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER, Role.COMPLAINTS_SOLVER)
                    .contains(role)) {

                /* missing validation here */

                List<Vehicle> resultGetVehicle = vehicleService.getManyVehicles(requestBody);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy danh sách phương tiện vận tải thành công", resultGetVehicle));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<List<Vehicle>>(true, "Người dùng không được phép truy cập tài nguyên này", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<List<Vehicle>>(true, e.getMessage(), null));
        }
    }

    @GetMapping("/shipments/get")
    public ResponseEntity<Response<List<String>>> getVehicleShipmentIds(@RequestParam String vehicleId) {
        try {
            Optional<Vehicle> resultGettingOneVehicle = vehicleService.checkExistVehicleById(vehicleId);
            if (resultGettingOneVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Phương tiện " + vehicleId + " không tồn tại.", null));
            }

            return ResponseEntity.ok().body(new Response<>(false, "Lấy thông tin thành công.",
                    resultGettingOneVehicle.get().getShipmentIds()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Vehicle>> updateVehicle(
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(value = "staffId", required = true) String staffId,
        @RequestHeader(value = "role", required = false) Role role,
        @RequestParam(value = "vehicle_id", required = true) String vehicleId,
        @RequestBody Vehicle requestBody
    ) {
        /* validation here */

        try {
            String[] updatorIdSubParts = staffId.split("_");
            String[] vehicleIdSubParts = vehicleId.split("_");

            if ((role == Role.AGENCY_MANAGER || role == Role.AGENCY_TELLER)
                    && (!updatorIdSubParts[0].equals(vehicleIdSubParts[0])
                            || !updatorIdSubParts[1].equals(vehicleIdSubParts[1]))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<Vehicle>(true,
                                "Phương tiện " + vehicleId + " không thuộc quyền kiểm soát của bưu cục " + agencyId,
                                null));
            }

            requestBody.setVehicleId(vehicleId);

            Vehicle resultUpdateVehicle = vehicleService.updateVehicle(requestBody);
            if (resultUpdateVehicle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Phương tiện " + vehicleId + " không tồn tại.", null));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<Vehicle>(false, "Cập nhật phương tiện " + vehicleId + " thành công.",
                            resultUpdateVehicle));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<Vehicle>(true, e.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<Vehicle>(true, "Đã xảy ra lỗi. Vui lòng thử lại", null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteVehicle(
        @RequestHeader(name = "agencyId") String agencyId,
        @RequestHeader(value = "staffId") String staffId,
        @RequestHeader(value = "role") Role role,
        @RequestParam(value = "vehicleId") String vehicleId
    ) {
        try {

            String[] deletorIdSubParts = staffId.split("_");
            String[] vehicleIdSubParts = vehicleId.split("_");

            if ((role == Role.AGENCY_MANAGER || role == Role.AGENCY_TELLER)
                    && (!deletorIdSubParts[0].equals(vehicleIdSubParts[0])
                            || !deletorIdSubParts[1].equals(vehicleIdSubParts[1]))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true,
                                "Phương tiện " + vehicleId + " không thuộc quyền quản lý của bưu cục " + agencyId,
                                null));
            }

            vehicleService.deleteVehicle(vehicleId);

            return ResponseEntity.ok()
                    .body(new Response<>(false, "Xóa phương tiện với id " + vehicleId + " thành công.", null));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    @PostMapping("/shipments/add")
    public ResponseEntity<?> addShipmentToVehicle(@RequestParam String vehicleId,
            @RequestBody List<String> shipmentIds) {
        try {

            Optional<Vehicle> resultGettingOneVehicle = vehicleService.checkExistVehicleById(vehicleId);
            if (resultGettingOneVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Phương tiện " + vehicleId + " không tồn tại.", null));
            }

            vehicleService.addShipmentToVehicle(vehicleId, shipmentIds);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<>(false, "Thêm lô hàng vào phương tiện thành công", shipmentIds));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    @PostMapping("/shipments/delete")
    public ResponseEntity<?> deleteShipmentFromVehicle(@RequestParam String vehicleId,
            @RequestBody List<String> shipmentIds) {
        try {

            Optional<Vehicle> resultGettingOneVehicle = vehicleService.checkExistVehicleById(vehicleId);
            if (resultGettingOneVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Phương tiện với id " + vehicleId + " không tồn tại.", null));
            }

            vehicleService.deleteShipmentFromVehicle(vehicleId, shipmentIds);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(false, "Xóa shipment ids thành công", shipmentIds));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    /* chưa test */
    public ResponseEntity<?> undertakeShipments(
            @RequestHeader(value = "shipment_id", required = true) String shipmentId,
            @RequestHeader(value = "staff_id", required = true) String staffId) {
        /* validation */

        try {

            /* missing implementation */

            Optional<Task> resultGetOneTaskOpt = driverService.getOneTask(shipmentId, staffId);
            if (resultGetOneTaskOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Staff with id " + staffId
                                + " is not allowed to undertake shipment with id " + shipmentId + ".", null));
            }

            Optional<Shipment> resultGettingOneShipmentOpt = shipmentService.getOneShipment(shipmentId);
            if (resultGettingOneShipmentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Shipment " + shipmentId + " does not exist to undertake.", null));
            }

            Shipment shipment = resultGettingOneShipmentOpt.get();

            if (shipment.getStatus() < 3) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response<>(true, "Shipment with id " + shipmentId
                                + " is not feasible to undertake at this time. Previous status required: 'Assigned'.",
                                null));
            }
            if (shipment.getStatus() >= 4) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response<>(true, "Shipment with id " + shipmentId
                                + " has been previously undertaken.", null));
            }

            /*
             * await shipmentsService.updateShipment({ status: 4, parent:
             * resultGettingOneTask[0].vehicle_id }, { shipment_id: req.query.shipment_id
             * });
             * const message = `${formattedTime}: Lô hàng được tiếp nhận bởi nhân viên có mã
             * ${req.user.staff_id} thuộc đối tác vận tải có mã ${req.user.partner_id} và
             * đang được vận chuyển trên phương tiện có mã
             * ${resultGettingOneTask[0].vehicle_id}.`;
             * await shipmentsService.updateJourney(req.query.shipment_id, formattedTime,
             * message);
             * await shipmentsService.updateJourney(req.query.shipment_id, formattedTime,
             * message,
             * utils.getPostalCodeFromAgencyID(resultGettingOneShipment[0].agency_id));
             * if (resultGettingOneShipment[0].agency_id_dest) {
             * await shipmentsService.updateJourney(req.query.shipment_id, formattedTime,
             * message,
             * utils.getPostalCodeFromAgencyID(resultGettingOneShipment[0].agency_id_dest));
             * }
             */

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<>(false, "Successfully undertook shipments.", null));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }

    }

}