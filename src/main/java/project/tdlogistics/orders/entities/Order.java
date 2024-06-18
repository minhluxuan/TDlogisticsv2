package project.tdlogistics.orders.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import project.tdlogistics.orders.configurations.ListToStringConverter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @Id
    @Column(name = "order_id", nullable = false, length = 30)
    private String orderId;

    @Column(name = "user_id", length = 25)
    private String userId;

    @Column(name = "agency_id", length = 25)
    private String agencyId;

    @Column(name = "service_type", length = 3)
    private String serviceType;

    @Column(name = "name_sender", length = 50)
    private String nameSender;

    @Column(name = "phone_number_sender", length = 11)
    private String phoneNumberSender;

    @Column(name = "name_receiver", length = 50)
    private String nameReceiver;

    @Column(name = "phone_number_receiver", length = 11)
    private String phoneNumberReceiver;

    @Column(name = "mass")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float mass;

    @Column(name = "height")
    private Float height;

    @Column(name = "width")
    private Float width;

    @Column(name = "length")
    private Float length;

    @Column(name = "province_source", length = 30)
    private String provinceSource;

    @Column(name = "district_source", length = 30)
    private String districtSource;

    @Column(name = "ward_source", length = 30)
    private String wardSource;

    @Column(name = "detail_source", length = 50)
    private String detailSource;

    @Column(name = "long_source")
    private Double longSource;

    @Column(name = "lat_source")
    private Double latSource;

    @Column(name = "province_dest", length = 30)
    private String provinceDest;

    @Column(name = "district_dest", length = 30)
    private String districtDest;

    @Column(name = "ward_dest", length = 30)
    private String wardDest;

    @Column(name = "detail_dest", length = 50)
    private String detailDest;

    @Column(name = "long_destination")
    private Double longDestination;

    @Column(name = "lat_destination")
    private Double latDestination;

    @Column(name = "fee")
    private Float fee;

    @Column(name = "parent", length = 25)
    private String parent;

    @Column(name = "journey")
    @Convert(converter = ListToStringConverter.class)
    private List<String> journey;

    @Column(name = "COD")
    private Float cod;

    @Column(name = "shipper", length = 25)
    private String shipper;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "miss")
    private byte miss;

    @Column(name = "send_images", columnDefinition = "LONGTEXT")
    private String sendImages;

    @Column(name = "receive_images", columnDefinition = "LONGTEXT")
    private String receiveImages;

    @Column(name = "send_signature", length = 256)
    private String sendSignature;

    @Column(name = "receive_signature", length = 256)
    private String receiveSignature;

    @Column(name = "qrcode", length = 7089)
    private String qrcode;

    @Column(name = "signature", length = 64)
    private String signature;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Column(name = "order_code")
    private Long orderCode;

    // Constructors, getters, and setters

    public Order() {}

    

    public Order(String orderId, String userId, String agencyId, String serviceType, String nameSender,
            String phoneNumberSender, String nameReceiver, String phoneNumberReceiver, Float mass, Float height,
            Float width, Float length, String provinceSource, String districtSource, String wardSource,
            String detailSource, Double longSource, Double latSource, String provinceDest, String districtDest,
            String wardDest, String detailDest, Double longDestination, Double latDestination, Float fee, String parent,
            List<String> journey, Float cod, String shipper, Integer statusCode, byte miss, String sendImages,
            String receiveImages, String sendSignature, String receiveSignature, String qrcode, String signature,
            Boolean paid, Date createdAt, Date lastUpdate, Long orderCode) {
        this.orderId = orderId;
        this.userId = userId;
        this.agencyId = agencyId;
        this.serviceType = serviceType;
        this.nameSender = nameSender;
        this.phoneNumberSender = phoneNumberSender;
        this.nameReceiver = nameReceiver;
        this.phoneNumberReceiver = phoneNumberReceiver;
        this.mass = mass;
        this.height = height;
        this.width = width;
        this.length = length;
        this.provinceSource = provinceSource;
        this.districtSource = districtSource;
        this.wardSource = wardSource;
        this.detailSource = detailSource;
        this.longSource = longSource;
        this.latSource = latSource;
        this.provinceDest = provinceDest;
        this.districtDest = districtDest;
        this.wardDest = wardDest;
        this.detailDest = detailDest;
        this.longDestination = longDestination;
        this.latDestination = latDestination;
        this.fee = fee;
        this.parent = parent;
        this.journey = journey;
        this.cod = cod;
        this.shipper = shipper;
        this.statusCode = statusCode;
        this.miss = miss;
        this.sendImages = sendImages;
        this.receiveImages = receiveImages;
        this.sendSignature = sendSignature;
        this.receiveSignature = receiveSignature;
        this.qrcode = qrcode;
        this.signature = signature;
        this.paid = paid;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.orderCode = orderCode;
    }



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public String getPhoneNumberSender() {
        return phoneNumberSender;
    }

    public void setPhoneNumberSender(String phoneNumberSender) {
        this.phoneNumberSender = phoneNumberSender;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
    }

    public String getPhoneNumberReceiver() {
        return phoneNumberReceiver;
    }

    public void setPhoneNumberReceiver(String phoneNumberReceiver) {
        this.phoneNumberReceiver = phoneNumberReceiver;
    }

    public Float getMass() {
        return mass;
    }

    public void setMass(Float mass) {
        this.mass = mass;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public String getProvinceSource() {
        return provinceSource;
    }

    public void setProvinceSource(String provinceSource) {
        this.provinceSource = provinceSource;
    }

    public String getDistrictSource() {
        return districtSource;
    }

    public void setDistrictSource(String districtSource) {
        this.districtSource = districtSource;
    }

    public String getWardSource() {
        return wardSource;
    }

    public void setWardSource(String wardSource) {
        this.wardSource = wardSource;
    }

    public String getDetailSource() {
        return detailSource;
    }

    public void setDetailSource(String detailSource) {
        this.detailSource = detailSource;
    }

    public Double getLongSource() {
        return longSource;
    }

    public void setLongSource(Double longSource) {
        this.longSource = longSource;
    }

    public Double getLatSource() {
        return latSource;
    }

    public void setLatSource(Double latSource) {
        this.latSource = latSource;
    }

    public String getProvinceDest() {
        return provinceDest;
    }

    public void setProvinceDest(String provinceDest) {
        this.provinceDest = provinceDest;
    }

    public String getDistrictDest() {
        return districtDest;
    }

    public void setDistrictDest(String districtDest) {
        this.districtDest = districtDest;
    }

    public String getWardDest() {
        return wardDest;
    }

    public void setWardDest(String wardDest) {
        this.wardDest = wardDest;
    }

    public String getDetailDest() {
        return detailDest;
    }

    public void setDetailDest(String detailDest) {
        this.detailDest = detailDest;
    }

    public Double getLongDestination() {
        return longDestination;
    }

    public void setLongDestination(Double longDestination) {
        this.longDestination = longDestination;
    }

    public Double getLatDestination() {
        return latDestination;
    }

    public void setLatDestination(Double latDestination) {
        this.latDestination = latDestination;
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<String> getJourney() {
        return journey;
    }

    public void setJourney(List<String> journey) {
        this.journey = journey;
    }

    public Float getCod() {
        return cod;
    }

    public void setCod(Float cod) {
        this.cod = cod;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public byte getMiss() {
        return miss;
    }

    public void setMiss(byte miss) {
        this.miss = miss;
    }

    public String getSendImages() {
        return sendImages;
    }

    public void setSendImages(String sendImages) {
        this.sendImages = sendImages;
    }

    public String getReceiveImages() {
        return receiveImages;
    }

    public void setReceiveImages(String receiveImages) {
        this.receiveImages = receiveImages;
    }

    public String getSendSignature() {
        return sendSignature;
    }

    public void setSendSignature(String sendSignature) {
        this.sendSignature = sendSignature;
    }

    public String getReceiveSignature() {
        return receiveSignature;
    }

    public void setReceiveSignature(String receiveSignature) {
        this.receiveSignature = receiveSignature;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean isPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }



    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", userId=" + userId + ", agencyId=" + agencyId
                + ", serviceType=" + serviceType + ", nameSender=" + nameSender + ", phoneNumberSender="
                + phoneNumberSender + ", nameReceiver=" + nameReceiver + ", phoneNumberReceiver=" + phoneNumberReceiver
                + ", mass=" + mass + ", height=" + height + ", width=" + width + ", length=" + length
                + ", provinceSource=" + provinceSource + ", districtSource=" + districtSource + ", wardSource="
                + wardSource + ", detailSource=" + detailSource + ", longSource=" + longSource + ", latSource="
                + latSource + ", provinceDest=" + provinceDest + ", districtDest=" + districtDest + ", wardDest="
                + wardDest + ", detailDest=" + detailDest + ", longDestination=" + longDestination + ", latDestination="
                + latDestination + ", fee=" + fee + ", parent=" + parent + ", journey=" + journey + ", cod=" + cod
                + ", shipper=" + shipper + ", statusCode=" + statusCode + ", miss=" + miss + ", sendImages="
                + sendImages + ", receiveImages=" + receiveImages + ", sendSignature=" + sendSignature
                + ", receiveSignature=" + receiveSignature + ", qrcode=" + qrcode + ", signature=" + signature
                + ", paid=" + paid + ", createdAt=" + createdAt + ", lastUpdate=" + lastUpdate + ", orderCode="
                + orderCode + "]";
    }

    
}

