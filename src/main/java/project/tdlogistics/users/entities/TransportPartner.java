package project.tdlogistics.users.entities;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TransportPartner {
    private String transportPartnerId;
    private String agencyId;
    private String transportPartnerName;
    private String taxCode;
    private String province;
    private String district;
    private String town;
    private String detailAddress;
    private Float debit;
    private String phoneNumber;
    private String email;
    private String bin;
    private String bank;
    private String contract;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
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