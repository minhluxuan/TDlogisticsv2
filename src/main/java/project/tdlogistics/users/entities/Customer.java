package project.tdlogistics.users.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import project.tdlogistics.users.validations.NullOrEmpty;
import project.tdlogistics.users.validations.account.Delete;
import project.tdlogistics.users.validations.customer.Create;
import project.tdlogistics.users.validations.customer.Search;
import project.tdlogistics.users.validations.customer.Update;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "fullname")
    @NotBlank(
        message = "Họ và tên không được để trống",
        groups = {Create.class}
    )
    @NotBlank(
        message = "Họ và tên không được để trống",
        groups = {Create.class}
    )
    @NullOrEmpty(
        message = "Họ và tên không được cho phép",
        groups = {Delete.class}
    )
    @Pattern(
        message = "Họ và tên không đúng định dạng",
        regexp = "([a-vxyỳọáầảấờễàạằệếýộậốũứĩõúữịỗìềểẩớặòùồợãụủíỹắẫựỉỏừỷởóéửỵẳẹèẽổẵẻỡơôưăêâđA-VXYỲỌÁẦẢẤỜỄÀẠẰỆẾÝỘẬỐŨỨĨÕÚỮỊỖÌỀỂẨỚẶÒÙỒỢÃỤỦÍỸẮẪỰỈỎỪỶỞÓÉỬỴẲẸÈẼỔẴẺỠƠÔƯĂÊÂĐ]+)((\\s{1}[a-vxyỳọáầảấờễàạằệếýộậốũứĩõúữịỗìềểẩớặòùồợãụủíỹắẫựỉỏừỷởóéửỵẳẹèẽổẵẻỡơôưăêâđA-VXYỲỌÁẦẢẤỜỄÀẠẰỆẾÝỘẬỐŨỨĨÕÚỮỊỖÌỀỂẨỚẶÒÙỒỢÃỤỦÍỸẮẪỰỈỎỪỶỞÓÉỬỴẲẸÈẼỔẴẺỠƠÔƯĂÊÂĐ]+){1,})",
        groups = {Create.class, Update.class}
    )
    private String fullname;

    @NullOrEmpty(
        message = "Họ và tên không được cho phép",
        groups = {Delete.class}
    )
    @Column(name = "province")
    private String province;

    @NullOrEmpty(
        message = "Họ và tên không được cho phép",
        groups = {Delete.class}
    )
    @Column(name = "district")
    private String district;

    @NullOrEmpty(
        message = "Họ và tên không được cho phép",
        groups = {Delete.class}
    )
    @Column(name = "ward")
    private String ward;

    @NullOrEmpty(
        message = "Họ và tên không được cho phép",
        groups = {Delete.class}
    )
    @Column(name = "detail_address")
    private String detailAddress;

    @NullOrEmpty(
        message = "Họ và tên không được cho phép",
        groups = {Delete.class}
    )
    @Column(name = "avatar")
    private String avatar;

    @NotNull(
        message = "Tài khoản không được để trống",
        groups = {Create.class}
    )
    @NullOrEmpty(
        message = "Tài khoản không được cho phép",
        groups = {Search.class, Update.class, Delete.class}
    )
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    public Customer() {
    }

    public Customer(String id,
            @Pattern(regexp = "([a-vxyỳọáầảấờễàạằệếýộậốũứĩõúữịỗìềểẩớặòùồợãụủíỹắẫựỉỏừỷởóéửỵẳẹèẽổẵẻỡơôưăêâđA-VXYỲỌÁẦẢẤỜỄÀẠẰỆẾÝỘẬỐŨỨĨÕÚỮỊỖÌỀỂẨỚẶÒÙỒỢÃỤỦÍỸẮẪỰỈỎỪỶỞÓÉỬỴẲẸÈẼỔẴẺỠƠÔƯĂÊÂĐ]+)((\\s{1}[a-vxyỳọáầảấờễàạằệếýộậốũứĩõúữịỗìềểẩớặòùồợãụủíỹắẫựỉỏừỷởóéửỵẳẹèẽổẵẻỡơôưăêâđA-VXYỲỌÁẦẢẤỜỄÀẠẰỆẾÝỘẬỐŨỨĨÕÚỮỊỖÌỀỂẨỚẶÒÙỒỢÃỤỦÍỸẮẪỰỈỎỪỶỞÓÉỬỴẲẸÈẼỔẴẺỠƠÔƯĂÊÂĐ]+){1,})") String fullname,
            String province, String district, String ward, String detailAddress, String avatar,
            @NotNull(message = "Tài khoản không được để trống") Account account) {
        this.id = id;
        this.fullname = fullname;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
        this.avatar = avatar;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", fullname=" + fullname + ", province=" + province + ", district=" + district
                + ", ward=" + ward + ", detailAddress=" + detailAddress + "]";
    }
}
