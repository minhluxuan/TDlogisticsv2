package project.tdlogistics.shipments.entities;


import jakarta.persistence.*;
import project.tdlogistics.shipments.configurations.ListToStringConverter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "agency")
public class Agency {

    @Column(name = "level")
    private Byte level;

    @Transient
    private String type;

    @Id
    @Column(name = "agencyId")
    private String agencyId;

    @Column(name = "agencyName")
    private String agencyName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "town")
    private String town;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    @Column(name = "bin")
    private String bin;

    @Column(name = "bank")
    private String bank;

    @Column(name = "commission_rate")
    private Float commissionRate;

    @Column(name = "revenue")
    private Float revenue;

    @Column(name = "contract")
    private String contract;

    @Column(name = "managed_wards")
    @Convert(converter = ListToStringConverter.class)
    private List<String> managedWards;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "individual_company")
    private Boolean individualCompany;

    @OneToOne(mappedBy = "agency", cascade = CascadeType.ALL)
    @JsonManagedReference
    private AgencyCompany agencyCompany;

    @Transient
    private AgencyAdmin agencyAdmin;

    public Agency() {
    }

    public Agency(Byte level, String type, String agencyId, String agencyName, String email, String phoneNumber,
            String postalCode, String province, String district, String town, String detailAddress, Float latitude,
            Float longitude, String bin, String bank, Float commissionRate, Float revenue, String contract,
            List<String> managedWards, LocalDateTime createdAt, LocalDateTime lastUpdate, Boolean individualCompany,
            AgencyCompany agencyCompany, AgencyAdmin agencyAdmin) {
        this.level = level;
        this.type = type;
        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
        this.province = province;
        this.district = district;
        this.town = town;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bin = bin;
        this.bank = bank;
        this.commissionRate = commissionRate;
        this.revenue = revenue;
        this.contract = contract;
        this.managedWards = managedWards;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.individualCompany = individualCompany;
        this.agencyCompany = agencyCompany;
        this.agencyAdmin = agencyAdmin;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIndividualCompany() {
        return individualCompany;
    }

    public void setIndividualCompany(Boolean individualCompany) {
        this.individualCompany = individualCompany;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
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

    public Float getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Float commissionRate) {
        this.commissionRate = commissionRate;
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

    public List<String> getManagedWards() {
        return managedWards;
    }

    public void setManagedWards(List<String> managedWards) {
        this.managedWards = managedWards;
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

    public AgencyCompany getAgencyCompany() {
        return agencyCompany;
    }

    public void setAgencyCompany(AgencyCompany agencyCompany) {
        this.agencyCompany = agencyCompany;
    }

    public AgencyAdmin getAgencyAdmin() {
        return agencyAdmin;
    }

    public void setAgencyAdmin(AgencyAdmin agencyAdmin) {
        this.agencyAdmin = agencyAdmin;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = this.lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Agency [level=" + level + ", type=" + type + ", agencyId=" + agencyId + ", agencyName=" + agencyName
                + ", email=" + email + ", phoneNumber=" + phoneNumber + ", postalCode=" + postalCode + ", province="
                + province + ", district=" + district + ", town=" + town + ", detailAddress=" + detailAddress
                + ", latitude=" + latitude + ", longitude=" + longitude + ", bin=" + bin + ", bank=" + bank
                + ", commissionRate=" + commissionRate + ", revenue=" + revenue + ", contract=" + contract
                + ", managedWards=" + managedWards + ", createdAt=" + createdAt + ", lastUpdate=" + lastUpdate
                + ", individualCompany=" + individualCompany + "]";
    }
}
