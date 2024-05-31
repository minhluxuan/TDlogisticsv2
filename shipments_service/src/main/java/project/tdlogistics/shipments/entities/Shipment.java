package project.tdlogistics.shipments.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @Column(name = "shipment_id", nullable = false, length = 30)
    private String shipment_id;

    @Column(name = "agency_id", length = 25)
    private String agency_id;

    @Column(name = "agency_id_dest", length = 30)
    private String agency_id_dest;

    @Column(name = "long_source")
    private Float long_source;

    @Column(name = "lat_source")
    private Float lat_source;

    @Column(name = "current_agency_id", length = 30)
    private String current_agency_id;

    @Column(name = "current_lat")
    private Float current_lat;

    @Column(name = "current_long")
    private Float current_long;

    @Column(name = "long_destination")
    private Float long_destination;

    @Column(name = "lat_destination")
    private Float lat_destination;

    @Column(name = "transport_partner_id", length = 25)
    private String transport_partner_id;

    @Column(name = "staff_id", length = 25)
    private String staff_id;

    @Column(name = "vehicle_id", length = 20)
    private String vehicle_id;

    @Column(name = "mass", columnDefinition = "float default 0")
    private Float mass = 0.0f;

    @Column(name = "order_ids", columnDefinition = "longtext")
    private String order_ids;

    @Column(name = "parent", length = 20)
    private String parent;

    @Column(name = "status", columnDefinition = "int(10) default 0")
    private Integer status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update", columnDefinition = "datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private Date last_update;

    @Column(name = "journey", columnDefinition = "longtext", length = 16777215)
    private String journey;

    // Getters and Setters

    public String getShipmentId() {
        return shipment_id;
    }

    public void setShipmentId(String shipmentId) {
        this.shipment_id = shipmentId;
    }

    public String getAgencyId() {
        return agency_id;
    }

    public void setAgencyId(String agencyId) {
        this.agency_id = agencyId;
    }

    public String getAgencyIdDest() {
        return agency_id_dest;
    }

    public void setAgencyIdDest(String agencyIdDest) {
        this.agency_id_dest = agencyIdDest;
    }

    public Float getLongSource() {
        return long_source;
    }

    public void setLongSource(Float longSource) {
        this.long_source = longSource;
    }

    public Float getLatSource() {
        return lat_source;
    }

    public void setLatSource(Float latSource) {
        this.lat_source = latSource;
    }

    public String getCurrentAgencyId() {
        return current_agency_id;
    }

    public void setCurrentAgencyId(String currentAgencyId) {
        this.current_agency_id = currentAgencyId;
    }

    public Float getCurrentLat() {
        return current_lat;
    }

    public void setCurrentLat(Float currentLat) {
        this.current_lat = currentLat;
    }

    public Float getCurrentLong() {
        return current_long;
    }

    public void setCurrentLong(Float currentLong) {
        this.current_long = currentLong;
    }

    public Float getLongDestination() {
        return long_destination;
    }

    public void setLongDestination(Float longDestination) {
        this.long_destination = longDestination;
    }

    public Float getLatDestination() {
        return lat_destination;
    }

    public void setLatDestination(Float latDestination) {
        this.lat_destination = latDestination;
    }

    public String getTransportPartnerId() {
        return transport_partner_id;
    }

    public void setTransportPartnerId(String transportPartnerId) {
        this.transport_partner_id = transportPartnerId;
    }

    public String getStaffId() {
        return staff_id;
    }

    public void setStaffId(String staffId) {
        this.staff_id = staffId;
    }

    public String getVehicleId() {
        return vehicle_id;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicle_id = vehicleId;
    }

    public Float getMass() {
        return mass;
    }

    public void setMass(Float mass) {
        this.mass = mass;
    }

    public String getOrderIds() {
        return order_ids;
    }

    public void setOrderIds(String orderIds) {
        this.order_ids = orderIds;
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
        return created_at;
    }

    public void setCreatedAt(Date createdAt) {
        this.created_at = createdAt;
    }

    public Date getLastUpdate() {
        return last_update;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.last_update = lastUpdate;
    }

    public String getJourney() {
        return journey;
    }

    public void setJourney(String journey) {
        this.journey = journey;
    }
}
