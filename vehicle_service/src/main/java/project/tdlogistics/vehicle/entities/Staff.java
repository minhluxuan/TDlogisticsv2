package project.tdlogistics.vehicle.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    private Account account;

    private String agencyId;

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

    private Integer deposit;

    private Integer salary;

    private Integer paidSalary;

    private List<String> managedWards;

    private String avatar;

    private Boolean active;

    private LocalDateTime dateCreated;

    private LocalDateTime dateModified;

    public Staff() {
    }

    public Staff(Long id, String accountId, Account account, String agencyId, String staffId, String fullname, Date dateOfBirth, String cccd,
            String province, String district, String town, String detailAddress, String position, String bin,
            String bank, Integer deposit, Integer salary, Integer paidSalary, List<String> managedWards, String avatar,
            Boolean active, LocalDateTime dateCreated, LocalDateTime dateModified) {
        this.account = account;
        this.agencyId = agencyId;
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
        this.deposit = deposit;
        this.salary = salary;
        this.paidSalary = paidSalary;
        this.managedWards = managedWards;
        this.avatar = avatar;
        this.active = active;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public Account getAccount() {
        if (account == null) {
            account = new Account();
        }
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getPaidSalary() {
        return paidSalary;
    }

    public void setPaidSalary(Integer paidSalary) {
        this.paidSalary = paidSalary;
    }

    public List<String> getManagedWards() {
        return managedWards;
    }

    public void setManagedWards(List<String> managedWards) {
        this.managedWards = managedWards;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    @PrePersist
    private void onCreate() {
        this.dateCreated = this.dateModified = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.dateModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Staff [account=" + account + ", agencyId=" + agencyId + ", staffId=" + staffId + ", fullname="
                + fullname + ", dateOfBirth=" + dateOfBirth + ", cccd=" + cccd + ", province=" + province
                + ", district=" + district + ", town=" + town + ", detailAddress=" + detailAddress + ", position="
                + position + ", bin=" + bin + ", bank=" + bank + ", deposit=" + deposit + ", salary=" + salary
                + ", paidSalary=" + paidSalary + ", managedWards=" + managedWards + ", avatar=" + avatar + ", active="
                + active + ", dateCreated=" + dateCreated + ", dateModified=" + dateModified + "]";
    }
}
