package project.tdlogistics.administrative.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "province")
public class Province {

    @Id
    @Column(name = "province_id", length = 3, nullable = false)
    private String provinceId;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "acronym", length = 3)
    private String acronym;

    @Column(name = "level", length = 30)
    private String level;

    @Column(name = "area")
    private Byte area;

    @Column(name = "region")
    private Byte region;

    @Column(name = "agency_ids", columnDefinition = "LONGTEXT")
    private String agencyIds;

    @Column(name = "postal_code", length = 5)
    private String postalCode;

    @Column(name = "managed_by", length = 255)
    private String managedBy;

    // Getters and Setters

    public Province(String provinceId, String province, String acronym, String level, Byte area, Byte region, String agencyIds, String postalCode, String managedBy) {
        this.provinceId = provinceId;
        this.province = province;
        this.acronym = acronym;
        this.level = level;
        this.area = area;
        this.region = region;
        this.agencyIds = agencyIds;
        this.postalCode = postalCode;
        this.managedBy = managedBy;
    }

    public Province() {
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Byte getArea() {
        return area;
    }

    public void setArea(Byte area) {
        this.area = area;
    }

    public Byte getRegion() {
        return region;
    }

    public void setRegion(Byte region) {
        this.region = region;
    }

    public String getAgencyIds() {
        return agencyIds;
    }

    public void setAgencyIds(String agencyIds) {
        this.agencyIds = agencyIds;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }
}