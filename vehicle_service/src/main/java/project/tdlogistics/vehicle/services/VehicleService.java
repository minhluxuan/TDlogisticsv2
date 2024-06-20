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

    public Optional<Vehicle> checkExistVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public Optional<Vehicle> checkExistVehicleByLicense(String license) {
        return vehicleRepository.findByLicensePlate(license);
    }

    public Optional<Vehicle> checkExistVehicle(Vehicle criteria) {

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
        return vehicleRepository.findAll(example);
    }

    public Vehicle createNewVehicle(Vehicle newVehicle) {
        return vehicleRepository.save(newVehicle);
    }

    public Vehicle updateVehicle(Vehicle updatedVehicle) {
        Optional<Vehicle> existingVehicleOpt = vehicleRepository.findById(updatedVehicle.getVehicleId());

        if (!existingVehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Xe với id " + updatedVehicle.getVehicleId() + " không tồn tại");
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
            throw new EntityNotFoundException("Xe với id " + vehicleId + " không tồn tại");
        }
        Vehicle resultGetOneVehicle = vehicleOpt.get();
        Float currentMass = resultGetOneVehicle.getMass();
        for (String shipmentId : shipmentIds) {
            // call for shipment service
            Optional<Shipment> resultGetOneShipmentOpt = shipmentService.getOneShipment(shipmentId);
            if (resultGetOneShipmentOpt.isEmpty()) {
                throw new EntityNotFoundException("Shipment với id " + shipmentId + " không tồn tại");
            }
            Shipment resultGetOneShipment = resultGetOneShipmentOpt.get();

            if (!resultGetOneVehicle.getShipmentIds().contains(resultGetOneShipment.getShipmentId())) {
                resultGetOneVehicle.getShipmentIds().add(resultGetOneShipment.getShipmentId());
                currentMass += resultGetOneShipment.getMass();
            } else {
                throw new IllegalArgumentException(
                        "Shipment với id " + shipmentId + " đã thuộc về xe có id " + vehicleId);

            }
        }
        resultGetOneVehicle.setMass(currentMass);
        vehicleRepository.save(resultGetOneVehicle);

    }

    public void deleteShipmentFromVehicle(String vehicleId, List<String> shipmentIds) throws Exception {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Xe với id " + vehicleId + " không tồn tại");
        }
        Vehicle vehicle = vehicleOpt.get();
        Float currentMass = vehicle.getMass();
        for (String shipmentId : shipmentIds) {
            // call for shipment service
            Optional<Shipment> shipmentOpt = shipmentService.getOneShipment(shipmentId);
            if (shipmentOpt.isEmpty()) {
                throw new EntityNotFoundException("Shipment với id " + shipmentId + " không tồn tại");
            }
            Shipment shipment = shipmentOpt.get();
            if (vehicle.getShipmentIds().contains(shipment.getShipmentId())) {
                vehicle.getShipmentIds().remove(shipment.getShipmentId());
                currentMass -= shipment.getMass();
            } else {
                throw new IllegalArgumentException(
                        "Shipment với id " + shipmentId + " không ở trong xe có id " + vehicleId);
            }
        }
        vehicle.setMass(currentMass);
        vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(String vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            throw new EntityNotFoundException("Xe với id " + vehicleId + " không tồn tại");
        }
        Vehicle vehicle = vehicleOpt.get();
        if (vehicle.getShipmentIds().size() > 0) {
            throw new IllegalArgumentException("Xe với id " + vehicleId + " đang chứa shipment");
        }
        vehicleRepository.deleteById(vehicleId);
    }
}