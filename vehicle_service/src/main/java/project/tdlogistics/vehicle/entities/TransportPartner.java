package project.tdlogistics.vehicle.entities;

import org.hibernate.validator.constraints.EAN;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportPartner {
    private String name;

    TransportPartner(String name) {
        this.name = name;
    }

    TransportPartner() {
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
