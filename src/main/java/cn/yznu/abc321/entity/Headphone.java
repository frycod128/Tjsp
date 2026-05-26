package cn.yznu.abc321.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 头戴式耳机实体类
 */
public class Headphone {
    private Integer id;
    private String model;
    private String brand;
    private Double driverSize;
    private Integer impedance;
    private Integer sensitivity;
    private String frequencyResponse;
    private BigDecimal price;
    private Integer stock;
    private Integer wireless;
    private Integer noiseCancelling;
    private Date createTime;

    // 无参构造
    public Headphone() {}

    // 全参构造
    public Headphone(Integer id, String model, String brand, Double driverSize,
                     Integer impedance, Integer sensitivity, String frequencyResponse,
                     BigDecimal price, Integer stock, Integer wireless,
                     Integer noiseCancelling, Date createTime) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.driverSize = driverSize;
        this.impedance = impedance;
        this.sensitivity = sensitivity;
        this.frequencyResponse = frequencyResponse;
        this.price = price;
        this.stock = stock;
        this.wireless = wireless;
        this.noiseCancelling = noiseCancelling;
        this.createTime = createTime;
    }

    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Double getDriverSize() { return driverSize; }
    public void setDriverSize(Double driverSize) { this.driverSize = driverSize; }

    public Integer getImpedance() { return impedance; }
    public void setImpedance(Integer impedance) { this.impedance = impedance; }

    public Integer getSensitivity() { return sensitivity; }
    public void setSensitivity(Integer sensitivity) { this.sensitivity = sensitivity; }

    public String getFrequencyResponse() { return frequencyResponse; }
    public void setFrequencyResponse(String frequencyResponse) { this.frequencyResponse = frequencyResponse; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getWireless() { return wireless; }
    public void setWireless(Integer wireless) { this.wireless = wireless; }

    public String getWirelessStr() {
        return wireless != null && wireless == 1 ? "是" : "否";
    }

    public Integer getNoiseCancelling() { return noiseCancelling; }
    public void setNoiseCancelling(Integer noiseCancelling) { this.noiseCancelling = noiseCancelling; }

    public String getNoiseCancellingStr() {
        return noiseCancelling != null && noiseCancelling == 1 ? "是" : "否";
    }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "Headphone{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", brand='" + brand + '\'' +
                ", driverSize=" + driverSize +
                ", impedance=" + impedance +
                ", sensitivity=" + sensitivity +
                ", frequencyResponse='" + frequencyResponse + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", wireless=" + getWirelessStr() +
                ", noiseCancelling=" + getNoiseCancellingStr() +
                ", createTime=" + createTime +
                '}';
    }
}