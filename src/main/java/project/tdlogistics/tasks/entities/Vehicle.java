package project.tdlogistics.tasks.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @Column(name = "vehicle_id")
    private String vehicleId;
    
    @Column(name = "transport_partner_id")
    private String transportPartnerId;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "type")
    private String type;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "mass")
    private Float mass;

    @Column(name = "max_load")
    private Float maxLoad;

    @Column(name = "shipment_ids")
    private List<String> shipmentIds;

    @Column(name = "busy")
    private Boolean busy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, String transportPartnerId, String agencyId, String staffId, String type,
            String licensePlate, Float mass, Float maxLoad, List<String> shipmentIds, Boolean busy,
            LocalDateTime createdAt, LocalDateTime lastUpdate) {
        this.vehicleId = vehicleId;
        this.transportPartnerId = transportPartnerId;
        this.agencyId = agencyId;
        this.staffId = staffId;
        this.type = type;
        this.licensePlate = licensePlate;
        this.mass = mass;
        this.maxLoad = maxLoad;
        this.shipmentIds = shipmentIds;
        this.busy = busy;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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

    public Float getMaxLoad() {
        return maxLoad;
    }

    public void setMaxLoad(Float maxLoad) {
        this.maxLoad = maxLoad;
    }

    public List<String> getShipmentIds() {
        return shipmentIds;
    }

    public void setShipmentIds(List<String> shipmentIds) {
        this.shipmentIds = shipmentIds;
    }

    public Boolean getBusy() {
        return busy;
    }

    public void setBusy(Boolean busy) {
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

    @Override
    public String toString() {
        return "Vehicle [vehicleId=" + vehicleId + ", transportPartnerId=" + transportPartnerId + ", agencyId="
                + agencyId + ", staffId=" + staffId + ", type=" + type + ", licensePlate=" + licensePlate + ", mass="
                + mass + ", maxLoad=" + maxLoad + ", shipmentIds=" + shipmentIds + ", busy=" + busy + ", createdAt="
                + createdAt + ", lastUpdate=" + lastUpdate + "]";
    }
}