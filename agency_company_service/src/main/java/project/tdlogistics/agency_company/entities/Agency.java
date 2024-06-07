package project.tdlogistics.agency_company.entities;

import jakarta.persistence.*;
import project.tdlogistics.agency_company.configurations.ListToStringConverter;
import project.tdlogistics.agency_company.entities.DTOs.AgencyCreateDTO;

import java.util.List;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

// import org.hibernate.mapping.List;

@Entity
@Table(name = "agency")
public class Agency {

    @Column(name = "level")
    private Byte level;

    @Id
    @Column(name = "agency_id")
    private String agency_id;

    @Column(name = "agency_name")
    private String agency_name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "postal_code")
    private String postal_code;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "town")
    private String town;

    @Column(name = "detail_address")
    private String detail_address;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    @Column(name = "bin")
    private String bin;

    @Column(name = "bank")
    private String bank;

    @Column(name = "commission_rate")
    private Float commission_rate;

    @Column(name = "revenue")
    private Float revenue;

    @Column(name = "contract")
    private String contract;

    @Column(name = "managed_wards")
    @Convert(converter = ListToStringConverter.class)
    private List<String> managed_wards;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "last_update")
    private LocalDateTime last_update;

    @Column(name = "individual_company")
    private Boolean individual_company;

    @Transient
    private String company_name;

    @Transient
    private String tax_number;

    @Transient
    @Convert(converter = ListToStringConverter.class)
    private List<String> license;

    // @OneToOne(optional = true)
    // @JoinColumn(name = "agency_id")
    // @JsonManagedReference
    // private AgencyCompany agency_company;

    // getters and setters

    public Agency() {
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agencyId) {
        this.agency_id = agencyId;
    }

    public String getAgency_name() {
        return agency_name;
    }

    public void setAgency_name(String agencyName) {
        this.agency_name = agencyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phoneNumber) {
        this.phone_number = phoneNumber;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postalCode) {
        this.postal_code = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDetail_address() {
        return detail_address;
    }

    public void setDetail_address(String detailAddress) {
        this.detail_address = detailAddress;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Float getCommission_rate() {
        return commission_rate;
    }

    public void setCommission_rate(Float commissionRate) {
        this.commission_rate = commissionRate;
    }

    public Float getRevenue() {
        return revenue;
    }

    public void setRevenue(Float revenue) {
        this.revenue = revenue;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public List<String> getManaged_wards() {
        return managed_wards;
    }

    public void setManaged_wards(List<String> managedWards) {
        this.managed_wards = managedWards;
    }

    // public void setManagedWards(List<String> listOfWard) {
    // ObjectMapper objectMapper = new ObjectMapper();
    // try {
    // this.managedWards = objectMapper.writeValueAsString(listOfWard);
    // } catch (JsonProcessingException e) {
    // e.printStackTrace();
    // }
    // }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }

    public LocalDateTime getLast_update() {
        return last_update;
    }

    public void setLast_update(LocalDateTime lastUpdate) {
        this.last_update = lastUpdate;
    }

    public Boolean isIndividual_company() {
        return individual_company;
    }

    public void setIndividual_company(Boolean individualCompany) {
        this.individual_company = individualCompany;
    }

    // public AgencyCompany getAgency_company() {
    // return agency_company;
    // }

    // public void setAgency_company(AgencyCompany agencyCompany) {
    // this.agency_company = agencyCompany;
    // }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getTax_number() {
        return tax_number;
    }

    public void setTax_number(String tax_number) {
        this.tax_number = tax_number;
    }

    public List<String> getLicense() {
        return license;
    }

    public void setLicense(List<String> license) {
        this.license = license;
    }

    public static Agency fromDTO(AgencyCreateDTO dto) {
        Agency agency = new Agency();
        agency.setLevel(dto.getLevel());
        agency.setAgency_name(dto.getAgency_name());
        agency.setEmail(dto.getEmail());
        agency.setPhone_number(dto.getPhone_number());
        agency.setPostal_code(dto.getPostal_code());
        agency.setProvince(dto.getProvince());
        agency.setDistrict(dto.getDistrict());
        agency.setTown(dto.getTown());
        agency.setDetail_address(dto.getDetail_address());
        agency.setLatitude(dto.getLatitude());
        agency.setLongitude(dto.getLongitude());
        agency.setBin(dto.getBin());
        agency.setBank(dto.getBank());
        agency.setCommission_rate(dto.getCommission_rate());
        agency.setRevenue(dto.getRevenue());
        agency.setContract(dto.getContract());
        agency.setManaged_wards(dto.getManaged_wards());
        agency.setCreated_at(dto.getCreated_at());
        agency.setLast_update(dto.getLast_update());
        agency.setIndividual_company(dto.getIndividual_company());
        agency.setCompany_name(dto.getCompany_name());
        agency.setTax_number(dto.getTax_number());
        agency.setLicense(dto.getLicense());

        return agency;
    }
}