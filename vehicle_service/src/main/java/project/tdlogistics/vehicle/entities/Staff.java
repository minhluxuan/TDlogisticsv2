package project.tdlogistics.vehicle.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    public Staff() {
    }

    private String staffId;

    private String name;

    public Staff(String staffId) {
        this.staffId = staffId;
    }

    // getter
    public String getStaffId() {
        return staffId;
    }

    // setter

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    // getter
    public String getName() {
        return name;
    }

    // setter

    public void setName(String name) {
        this.name = name;
    }

}
