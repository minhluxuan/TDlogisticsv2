package project.tdlogistics.vehicle.services;

import project.tdlogistics.vehicle.entities.Agency;
import project.tdlogistics.vehicle.entities.Response;
import project.tdlogistics.vehicle.entities.Shipment;
import project.tdlogistics.vehicle.entities.Staff;
import project.tdlogistics.vehicle.entities.TransportPartner;
import project.tdlogistics.vehicle.entities.Vehicle;
import project.tdlogistics.vehicle.repositories.VehicleRepository;
import project.tdlogistics.vehicle.configurations.MyBeanUtils;
import project.tdlogistics.vehicle.services.ShipmentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VehicleService {

    private final ShipmentService shipmentService;

    private final AgencyService agencyService;

    private final TransportPartnerService transportPartnerService;

    private final StaffService staffService;

    private final VehicleRepository vehicleRepository;
    private final MyBeanUtils myBeanUtils;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, MyBeanUtils myBeanUtils,
            ShipmentService shipmentService, AgencyService agencyService,
            TransportPartnerService transportPartnerService, StaffService staffService) {
        this.myBeanUtils = myBeanUtils;
        this.vehicleRepository = vehicleRepository;
        this.shipmentService = shipmentService;
        this.agencyService = agencyService;
        this.transportPartnerService = transportPartnerService;
        this.staffService = staffService;
    }

    public Optional<Vehicle> checkExitVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public Optional<Vehicle> checkExitVehicleByLicense(String license) {
        return vehicleRepository.findByLicensePlate(license);

    }

    public Optional<Vehicle> checkExitVehicle(Vehicle criteria) {

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Vehicle> example = Example.of(criteria, matcher);
        List<Vehicle> result = vehicleRepository.findAll(example);
        if (result.size() > 0) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Vehicle> getOneVehicle(Vehicle criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Vehicle> example = Example.of(criteria, matcher);
        return vehicleRepository.findOne(example);
    }

    public List<Vehicle> getManyVehicles(Vehicle criteria) throws Exception {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Vehicle> example = Example.of(criteria, matcher);
        List<Vehicle> resultGetVehicles = vehicleRepository.findAll(example);
        for (Vehicle vehicle : resultGetVehicles) {
            // transportPartnerService.getOneTransportPartner(vehicle.getTransportPartnerId());
            Agency resultGetOneAgency = agencyService.getOneAgencyById(vehicle.getAgencyId());
            if (resultGetOneAgency == null) {
                throw new EntityNotFoundException("Agency with id " + vehicle.getAgencyId() + " not found");
            }
            final String agencyName = resultGetOneAgency.getName();
            vehicle.setAgencyName(agencyName);
            // staff

            Optional<Staff> resultGetOneStaffOpt = staffService.getOneStaffById(vehicle.getStaffId());
            if (resultGetOneStaffOpt.isEmpty()) {
                throw new EntityNotFoundException("Staff with id " + vehicle.getStaffId() + " not found");
            }

            Staff resultGetOneStaff = resultGetOneStaffOpt.get();
            final String staffName = resultGetOneStaff.getName();
            vehicle.setStaffName(staffName);

            TransportPartner resultGetOneTransportPartner = transportPartnerService
                    .getOneTransportPartnerById(vehicle.getTransportPartnerId());
            if (resultGetOneTransportPartner != null) {
                final String transportPartnerName = resultGetOneTransportPartner.getName();
                vehicle.setTransportPartnerName(transportPartnerName);

            }
        }
        return null;

    }

    public Vehicle createNewVehicle(Vehicle newVehicle) {
        return vehicleRepository.save(newVehicle);
    }

    public Vehicle updateVehicle(Vehicle updatedVehicle) {
        Optional<Vehicle> existingVehicleOpt = vehicleRepository.findById(updatedVehicle.getVehicleId());

        if (!existingVehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Vehicle with id " + updatedVehicle.getVehicleId() + " not found");
        }

        Vehicle existingVehicle = existingVehicleOpt.get();
        MyBeanUtils.copyNonNullProperties(updatedVehicle, existingVehicle);

        return vehicleRepository.save(existingVehicle);
    }

    // should be in the shipment service

    // public List<Shipment> getVehicleShipmentIds(Vehicle vehicle) throws Exception
    // {
    // }
    public void addShipmentToVehicle(String vehicleId, List<String> shipmentIds) throws Exception {

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Vehicle with id " + vehicleId + " not found");
        }
        Vehicle resultGetOneVehicle = vehicleOpt.get();
        Float currentMass = resultGetOneVehicle.getMass();
        for (String shipmentId : shipmentIds) {
            // call for shipment service
            Optional<Shipment> resultGetOneShipmentOpt = shipmentService.getOneShipment(shipmentId);
            if (resultGetOneShipmentOpt.isEmpty()) {
                throw new EntityNotFoundException("Shipment with id " + shipmentId + " not found");
            }
            Shipment resultGetOneShipment = resultGetOneShipmentOpt.get();

            if (!resultGetOneVehicle.getShipmentIds().contains(resultGetOneShipment.getShipmentId())) {
                resultGetOneVehicle.getShipmentIds().add(resultGetOneShipment.getShipmentId());
                currentMass += resultGetOneShipment.getMass();
            } else {
                throw new IllegalArgumentException(
                        "Shipment with id " + shipmentId + " already exists in vehicle with id " + vehicleId);

            }
        }
        resultGetOneVehicle.setMass(currentMass);
        vehicleRepository.save(resultGetOneVehicle);

    }

    public void deleteShipmentFromVehicle(String vehicleId, List<String> shipmentIds) throws Exception {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Vehicle with id " + vehicleId + " not found");
        }
        Vehicle vehicle = vehicleOpt.get();
        Float currentMass = vehicle.getMass();
        for (String shipmentId : shipmentIds) {
            // call for shipment service
            Optional<Shipment> shipmentOpt = shipmentService.getOneShipment(shipmentId);
            if (shipmentOpt.isEmpty()) {
                throw new EntityNotFoundException("Shipment with id " + shipmentId + " not found");
            }
            Shipment shipment = shipmentOpt.get();
            if (vehicle.getShipmentIds().contains(shipment.getShipmentId())) {
                vehicle.getShipmentIds().remove(shipment.getShipmentId());
                currentMass -= shipment.getMass();
            } else {
                throw new IllegalArgumentException(
                        "Shipment with id " + shipmentId + " does not exist in vehicle with id " + vehicleId);
            }
        }
        vehicle.setMass(currentMass);
        vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(String vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Vehicle with id " + vehicleId + " not found");
        }
        Vehicle vehicle = vehicleOpt.get();
        if (vehicle.getShipmentIds().size() > 0) {
            throw new IllegalArgumentException("Vehicle with id " + vehicleId + " has shipments");
        }
        vehicleRepository.deleteById(vehicleId);
    }
}