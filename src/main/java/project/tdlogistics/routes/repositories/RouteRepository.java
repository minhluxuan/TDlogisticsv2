package project.tdlogistics.routes.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.tdlogistics.routes.entities.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer>, RouteRepositoryCustom {

    Optional<Route> findByVehicleId(String vehicleId);

    List<Route> findByDepartureTimeBetween(LocalDateTime startDepartureTime, LocalDateTime endDepartureTime);

}
