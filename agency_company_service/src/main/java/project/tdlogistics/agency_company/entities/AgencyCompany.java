package project.tdlogistics.agency_company.entities;

// import org.hibernate.mapping.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import project.tdlogistics.agency_company.configurations.ListToStringConverter;

import java.util.List;

@Entity
@Table(name = "agency_company")
public class AgencyCompany {

    @Id
    @Column(name = "agency_id", nullable = false)
    private String agency_id;

    @Column(name = "tax_number")
    private String tax_number;

    @Column(name = "company_name", nullable = false)
    private String company_name;

    @Column(name = "license")
    @Convert(converter = ListToStringConverter.class)
    private List<String> license;

    // @OneToOne(optional = false)
    // @JoinColumn(name = "agency_id")
    // @JsonBackReference
    // private Agency agency;

    // getters and setters

    public AgencyCompany(String agency_id, String tax_number, String company_name, List<String> license) {
        this.agency_id = agency_id;
        this.tax_number = tax_number;
        this.company_name = company_name;
        this.license = license;
    }

    public AgencyCompany() {
    }

    public String getAgency_id() {

        return agency_id;
    }

    public void setAgency_id(String agencyId) {
        this.agency_id = agencyId;
    }

    public String getTax_number() {
        return tax_number;
    }

    public void setTax_number(String taxNumber) {
        this.tax_number = taxNumber;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String companyName) {
        this.company_name = companyName;
    }

    public List<String> getLicense() {
        return license;
    }

    public void setLicense(List<String> license) {
        this.license = license;
    }

    // public Agency getAgency() {
    // return agency;
    // }

    // public void setAgency(Agency agency) {
    // this.agency = agency;
    // }

}