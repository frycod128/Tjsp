package cn.yznu.abc321.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime createTime;

    // getters & setters
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
    public Integer getNoiseCancelling() { return noiseCancelling; }
    public void setNoiseCancelling(Integer noiseCancelling) { this.noiseCancelling = noiseCancelling; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
