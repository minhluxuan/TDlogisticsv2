package project.tdlogistics.vehicle.controllers;

import project.tdlogistics.vehicle.entities.Agency;
import project.tdlogistics.vehicle.entities.Response;
import project.tdlogistics.vehicle.entities.Role;
import project.tdlogistics.vehicle.entities.Shipment;
import project.tdlogistics.vehicle.entities.Staff;
import project.tdlogistics.vehicle.entities.Task;
import project.tdlogistics.vehicle.entities.TransportPartner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.tdlogistics.vehicle.entities.Vehicle;
import project.tdlogistics.vehicle.services.AgencyService;
import project.tdlogistics.vehicle.services.DriverService;
import project.tdlogistics.vehicle.services.ShipmentService;
import project.tdlogistics.vehicle.services.StaffService;
import project.tdlogistics.vehicle.services.TransportPartnerService;
import project.tdlogistics.vehicle.services.VehicleService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    private final VehicleService vehicleService;

    private final AgencyService agencyService;

    private final TransportPartnerService transportPartnerService;

    private final StaffService staffService;

    private final ShipmentService shipmentService;

    private final DriverService driverService;

    @Autowired
    public VehicleRestController(VehicleService vehicleService, AgencyService agencyService,
            ShipmentService shipmentService, TransportPartnerService transportPartnerService, StaffService staffService,
            DriverService driverService) {
        this.vehicleService = vehicleService;
        this.agencyService = agencyService;
        this.transportPartnerService = transportPartnerService;
        this.staffService = staffService;
        this.driverService = driverService;
        this.shipmentService = shipmentService;
    }

    @GetMapping("/test")
    public ResponseEntity<Response<String>> test() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response<String>(false, "Test successful", "Test successful"));
    }

    @PostMapping("/check")
    public ResponseEntity<Response<Vehicle>> checkExistVehicle(@RequestParam String vehicleId) {
        try {
            final Optional<Vehicle> optionalVehicle = vehicleService.checkExitVehicleById(vehicleId);
            if (optionalVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<Vehicle>(false, "Vehicle does not exist", null));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<Vehicle>(false, "Vehicle already exists", optionalVehicle.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<Vehicle>(true, "An error occurred. Please try again", null));
        }
    }

    public ResponseEntity<?> createNewVehicle(@RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "agencyId", required = false) String agencyId,
            @RequestHeader(value = "transportPartnerId", required = false) String transportPartnerId,
            @RequestHeader(value = "staffId", required = false) String staffId, @RequestBody Vehicle requestBody) {
        try {
            if (List.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER).contains(role)) {
                // validate
                // checkExitsAgency

            } else if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER).contains(role)) {
                // validate create vehicle by agency(requestbody)
                requestBody.setAgencyId(agencyId);
            }

            // const resultCheckingExistAgencyAndStaff = await
            // staffsService.checkExistStaffIntersect({ agency_id: req.body.agency_id,
            // staff_id: req.body.staff_id });

            Optional<Vehicle> resultCheckVehicle = vehicleService
                    .checkExitVehicleByLicense(requestBody.getLicensePlate());
            if (resultCheckVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<Vehicle>(true, "Vehicle dont exit", null));
            }
            String[] agencyIdSubParts = agencyId.split("_");
            String modifiedLicensePlate = requestBody.getLicensePlate().replaceAll("[-\\s]", "");
            requestBody.setVehicleId(agencyIdSubParts[0] + "_" + agencyIdSubParts[1] + "_" + modifiedLicensePlate);
            if (requestBody.getTransportPartnerId() != null) {
                // PartnerStaffIntersection resultCheckingExistTransportPartnerAndStaff =
                // partnerStaffService
                // .checkExistPartnerStaffIntersect(requestBody.getTransportPartnerId(),
                // requestBody.getStaffId());

                // final String resultCheckingExistTransportPartnerAndStaff = "";
                // if (resultCheckingExistTransportPartnerAndStaff == null) {
                // return ResponseEntity.status(HttpStatus.NOT_FOUND)
                // .body(new Response<>(true,
                // "Nhân viên có mã " + requestBody.getStaffId()
                // + " không tồn tại hoặc không thuộc đối tác vận tải có mã "
                // + requestBody.getTransportPartnerId(),
                // null));
                // }

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

    public ResponseEntity<?> getVehicles(@RequestHeader(value = "role", required = false) Role role,
            @RequestHeader(value = "agencyId", required = false) String agencyId,
            @RequestHeader(value = "transportPartnerId", required = false) String transportPartnerId,
            @RequestBody Vehicle requestBody) {
        try {

            /* pagination related content */

            if (List.of(Role.DRIVER, Role.SHIPPER, Role.AGENCY_DRIVER, Role.AGENCY_SHIPPER, Role.PARTNER_DRIVER,
                    Role.PARTNER_SHIPPER).contains(role)) {

                /* missing validation here */

                Optional<Vehicle> resultCheckVehicle = vehicleService.checkExitVehicle(requestBody);
                if (resultCheckVehicle.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response<>(true, "Không tìm thấy phương tiện vận tải", null));
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy danh sách phương tiện vận tải thành công",
                                resultCheckVehicle.get()));

            }
            if (List.of(Role.TRANSPORT_PARTNER_REPRESENTOR).contains(role)) {

                /* missing validation here */

                if (transportPartnerId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new Response<>(true, "Thiếu thông tin đối tác vận tải", null));
                }

                requestBody.setTransportPartnerId(transportPartnerId);
                List<Vehicle> resultGetVehicles = vehicleService.getManyVehicles(requestBody);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy danh sách phương tiện vận tải thành công", resultGetVehicles));

            }
            if (List.of(Role.AGENCY_MANAGER, Role.AGENCY_HUMAN_RESOURCE_MANAGER, Role.AGENCY_TELLER,
                    Role.AGENCY_COMPLAINTS_SOLVER).contains(role)) {

                /* missing validation here */

                if (agencyId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new Response<>(true, "Thiếu thông tin đại lý", null));
                }

                requestBody.setAgencyId(agencyId);
                List<Vehicle> resultGetVehicle = vehicleService.getManyVehicles(requestBody);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy danh sách phương tiện vận tải thành công", resultGetVehicle));

            }
            if (List.of(Role.ADMIN, Role.MANAGER, Role.HUMAN_RESOURCE_MANAGER, Role.TELLER, Role.COMPLAINTS_SOLVER)
                    .contains(role)) {

                /* missing validation here */

                List<Vehicle> resultGetVehicle = vehicleService.getManyVehicles(requestBody);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>(false, "Lấy danh sách phương tiện vận tải thành công", resultGetVehicle));

            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(true, "Lỗi hệ thống. Vui lòng thử lại", null));

    }

    @GetMapping("/getVehicleShipmentIds")
    public ResponseEntity<?> getVehicleShipmentIds(@RequestParam String vehicleId) {
        try {

            Optional<Vehicle> resultGettingOneVehicle = vehicleService.checkExitVehicleById(vehicleId);
            if (resultGettingOneVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Vehicle with id " + vehicleId + " does not exist.", null));
            }

            return ResponseEntity.ok().body(new Response<>(false, "Successfully fetched data.",
                    resultGettingOneVehicle.get().getShipmentIds()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    public ResponseEntity<?> updateVehicle(@RequestHeader(value = "staffId", required = true) String staffId,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestParam(value = "vehicle_id", required = true) String vehicleId, @RequestBody Vehicle requestBody) {
        /* validation here */

        try {

            String[] updatorIdSubParts = staffId.split("_");
            String[] vehicleIdSubParts = vehicleId.split("_");

            if ((role == Role.AGENCY_MANAGER || role == Role.AGENCY_TELLER)
                    && (!updatorIdSubParts[0].equals(vehicleIdSubParts[0])
                            || !updatorIdSubParts[1].equals(vehicleIdSubParts[1]))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true,
                                "Vehicle with id " + vehicleId + " does not exist or is not under your control.",
                                null));
            }
            requestBody.setVehicleId(vehicleId);

            Vehicle resultUpdateVehicle = vehicleService.updateVehicle(requestBody);
            if (resultUpdateVehicle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Vehicle with id " + vehicleId + " does not exist.", null));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<>(false, "Successfully updated vehicle with id " + vehicleId,
                            resultUpdateVehicle));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    @RequestMapping(value = "/deleteVehicle", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVehicle(@RequestHeader(value = "staffId", required = true) String staffId,
            @RequestHeader(value = "role", required = false) Role role,
            @RequestParam(value = "vehicle_id", required = true) String vehicleId) {
        try {

            String[] deletorIdSubParts = staffId.split("_");
            String[] vehicleIdSubParts = vehicleId.split("_");

            if ((role == Role.AGENCY_MANAGER || role == Role.AGENCY_TELLER)
                    && (!deletorIdSubParts[0].equals(vehicleIdSubParts[0])
                            || !deletorIdSubParts[1].equals(vehicleIdSubParts[1]))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true,
                                "Vehicle with id " + vehicleId + " does not exist or is not under your control.",
                                null));
            }

            vehicleService.deleteVehicle(vehicleId);

            return ResponseEntity.ok()
                    .body(new Response<>(false, "Successfully deleted vehicle with id " + vehicleId, null));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    @PostMapping("/addShipmentToVehicle")
    public ResponseEntity<?> addShipmentToVehicle(@RequestParam String vehicleId,
            @RequestBody List<String> shipmentIds) {
        try {

            Optional<Vehicle> resultGettingOneVehicle = vehicleService.checkExitVehicleById(vehicleId);
            if (resultGettingOneVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Vehicle with id " + vehicleId + " does not exist.", null));
            }

            vehicleService.addShipmentToVehicle(vehicleId, shipmentIds);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(false, "Successfully added shipments.", shipmentIds));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

    @PostMapping("/deleteShipmentFromVehicle")
    public ResponseEntity<?> deleteShipmentFromVehicle(@RequestParam String vehicleId,
            @RequestBody List<String> shipmentIds) {
        try {

            Optional<Vehicle> resultGettingOneVehicle = vehicleService.checkExitVehicleById(vehicleId);
            if (resultGettingOneVehicle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>(true, "Vehicle with id " + vehicleId + " does not exist.", null));
            }

            vehicleService.deleteShipmentFromVehicle(vehicleId, shipmentIds);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(false, "Successfully deleted shipments.", shipmentIds));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(true, ex.getMessage(), null));
        }
    }

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