package project.tdlogistics.vehicle.entities.placeholder;

import java.time.LocalDateTime;
import java.util.List;

public class VehicleDTO {

    private String transportPartnerId;
    private String agencyId;
    private String staffId;
    private String vehicleId;
    private String type;
    private String licensePlate;
    private Float mass;
    private Float maxLoads;
    private Byte busy;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate;
    private List<String> shipmentIds;

    // constructors, getters and setters follow with the new camelCase field names

    public VehicleDTO() {

    }

    public VehicleDTO(String transportPartnerId, String agencyId, String staffId, String vehicleId, String type,
            String licensePlate, Float mass, Float maxLoads, Byte busy, LocalDateTime createdAt,
            LocalDateTime lastUpdate, List<String> shipmentIds) {
        this.transportPartnerId = transportPartnerId;
        this.agencyId = agencyId;
        this.staffId = staffId;
        this.vehicleId = vehicleId;
        this.type = type;
        this.licensePlate = licensePlate;
        this.mass = mass;
        this.maxLoads = maxLoads;
        this.busy = busy;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.shipmentIds = shipmentIds;
    }

    public String getTransportPartnerId() {
        return transportPartnerId;
    }

    public void setTransportPartnerId(String transportPartnerId) {
        this.transportPartnerId = transportPartnerId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Float getMass() {
        return mass;
    }

    public void setMass(Float mass) {
        this.mass = mass;
    }

    public Float getMaxLoads() {
        return maxLoads;
    }

    public void setMaxLoads(Float maxLoads) {
        this.maxLoads = maxLoads;
    }

    public Byte getBusy() {
        return busy;
    }

    public void setBusy(Byte busy) {
        this.busy = busy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<String> getShipmentIds() {
        return shipmentIds;
    }

    public void setShipmentIds(List<String> shipmentIds) {
        this.shipmentIds = shipmentIds;
    }

    // getters and setters...

}