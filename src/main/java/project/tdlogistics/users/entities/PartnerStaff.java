package project.tdlogistics.users.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import project.tdlogistics.users.configurations.ListToStringConverter;

@Entity
@Table(name = "partner_staff")
public class PartnerStaff {
    @Id
    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "fullname")
    private String fullname;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "cccd")
    private String cccd;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "town")
    private String town;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "position")
    private String position;

    @Column(name = "bin")
    private String bin;

    @Column(name = "bank")
    private String bank;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "image_license")
    @Convert(converter = ListToStringConverter.class)
    private List<String> imageLicense;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_update")
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private Boolean active;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    public PartnerStaff(){
    }

    public PartnerStaff(String staffId, String partnerId, String agencyId, String fullname, Date dateOfBirth,
            String cccd, String province, String district, String town, String detailAddress, String position,
            String bin, String bank, String avatar, List<String> imageLicense, LocalDateTime createdAt,
            LocalDateTime updatedAt, Boolean active, Account account) {
        this.staffId = staffId;
        this.partnerId = partnerId;
        this.agencyId = agencyId;
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
        this.avatar = avatar;
        this.imageLicense = imageLicense;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
        this.account = account;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getImageLicense() {
        return imageLicense;
    }

    public void setImageLicense(List<String> imageLicense) {
        this.imageLicense = imageLicense;
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
        return "PartnerStaff [staffId=" + staffId + ", partnerId=" + partnerId + ", agencyId=" + agencyId
                + ", fullname=" + fullname + ", dateOfBirth=" + dateOfBirth + ", cccd=" + cccd + ", province="
                + province + ", district=" + district + ", town=" + town + ", detailAddress=" + detailAddress
                + ", position=" + position + ", bin=" + bin + ", bank=" + bank + ", avatar=" + avatar
                + ", imageLicense=" + imageLicense + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + ", active=" + active + ", account=" + account + "]";
    }
}
