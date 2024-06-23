package project.tdlogistics.transport_partners.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "transport_partner")
public class TransportPartner {
    @Id
    @Column(name = "transport_partner_id")
    private String transportPartnerId;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "transport_partner_name")
    private String transportPartnerName;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "town")
    private String town;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "debit")
    private Float debit;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "bin")
    private String bin;

    @Column(name = "bank")
    private String bank;

    @Column(name = "contract")
    private String contract;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    private TransportPartnerRepresentor representor;

    public TransportPartner() {
    }

    public TransportPartner(String transportPartnerId, String agencyId, String transportPartnerName, String taxCode,
            String province, String district, String town, String detailAddress, Float debit, String phoneNumber,
            String email, String bin, String bank, String contract, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.transportPartnerId = transportPartnerId;
        this.agencyId = agencyId;
        this.transportPartnerName = transportPartnerName;
        this.taxCode = taxCode;
        this.province = province;
        this.district = district;
        this.town = town;
        this.detailAddress = detailAddress;
        this.debit = debit;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bin = bin;
        this.bank = bank;
        this.contract = contract;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTransportPartnerId() {
        return transportPartnerId;
    }

    public void setTransportPartnerId(String transportPartnerId) {
        this.transportPartnerId = transportPartnerId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getTransportPartnerName() {
        return transportPartnerName;
    }

    public void setTransportPartnerName(String transportPartnerName) {
        this.transportPartnerName = transportPartnerName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
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

    public Float getDebit() {
        return debit;
    }

    public void setDebit(Float debit) {
        this.debit = debit;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TransportPartnerRepresentor getRepresentor() {
        return representor;
    }

    public void setRepresentor(TransportPartnerRepresentor representor) {
        this.representor = representor;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TransportPartner [transportPartnerId=" + transportPartnerId + ", agencyId=" + agencyId
                + ", transportPartnerName=" + transportPartnerName + ", taxCode=" + taxCode + ", province=" + province
                + ", district=" + district + ", town=" + town + ", detailAddress=" + detailAddress + ", debit=" + debit
                + ", phoneNumber=" + phoneNumber + ", email=" + email + ", bin=" + bin + ", bank=" + bank
                + ", contract=" + contract + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", representor="
                + representor + "]";
    }
}