package project.tdlogistics.administrative.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitRequest {
    private String province;
    private String district;
    private String ward;
    private String postalCode;
    private String agencyId;
    private String shipper;

    public UnitRequest() {
    }

    public UnitRequest(String province, String district, String ward) {
        this.province = province;
        this.district = district;
        this.ward = ward;
    }

    public UnitRequest(String province, String district, String ward, String postalCode, String agencyId) {
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.postalCode = postalCode;
        this.agencyId = agencyId;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }
}
