package project.tdlogistics.agency_company.entities;

import jakarta.persistence.*;
import project.tdlogistics.agency_company.configurations.ListToStringConverter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "agency_company")
public class AgencyCompany implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "agency_id", referencedColumnName = "agencyId")
    @JsonBackReference
    private Agency agency;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "license")
    @Convert(converter = ListToStringConverter.class)
    private List<String> license;

    public AgencyCompany() {
    }

    public AgencyCompany(Agency agency, String taxNumber, String companyName, List<String> license) {
        this.agency = agency;
        this.taxNumber = taxNumber;
        this.companyName = companyName;
        this.license = license;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getLicense() {
        return license;
    }

    public void setLicense(List<String> license) {
        this.license = license;
    }

    @Override
    public String toString() {
        return "AgencyCompany [id=" + id + ", agency=" + agency + ", taxNumber=" + taxNumber + ", companyName="
                + companyName + ", license=" + license + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AgencyCompany cloned = (AgencyCompany) super.clone();
        // Sao chép các đối tượng con nếu có
        return cloned;
    }
}