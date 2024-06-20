package project.tdlogistics.routes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import project.tdlogistics.routes.entities.Response;
import project.tdlogistics.routes.entities.Route;
import project.tdlogistics.routes.entities.Vehicle;
import project.tdlogistics.routes.repositories.RouteRepository;
import project.tdlogistics.routes.services.RouteService;
import project.tdlogistics.routes.services.VehicleService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/routes")
public class RouteRestController {

    final private RouteService routeService;
    final private VehicleService vehicleService;

    public RouteRestController(RouteService routeService, VehicleService vehicleService) {
        this.routeService = routeService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("test")
    public ResponseEntity<String> test() {
        try {
            String startTime = "10:15:30";
            LocalTime timeStart = LocalTime.parse(startTime);

            String endTime = "15:15:15";
            LocalTime timeEnd = LocalTime.parse(endTime);

            List<Route> routes = routeService.getRoutes(new Route(), timeStart, timeEnd);
            System.out.println(routes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
        // call route service

        return ResponseEntity.ok("Test");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewRoute(@RequestBody Route body) {
        // TODO: process POST request

        try {
            /* validation here */

            /*
             * call admininstrative service to check for province in source and destination
             */

            /*
             * call vehicle service to check for vehicle
             */

            Optional<Route> route = routeService.checkExistRoute(body);
            if (route.isPresent()) {
                return ResponseEntity.status(400).body(new Response(true, "Route already exist", null));
            }

            Route newRoute = routeService.createNewRoute(body);
            if (newRoute == null) {
                return ResponseEntity.status(400).body(new Response(true, "Failed to create new route", null));
            }

            return ResponseEntity.status(200).body(new Response(false, "oke", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Response(true, e.getMessage(), null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getRoutes(@RequestBody Route routeBody,
            @RequestParam(required = false) String startDepartureTime,
            @RequestParam(required = false) String endDepartureTime) {

        try {

            /*
             * validation here
             */

            LocalTime timeStart = null;
            LocalTime timeEnd = null;

            if (startDepartureTime != null) {
                timeStart = LocalTime.parse(startDepartureTime);
            }

            if (endDepartureTime != null) {
                timeEnd = LocalTime.parse(endDepartureTime);
            }

            List<Route> routes = routeService.getRoutes(routeBody, timeStart, timeEnd);
            return ResponseEntity.status(200).body(new Response(false, "oke", routes));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Response(true, e.getMessage(), null));
        }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Integer id, @RequestBody Route body) {
        try {
            /* validate id */
            /* validate route id */
            /* validate body */

            if (body.getVehicleId() != null && !vehicleService.checkExistVehicle(body.getVehicleId())) {
                return ResponseEntity.status(500).body(new Response<>(true, "Vehicle not found", null));
            }

            Optional<Route> resultGetRouteToUpdate = routeService.checkExistRoute(id);
            if (resultGetRouteToUpdate.isEmpty()) {
                return ResponseEntity.status(500).body(new Response<>(true, "Route not found", null));
            }

            Optional<Route> resultCheckExitRoute = routeService.checkExistRoute(body);
            if (resultCheckExitRoute.isPresent()) {
                return ResponseEntity.status(500).body(new Response<>(true, "Route already exist", null));
            }

            Route updatedRoute = routeService.updateRoute(id, body);
            if (updatedRoute == null) {
                return ResponseEntity.status(500).body(new Response<>(true, "Failed to update route", null));
            }

            return ResponseEntity.status(200).body(new Response<>(false, "oke", updatedRoute));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Response<>(true, e.getMessage(), null));
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Integer id) {
        try {
            /* validate id */
            Optional<Route> resultGetRouteToDelete = routeService.checkExistRoute(id);
            if (resultGetRouteToDelete.isEmpty()) {
                return ResponseEntity.status(500).body(new Response<>(true, "Route not found", null));
            }

            routeService.deleteRoute(id);
            return ResponseEntity.status(200).body(new Response<>(false, "oke", null));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Response<>(true, e.getMessage(), null));
        }
    }

}