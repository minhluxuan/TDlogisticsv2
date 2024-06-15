package project.tdlogistics.vehicle.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    private String name;
    private String shipmentId;
    private String staffId;

    Task(String name) {
        this.name = name;
    }

    public Task(String shipmentId, String staffId) {
        this.shipmentId = shipmentId;
        this.staffId = staffId;
    }

    public Task() {
    }

    // getter
    public String getName() {
        return name;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    // getter
    public String getShipmentId() {
        return shipmentId;
    }

    // setter
    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    // getter
    public String getStaffId() {
        return staffId;
    }

    // setter
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
