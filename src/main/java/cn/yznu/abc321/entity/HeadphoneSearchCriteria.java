package cn.yznu.abc321.entity;

import java.math.BigDecimal;

/** 动态SQL搜索条件，字段为null表示不参与查询 */
public class HeadphoneSearchCriteria {
    private String model;
    private String brand;
    private Double driverSize;
    private Integer impedance;
    private Integer sensitivity;
    private String frequencyResponse;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private Integer stock;
    private Integer wireless;       // null=不限, 0=否, 1=是
    private Integer noiseCancelling;

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
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getWireless() { return wireless; }
    public void setWireless(Integer wireless) { this.wireless = wireless; }
    public Integer getNoiseCancelling() { return noiseCancelling; }
    public void setNoiseCancelling(Integer noiseCancelling) { this.noiseCancelling = noiseCancelling; }
}
