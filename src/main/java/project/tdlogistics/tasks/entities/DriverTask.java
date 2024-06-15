package project.tdlogistics.tasks.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_tasks")
public class DriverTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "shipment_id", nullable = false, length = 30)
    private String shipmentId;

    @Column(name = "staff_id", nullable = false, length = 25)
    private String staffId;

    @Column(name = "vehicle_id", nullable = false, length = 20)
    private String vehicleId;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // Constructors, getters, setters, etc.

    public DriverTask() {

    }
    

    public DriverTask(int id, String shipmentId, String staffId, String vehicleId, LocalDateTime createdAt) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.staffId = staffId;
        this.vehicleId = vehicleId;
        this.createdAt = createdAt;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "Driver [id=" + id + ", shipmentId=" + shipmentId + ", staffId=" + staffId + ", vehicleId=" + vehicleId
                + ", createdAt=" + createdAt + ", getId()=" + getId() + ", getShipmentId()=" + getShipmentId()
                + ", getStaffId()=" + getStaffId() + ", getVehicleId()=" + getVehicleId() + ", getClass()=" + getClass()
                + ", getCreatedAt()=" + getCreatedAt() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }

    
}
