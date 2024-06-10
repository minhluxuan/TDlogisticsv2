package project.tdlogistics.orders.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Agency {
    private String province;
    private String district;
    private String ward;
    private String agencyName;
    private String postalCode;
    private String agencyId;
    private String shipper;


    

    public Agency() {

    }
    
    public Agency(String postalCode, String agencyId, String shipper) {
        this.postalCode = postalCode;
        this.agencyId = agencyId;
        this.shipper = shipper;
    }
    
    public Agency(String province, String district, String ward, String agencyName, String postalCode, String agencyId,
            String shipper) {
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.agencyName = agencyName;
        this.postalCode = postalCode;
        this.agencyId = agencyId;
        this.shipper = shipper;
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
    public String getAgencyName() {
        return agencyName;
    }
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
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

    @Override
    public String toString() {
        return "Agency [province=" + province + ", district=" + district + ", ward=" + ward + ", agencyName="
                + agencyName + ", postalCode=" + postalCode + ", agencyId=" + agencyId + ", shipper=" + shipper + "]";
    }


}
