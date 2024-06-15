package project.tdlogistics.vehicle.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.vehicle.entities.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    Optional<Vehicle> findByLicensePlate(String vehicleId);

}
