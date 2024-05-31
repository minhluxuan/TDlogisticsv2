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
    private String order_id;

    @Column(name = "user_id", length = 25)
    private String user_id;

    @Column(name = "agency_id", length = 25)
    private String agency_id;

    @Column(name = "service_type", length = 3)
    private String service_type;

    @Column(name = "name_sender", length = 50)
    private String name_sender;

    @Column(name = "phone_number_sender", length = 11)
    private String phone_number_sender;

    @Column(name = "name_receiver", length = 50)
    private String name_receiver;

    @Column(name = "phone_number_receiver", length = 11)
    private String phone_number_receiver;

    @Column(name = "mass")
    private float mass;

    @Column(name = "height")
    private float height;

    @Column(name = "width")
    private float width;

    @Column(name = "length")
    private float length;

    @Column(name = "province_source", length = 30)
    private String province_source;

    @Column(name = "district_source", length = 30)
    private String district_source;

    @Column(name = "ward_source", length = 30)
    private String ward_source;

    @Column(name = "detail_source", length = 50)
    private String detail_source;

    @Column(name = "long_source")
    private double long_source;

    @Column(name = "lat_source")
    private double lat_source;

    @Column(name = "province_dest", length = 30)
    private String province_dest;

    @Column(name = "district_dest", length = 30)
    private String district_dest;

    @Column(name = "ward_dest", length = 30)
    private String ward_dest;

    @Column(name = "detail_dest", length = 50)
    private String detail_dest;

    @Column(name = "long_destination")
    private double long_destination;

    @Column(name = "lat_destination")
    private double lat_destination;

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
    private int status_code;

    @Column(name = "miss")
    private byte miss;

    @Column(name = "send_images", columnDefinition = "LONGTEXT")
    private String send_images;

    @Column(name = "receive_images", columnDefinition = "LONGTEXT")
    private String receive_images;

    @Column(name = "send_signature", length = 256)
    private String send_signature;

    @Column(name = "receive_signature", length = 256)
    private String receive_signature;

    @Column(name = "qrcode", length = 7089)
    private String qrcode;

    @Column(name = "signature", length = 64)
    private String signature;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date last_update;

    @Column(name = "order_code")
    private Long order_code;

    // Constructors, getters, and setters

    public Order() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String orderId) {
        this.order_id = orderId;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getAgencyId() {
        return agency_id;
    }

    public void setAgencyId(String agencyId) {
        this.agency_id = agencyId;
    }

    public String getServiceType() {
        return service_type;
    }

    public void setServiceType(String serviceType) {
        this.service_type = serviceType;
    }

    public String getNameSender() {
        return name_sender;
    }

    public void setNameSender(String nameSender) {
        this.name_sender = nameSender;
    }

    public String getPhoneNumberSender() {
        return phone_number_sender;
    }

    public void setPhoneNumberSender(String phoneNumberSender) {
        this.phone_number_sender = phoneNumberSender;
    }

    public String getNameReceiver() {
        return name_receiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.name_receiver = nameReceiver;
    }

    public String getPhoneNumberReceiver() {
        return phone_number_receiver;
    }

    public void setPhoneNumberReceiver(String phoneNumberReceiver) {
        this.phone_number_receiver = phoneNumberReceiver;
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
        return province_source;
    }

    public void setProvinceSource(String provinceSource) {
        this.province_source = provinceSource;
    }

    public String getDistrictSource() {
        return district_source;
    }

    public void setDistrictSource(String districtSource) {
        this.district_source = districtSource;
    }

    public String getWardSource() {
        return ward_source;
    }

    public void setWardSource(String wardSource) {
        this.ward_source = wardSource;
    }

    public String getDetailSource() {
        return detail_source;
    }

    public void setDetailSource(String detailSource) {
        this.detail_source = detailSource;
    }

    public double getLongSource() {
        return long_source;
    }

    public void setLongSource(double longSource) {
        this.long_source = longSource;
    }

    public double getLatSource() {
        return lat_source;
    }

    public void setLatSource(double latSource) {
        this.lat_source = latSource;
    }

    public String getProvinceDest() {
        return province_dest;
    }

    public void setProvinceDest(String provinceDest) {
        this.province_dest = provinceDest;
    }

    public String getDistrictDest() {
        return district_dest;
    }

    public void setDistrictDest(String districtDest) {
        this.district_dest = districtDest;
    }

    public String getWardDest() {
        return ward_dest;
    }

    public void setWardDest(String wardDest) {
        this.ward_dest = wardDest;
    }

    public String getDetailDest() {
        return detail_dest;
    }

    public void setDetailDest(String detailDest) {
        this.detail_dest = detailDest;
    }

    public double getLongDestination() {
        return long_destination;
    }

    public void setLongDestination(double longDestination) {
        this.long_destination = longDestination;
    }

    public double getLatDestination() {
        return lat_destination;
    }

    public void setLatDestination(double latDestination) {
        this.lat_destination = latDestination;
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
        return status_code;
    }

    public void setStatusCode(int statusCode) {
        this.status_code = statusCode;
    }

    public byte getMiss() {
        return miss;
    }

    public void setMiss(byte miss) {
        this.miss = miss;
    }

    public String getSendImages() {
        return send_images;
    }

    public void setSendImages(String sendImages) {
        this.send_images = sendImages;
    }

    public String getReceiveImages() {
        return receive_images;
    }

    public void setReceiveImages(String receiveImages) {
        this.receive_images = receiveImages;
    }

    public String getSendSignature() {
        return send_signature;
    }

    public void setSendSignature(String sendSignature) {
        this.send_signature = sendSignature;
    }

    public String getReceiveSignature() {
        return receive_signature;
    }

    public void setReceiveSignature(String receiveSignature) {
        this.receive_signature = receiveSignature;
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
        return created_at;
    }

    public void setCreatedAt(Date createdAt) {
        this.created_at = createdAt;
    }

    public Date getLastUpdate() {
        return last_update;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.last_update = lastUpdate;
    }

    public Long getOrderCode() {
        return order_code;
    }

    public void setOrderCode(Long orderCode) {
        this.order_code = orderCode;
    }
}

