package project.tdlogistics.transport_partners.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportPartnerRepresentor {

    private String agencyId;
    private String partnerId;
    private String staffId;
    private String fullname;
    private Date dateOfBirth;
    private String cccd;
    private String province;
    private String district;
    private String town;
    private String detailAddress;
    private String position;
    private String bin;
    private String bank;
    private Boolean active;
    private Account account;

    public TransportPartnerRepresentor() {
    }

    public TransportPartnerRepresentor(String agencyId, String partnerId, String staffId, String fullname,
            Date dateOfBirth, String cccd, String province, String district, String town, String detailAddress,
            String position, String bin, String bank, Boolean active, Account account) {
        this.agencyId = agencyId;
        this.partnerId = partnerId;
        this.staffId = staffId;
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.cccd = cccd;
        this.province = province;
        this.district = district;
        this.town = town;
        this.detailAddress = detailAddress;
        this.position = position;
        this.bin = bin;
        this.bank = bank;
        this.active = active;
        this.account = account;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "TransportPartnerRepresentor [agencyId=" + agencyId + ", transportPartnerId=" + partnerId
                + ", staffId=" + staffId + ", fullname=" + fullname + ", dateOfBirth=" + dateOfBirth + ", cccd=" + cccd
                + ", province=" + province + ", district=" + district + ", town=" + town + ", detailAddress="
                + detailAddress + ", position=" + position + ", bin=" + bin + ", bank=" + bank + ", active=" + active
                + ", account=" + account + "]";
    }
}
