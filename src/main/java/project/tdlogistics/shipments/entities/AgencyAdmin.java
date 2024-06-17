package project.tdlogistics.shipments.entities;

import java.time.LocalDateTime;
import java.util.Date;

public class AgencyAdmin {
    private String staffId;
    private String agencyId;
    private String fullname;
    private String phoneNumber;
    private String email;
    private Date dateOfBirth;
    private String cccd;
    private String province;
    private String district;
    private String town;
    private String detailAddress;
    private String position;
    private String bin;
    private String bank;
    private Float deposit;
    private Float salary;
    private Float paidSalary;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private Account account;
    
    public AgencyAdmin() {
    }

    public AgencyAdmin(String cccd) {
        this.cccd = cccd;
    }

    public AgencyAdmin(String staffId, String agencyId, String fullname, String phoneNumber, String email,
            Date dateOfBirth, String cccd, String province, String district, String town, String detailAddress,
            String position, String bin, String bank, Float deposit, Float salary, Float paidSalary,
            LocalDateTime dateCreated, LocalDateTime dateModified, Account account) {
        this.staffId = staffId;
        this.agencyId = agencyId;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.email = email;
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
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
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

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public Float getDeposit() {
        return deposit;
    }

    public void setDeposit(Float deposit) {
        this.deposit = deposit;
    }

    public Float getPaidSalary() {
        return paidSalary;
    }

    public void setPaidSalary(Float paidSalary) {
        this.paidSalary = paidSalary;
    }

    @Override
    public String toString() {
        return "AgencyAdmin [staffId=" + staffId + ", agencyId=" + agencyId + ", fullname=" + fullname
                + ", phoneNumber=" + phoneNumber + ", email=" + email + ", dateOfBirth=" + dateOfBirth + ", cccd="
                + cccd + ", province=" + province + ", district=" + district + ", town=" + town + ", detailAddress="
                + detailAddress + ", position=" + position + ", bin=" + bin + ", bank=" + bank + ", deposit=" + deposit
                + ", salary=" + salary + ", paidSalary=" + paidSalary + ", dateCreated=" + dateCreated
                + ", dateModified=" + dateModified + ", account=" + account + "]";
    }

    
}
