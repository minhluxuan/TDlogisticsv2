package project.tdlogistics.agency_company.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {
    private String cccd;

    public Staff() {};

    public Staff(String cccd) {
        this.cccd = cccd;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }
}
