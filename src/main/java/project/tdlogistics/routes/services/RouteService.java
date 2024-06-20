package project.tdlogistics.routes.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import project.tdlogistics.routes.entities.Route;
import project.tdlogistics.routes.repositories.RouteRepository;

import project.tdlogistics.routes.configurations.MyBeanUtils;

import java.util.Optional;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final VehicleService vehicleService;

    public RouteService(RouteRepository routeRepository, VehicleService vehicleService) {
        this.routeRepository = routeRepository;
        this.vehicleService = vehicleService;
    }

    public Optional<Route> checkExistRoute(Route criteria) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues();
        Example<Route> example = Example.of(criteria, matcher);
        return routeRepository.findOne(example);
    }

    public List<Route> getRoutes(Route criteria, LocalTime startDepartureTime, LocalTime endDepartureTime)
            throws Exception {

        // check if there not exist startDeparturetime and endtime concurrently
        if ((startDepartureTime == null && endDepartureTime != null)
                || (startDepartureTime != null && endDepartureTime == null)) {
            throw new IllegalArgumentException("Start time and end time must be provided together");
        }

        if (startDepartureTime != null && endDepartureTime != null) {
            if (startDepartureTime.isAfter(endDepartureTime)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
        }

        if (startDepartureTime != null && endDepartureTime != null) {
            return routeRepository.findInDepartureTimeRangeAndOtherCriteria(criteria, startDepartureTime,
                    endDepartureTime);
        }

        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues();
        Example<Route> example = Example.of(criteria, matcher);
        return routeRepository.findAll(example);

    }

    public Route createNewRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route updateRoute(Integer id, Route route) throws Exception {
        Optional<Route> existingRoute = routeRepository.findById(id);
        if (existingRoute.isEmpty()) {
            throw new IllegalArgumentException("Route not found");
        }

        Route routeToUpdate = existingRoute.get();
        MyBeanUtils.copyNonNullProperties(route, routeToUpdate);
        return routeRepository.save(routeToUpdate);
    }

    public void deleteRoute(Integer id) {
        Optional<Route> existingRoute = routeRepository.findById(id);
        if (existingRoute.isEmpty()) {
            throw new IllegalArgumentException("Route not found");
        }
        routeRepository.deleteById(id);
    }

    public Optional<Route> checkExistRoute(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkExistRoute'");
    }

}