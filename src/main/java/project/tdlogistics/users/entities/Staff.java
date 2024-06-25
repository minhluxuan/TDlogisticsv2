package project.tdlogistics.users.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import project.tdlogistics.users.validations.NullOrEmpty;
import project.tdlogistics.users.validations.staffs.*;

@Entity
@Table(name = "staff")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    @OneToOne
    @JoinColumn(name = "account")
    @NotNull(message = "Tài khoản không được để trống", groups = {CreateByAdmin.class, CreateByAgency.class})
    @NullOrEmpty(message = "Tài khoản không được cho phép", groups = {SearchByAdmin.class, SearchByAgency.class, Update.class})
    private Account account;

    @Column(name = "agency_id")
    @NotNull(message = "Mã bưu cục/đại lý không được để trống", groups = {CreateByAdmin.class})
    @NotBlank(message = "Mã bưu cục/đại lý không được để trống", groups = {CreateByAdmin.class})
    @Pattern(message = "Mã bưu cục/đại lý không đúng định dạng", groups = {CreateByAdmin.class}, regexp = "(TD|BC|DL)_\\d{5}_\\d{12}")
    @NullOrEmpty(message = "Mã bưu cục/đại lý không được cho phép", groups = {CreateByAgency.class, SearchByAgency.class, Update.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String agencyId;

    @Id
    @Column(name = "staff_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NullOrEmpty(message = "Mã nhân viên không được cho phép", groups = {CreateByAdmin.class, CreateByAgency.class, Update.class})
    private String staffId;

    @Column(name = "fullname")
    @NotNull(message = "Họ và tên không được để null", groups = {CreateByAdmin.class, CreateByAgency.class})
    @NotBlank(message = "Họ và tên không được để trống", groups = {CreateByAdmin.class, CreateByAgency.class})
    @Pattern(message = "Họ và tên không đúng định dạng",
        groups = {CreateByAdmin.class, CreateByAgency.class, SearchByAdmin.class, SearchByAgency.class, Update.class},
        regexp = "([a-vxyỳọáầảấờễàạằệếýộậốũứĩõúữịỗìềểẩớặòùồợãụủíỹắẫựỉỏừỷởóéửỵẳẹèẽổẵẻỡơôưăêâđA-VXYỲỌÁẦẢẤỜỄÀẠẰỆẾÝỘẬỐŨỨĨÕÚỮỊỖÌỀỂẨỚẶÒÙỒỢÃỤỦÍỸẮẪỰỈỎỪỶỞÓÉỬỴẲẸÈẼỔẴẺỠƠÔƯĂÊÂĐ]+)((\\s{1}[a-vxyỳọáầảấờễàạằệếýộậốũứĩõúữịỗìềểẩớặòùồợãụủíỹắẫựỉỏừỷởóéửỵẳẹèẽổẵẻỡơôưăêâđA-VXYỲỌÁẦẢẤỜỄÀẠẰỆẾÝỘẬỐŨỨĨÕÚỮỊỖÌỀỂẨỚẶÒÙỒỢÃỤỦÍỸẮẪỰỈỎỪỶỞÓÉỬỴẲẸÈẼỔẴẺỠƠÔƯĂÊÂĐ]+){1,})"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullname;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date dateOfBirth;

    @Column(name = "cccd")
    @NotNull(message = "Căn cước công dân không được để trống", groups = {CreateByAdmin.class, CreateByAgency.class})
    @NotBlank(message = "Căn cước công dân không được để trống", groups = {CreateByAdmin.class, CreateByAgency.class})
    @Pattern(message = "Căn cước công dân không đúng định dạng", groups = {CreateByAdmin.class, CreateByAgency.class, SearchByAdmin.class, SearchByAgency.class, Update.class}, regexp = "[0-9]{12}")
    @NullOrEmpty(message = "Căn cước công dân không được cho phép", groups = {Update.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cccd;

    @Column(name = "province")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String province;

    @Column(name = "district")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String district;

    @Column(name = "town")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String town;

    @Column(name = "detail_address")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String detailAddress;

    @Column(name = "position")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String position;

    @Column(name = "bin")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bin;

    @Column(name = "bank")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bank;

    @Column(name = "deposit", columnDefinition = "INT UNSIGNED")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer deposit;

    @Column(name = "salary", columnDefinition = "INT UNSIGNED")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer salary;

    @Column(name = "paid_salary", columnDefinition = "INT UNSIGNED")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer paidSalary;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> managedWards;

    @Column(name = "avatar")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String avatar;

    @Column(name = "active", columnDefinition = "TINYINT UNSIGNED")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NullOrEmpty(message = "Trạng thái tài khoản không được cho phép", groups = {CreateByAdmin.class, CreateByAgency.class, SearchByAdmin.class, SearchByAgency.class, Update.class})
    private Boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NullOrEmpty(message = "Thời gian tạo không được cho phép", groups = {CreateByAdmin.class, CreateByAgency.class, SearchByAdmin.class, SearchByAgency.class, Update.class})
    private LocalDateTime dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NullOrEmpty(message = "Thời gian cập nhật không được cho phép", groups = {CreateByAdmin.class, CreateByAgency.class, SearchByAdmin.class, SearchByAgency.class, Update.class})
    private LocalDateTime dateModified;

    public Staff() {
    }

    public Staff(Long id,
            String accountId,
            Account account,
            String agencyId,
            String staffId,
            String fullname,
            Date dateOfBirth,
            String cccd,
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
