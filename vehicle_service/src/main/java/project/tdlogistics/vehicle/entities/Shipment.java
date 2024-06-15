package project.tdlogistics.vehicle.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import project.tdlogistics.vehicle.configurations.ListToStringConverter;
import project.tdlogistics.vehicle.configurations.ListMapToStringConverter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @Column(name = "shipment_id", nullable = false, length = 30)
    private String shipmentId;

    @Column(name = "agency_id", length = 25)
    private String agencyId;

    @Column(name = "agency_id_dest", length = 30)
    private String agencyIdDest;

    @Column(name = "long_source")
    private Float longSource;

    @Column(name = "lat_source")
    private Float latSource;

    @Column(name = "current_agency_id", length = 30)
    private String currentAgencyId;

    @Column(name = "current_lat")
    private Float currentLat;

    @Column(name = "current_long")
    private Float currentLong;

    @Column(name = "long_destination")
    private Float longDestination;

    @Column(name = "lat_destination")
    private Float latDestination;

    @Column(name = "transport_partner_id", length = 25)
    private String transportPartnerId;

    @Column(name = "staff_id", length = 25)
    private String staffId;

    @Column(name = "vehicle_id", length = 20)
    private String vehicleId;

    @Column(name = "mass", columnDefinition = "float default 0")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float mass = 0.0f;

    @Column(name = "order_ids", columnDefinition = "longtext")
    @Convert(converter = ListToStringConverter.class)
    private List<String> orderIds;

    @Column(name = "parent", length = 20)
    private String parent;

    @Column(name = "status", columnDefinition = "int(10) default 0")
    private Integer status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update", columnDefinition = "datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private Date lastUpdate;

    @Column(name = "journey", columnDefinition = "LONGTEXT")
    @Convert(converter = ListToStringConverter.class)
    private List<String> journey;

    // Getters and Setters

    public Shipment(String shipmentId, String agencyId, String agencyIdDest, Float longSource, Float latSource,
            String currentAgencyId, Float currentLat, Float currentLong, Float longDestination, Float latDestination,
            String transportPartnerId, String staffId, String vehicleId, Float mass, List<String> orderIds,
            String parent, Integer status, Date createdAt, Date lastUpdate, List<String> journey) {
        this.shipmentId = shipmentId;
        this.agencyId = agencyId;
        this.agencyIdDest = agencyIdDest;
        this.longSource = longSource;
        this.latSource = latSource;
        this.currentAgencyId = currentAgencyId;
        this.currentLat = currentLat;
        this.currentLong = currentLong;
        this.longDestination = longDestination;
        this.latDestination = latDestination;
        this.transportPartnerId = transportPartnerId;
        this.staffId = staffId;
        this.vehicleId = vehicleId;
        this.mass = mass;
        this.orderIds = orderIds;
        this.parent = parent;
        this.status = status;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.journey = journey;
    }

    public Shipment() {
    }

    public Shipment(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyIdDest() {
        return agencyIdDest;
    }

    public void setAgencyIdDest(String agencyIdDest) {
        this.agencyIdDest = agencyIdDest;
    }

    public Float getLongSource() {
        return longSource;
    }

    public void setLongSource(Float longSource) {
        this.longSource = longSource;
    }

    public Float getLatSource() {
        return latSource;
    }

    public void setLatSource(Float latSource) {
        this.latSource = latSource;
    }

    public String getCurrentAgencyId() {
        return currentAgencyId;
    }

    public void setCurrentAgencyId(String currentAgencyId) {
        this.currentAgencyId = currentAgencyId;
    }

    public Float getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Float currentLat) {
        this.currentLat = currentLat;
    }

    public Float getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(Float currentLong) {
        this.currentLong = currentLong;
    }

    public Float getLongDestination() {
        return longDestination;
    }

    public void setLongDestination(Float longDestination) {
        this.longDestination = longDestination;
    }

    public Float getLatDestination() {
        return latDestination;
    }

    public void setLatDestination(Float latDestination) {
        this.latDestination = latDestination;
    }

    public String getTransportPartnerId() {
        return transportPartnerId;
    }

    public void setTransportPartnerId(String transportPartnerId) {
        this.transportPartnerId = transportPartnerId;
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

    public Float getMass() {
        return mass;
    }

    public void setMass(Float mass) {
        this.mass = mass;
    }

    public List<String> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<String> orderIds) {
        this.orderIds = orderIds;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<String> getJourney() {
        return journey;
    }

    public void setJourney(List<String> journey) {
        this.journey = journey;
    }

    @Override
    public String toString() {
        return "Shipment [shipmentId=" + shipmentId + ", agencyId=" + agencyId + ", agencyIdDest=" + agencyIdDest
                + ", longSource=" + longSource + ", latSource=" + latSource + ", currentAgencyId=" + currentAgencyId
                + ", currentLat=" + currentLat + ", currentLong=" + currentLong + ", longDestination=" + longDestination
                + ", latDestination=" + latDestination + ", transportPartnerId=" + transportPartnerId + ", staffId="
                + staffId + ", vehicleId=" + vehicleId + ", mass=" + mass + ", orderIds=" + orderIds + ", parent="
                + parent + ", status=" + status + ", createdAt=" + createdAt + ", lastUpdate=" + lastUpdate
                + ", journey=" + journey + "]";
    }

}