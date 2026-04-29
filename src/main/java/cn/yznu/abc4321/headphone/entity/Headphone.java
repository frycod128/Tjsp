package cn.yznu.abc4321.headphone.entity;

import cn.yznu.abc4321.common.entity.BaseEntity;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Headphone implements BaseEntity {
    private Integer id;
    private String model;
    private String brand;
    private Double driverSize;
    private Integer impedance;
    private Integer sensitivity;
    private String frequencyResponse;
    private BigDecimal price;
    private Integer stock;
    private Boolean wireless;
    private Boolean noiseCancelling;
    private Timestamp createTime;

    @Override
    public String getTableName() {
        return "headphone";
    }

    @Override
    public String getPrimaryKeyColumn() {
        return "id";
    }

    @Override
    public Object getPrimaryKeyValue() {
        return id;
    }

    @Override
    public void setPrimaryKeyValue(Object value) {
        if (value instanceof Number) {
            this.id = ((Number) value).intValue();
        }
    }

    // Getter和Setter方法
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
    public void setFrequencyResponse(String frequencyResponse) {
        this.frequencyResponse = frequencyResponse;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getWireless() { return wireless; }
    public void setWireless(Boolean wireless) { this.wireless = wireless; }

    public Boolean getNoiseCancelling() { return noiseCancelling; }
    public void setNoiseCancelling(Boolean noiseCancelling) {
        this.noiseCancelling = noiseCancelling;
    }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "Headphone{id=" + id + ", model='" + model + "', brand='" + brand +
                "', price=" + price + ", stock=" + stock + "}";
    }
}