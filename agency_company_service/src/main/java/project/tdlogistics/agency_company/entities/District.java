package project.tdlogistics.agency_company.entities;

// import org.hibernate.mapping.List;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import project.tdlogistics.agency_company.configurations.ListToStringConverter;

@Entity
@Table(name = "district")
public class District {

    @Id
    @Column(name = "district_id", length = 4, nullable = false)
    private String districtId;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "level", length = 30)
    private String level;

    @Column(name = "province_id", length = 3)
    private String provinceId;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "agency_ids", columnDefinition = "LONGTEXT")
    @Convert(converter = ListToStringConverter.class)
    private List<String> agencyIds;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    // Getters and Setters

    public District(String districtId, String district, String level, String provinceId, String province,
            List<String> agencyIds, String postalCode) {
        this.districtId = districtId;
        this.district = district;
        this.level = level;
        this.provinceId = provinceId;
        this.province = province;
        this.agencyIds = agencyIds;
        this.postalCode = postalCode;
    }

    public District() {
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public List<String> getAgencyIds() {
        return agencyIds;
    }

    public void setAgencyIds(List<String> agencyIds) {
        this.agencyIds = agencyIds;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}