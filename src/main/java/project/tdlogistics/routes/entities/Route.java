package project.tdlogistics.routes.entities;

import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vehicle_id", nullable = false, length = 20)
    private String vehicleId;

    @Column(name = "source", nullable = false, length = 30)
    private String source;

    @Column(name = "destination", nullable = false, length = 30)
    private String destination;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    // getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "Route [departureTime=" + departureTime + ", destination=" + destination + ", id=" + id + ", source="
                + source + ", vehicleId=" + vehicleId + "]";
    }

}