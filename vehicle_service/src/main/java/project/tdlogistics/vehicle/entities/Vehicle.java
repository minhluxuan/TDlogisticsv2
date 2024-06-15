package project.tdlogistics.vehicle.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import project.tdlogistics.vehicle.configurations.ListToStringConverter;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Column(name = "transport_partner_id")
    private String transportPartnerId;

    @Column(name = "agency_id", nullable = false)
    private String agencyId;

    @Column(name = "staff_id")
    private String staffId;

    @Id
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "type")
    private String type;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "mass")
    private Float mass;

    @Column(name = "max_load")
    private Float maxLoads;

    @Column(name = "busy")
    private Byte busy;

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "last_update", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdate;

    @Convert(converter = ListToStringConverter.class)
    private List<String> shipmentIds;

    @Transient
    private String transportPartnerName;

    @Transient
    private String agencyName;

    @Transient
    private String staffName;

    // constructors, getters and setters follow with the new camelCase field names

    public Vehicle() {

    }

    public Vehicle(String transportPartnerId, String agencyId, String staffId, String vehicleId, String type,
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }

    // transient
    public String getTransportPartnerName() {
        return transportPartnerName;
    }

    public void setTransportPartnerName(String transportPartnerName) {
        this.transportPartnerName = transportPartnerName;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

}