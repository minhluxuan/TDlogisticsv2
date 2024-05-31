package project.tdlogistics.orders.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(name = "order_id", length = 30)
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
    private float mass;

    @Column(name = "height")
    private float height;

    @Column(name = "width")
    private float width;

    @Column(name = "length")
    private float length;

    @Column(name = "province_source", length = 30)
    private String provinceSource;

    @Column(name = "district_source", length = 30)
    private String districtSource;

    @Column(name = "ward_source", length = 30)
    private String wardSource;

    @Column(name = "detail_source", length = 50)
    private String detailSource;

    @Column(name = "long_source")
    private double longSource;

    @Column(name = "lat_source")
    private double latSource;

    @Column(name = "province_dest", length = 30)
    private String provinceDest;

    @Column(name = "district_dest", length = 30)
    private String districtDest;

    @Column(name = "ward_dest", length = 30)
    private String wardDest;

    @Column(name = "detail_dest", length = 50)
    private String detailDest;

    @Column(name = "long_destination")
    private double longDestination;

    @Column(name = "lat_destination")
    private double latDestination;

    @Column(name = "fee")
    private float fee;

    @Column(name = "parent", length = 25)
    private String parent;

    @Column(name = "journey", columnDefinition = "LONGTEXT")
    private String journey;

    @Column(name = "COD")
    private float cod;

    @Column(name = "shipper", length = 25)
    private String shipper;

    @Column(name = "status_code")
    private int statusCode;

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
    private boolean paid;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
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

    public double getLongSource() {
        return longSource;
    }

    public void setLongSource(double longSource) {
        this.longSource = longSource;
    }

    public double getLatSource() {
        return latSource;
    }

    public void setLatSource(double latSource) {
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

    public double getLongDestination() {
        return longDestination;
    }

    public void setLongDestination(double longDestination) {
        this.longDestination = longDestination;
    }

    public double getLatDestination() {
        return latDestination;
    }

    public void setLatDestination(double latDestination) {
        this.latDestination = latDestination;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getJourney() {
        return journey;
    }

    public void setJourney(String journey) {
        this.journey = journey;
    }

    public float getCod() {
        return cod;
    }

    public void setCod(float cod) {
        this.cod = cod;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
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

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
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
}

