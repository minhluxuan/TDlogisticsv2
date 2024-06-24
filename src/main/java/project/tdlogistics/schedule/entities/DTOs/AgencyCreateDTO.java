
package project.tdlogistics.schedule.entities.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public class AgencyCreateDTO {

    private Byte level;
    private String agency_id;
    private String agency_name;
    private String email;
    private String phone_number;
    private String postal_code;
    private String province;
    private String district;
    private String town;
    private String detail_address;
    private Float latitude;
    private Float longitude;
    private String bin;
    private String bank;
    private Float commission_rate;
    private Float revenue;
    private String contract;
    private List<String> managed_wards;
    private LocalDateTime created_at;
    private LocalDateTime last_update;
    private Boolean individual_company;
    private String company_name;
    private String tax_number;
    private List<String> license;
    private String user_cccd;
    private String type;

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getAgency_name() {
        return agency_name;
    }

    public void setAgency_name(String agency_name) {
        this.agency_name = agency_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
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

    public String getDetail_address() {
        return detail_address;
    }

    public void setDetail_address(String detail_address) {
        this.detail_address = detail_address;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
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

    public Float getCommission_rate() {
        return commission_rate;
    }

    public void setCommission_rate(Float commission_rate) {
        this.commission_rate = commission_rate;
    }

    public Float getRevenue() {
        return revenue;
    }

    public void setRevenue(Float revenue) {
        this.revenue = revenue;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public List<String> getManaged_wards() {
        return managed_wards;
    }

    public void setManaged_wards(List<String> managed_wards) {
        this.managed_wards = managed_wards;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getLast_update() {
        return last_update;
    }

    public void setLast_update(LocalDateTime last_update) {
        this.last_update = last_update;
    }

    public Boolean getIndividual_company() {
        return individual_company;
    }

    public void setIndividual_company(Boolean individual_company) {
        this.individual_company = individual_company;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getTax_number() {
        return tax_number;
    }

    public void setTax_number(String tax_number) {
        this.tax_number = tax_number;
    }

    public List<String> getLicense() {
        return license;
    }

    public void setLicense(List<String> license) {
        this.license = license;
    }

    public String getUser_cccd() {
        return user_cccd;
    }

    public void setUser_cccd(String user_cccd) {
        this.user_cccd = user_cccd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // getters and setters...
};
