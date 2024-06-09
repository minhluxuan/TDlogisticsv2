package project.tdlogistics.administrative.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ward")
public class Ward {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ward_id")
    private Integer wardId;

    @Column(name = "ward", length = 30)
    private String ward;

    @Column(name = "level", length = 10)
    private String level;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district", length = 30)
    private String district;

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "province", length = 30)
    private String province;

    @Column(name = "agency_id", length = 30)
    private String agencyId;

    @Column(name = "postal_code", length = 5)
    private String postalCode;

    @Column(name = "shipper", length = 25)
    private String shipper;

    // Getters and Setters

    public Ward(Integer id, Integer wardId, String ward, String level, Integer districtId, String district, Integer provinceId, String province, String agencyId, String postalCode, String shipper) {
        this.id = id;
        this.wardId = wardId;
        this.ward = ward;
        this.level = level;
        this.districtId = districtId;
        this.district = district;
        this.provinceId = provinceId;
        this.province = province;
        this.agencyId = agencyId;
        this.postalCode = postalCode;
        this.shipper = shipper;
    }

    public Ward(String province, String district, String ward) {
        this.province = province;
        this.district = district;
        this.ward = ward;
    }

    public Ward(String ward, String district, String province, String postalCode, String agencyId, String shipper) {
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.agencyId = agencyId;
        this.postalCode = postalCode;
        this.shipper = shipper;
    }

    public Ward() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWardId() {
        return wardId;
    }

    public void setWardId(Integer wardId) {
        this.wardId = wardId;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    @Override
    public String toString() {
        return "Ward [id=" + id + ", wardId=" + wardId + ", ward=" + ward + ", level=" + level + ", districtId="
                + districtId + ", district=" + district + ", provinceId=" + provinceId + ", province=" + province
                + ", agencyId=" + agencyId + ", postalCode=" + postalCode + ", shipper=" + shipper + "]";
    }
}