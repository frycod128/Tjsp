package cn.yznu.abc4321.headphone;

import cn.yznu.abc4321.common.annotation.Column;
import cn.yznu.abc4321.common.annotation.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Table(name = "headphone")
public class Headphone {
    @Column(name = "id", id = true, autoIncrement = true)
    private Integer id;

    @Column(name = "model")
    private String model;

    @Column(name = "brand")
    private String brand;

    @Column(name = "driver_size")
    private Double driverSize;

    @Column(name = "impedance")
    private Integer impedance;

    @Column(name = "sensitivity")
    private Integer sensitivity;

    @Column(name = "frequency_response")
    private String frequencyResponse;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "wireless")
    private Boolean wireless;

    @Column(name = "noise_cancelling")
    private Boolean noiseCancelling;

    @Column(name = "create_time")
    private Timestamp createTime;

    // 无参构造（必须）
    public Headphone() {}

    // Getter/Setter省略...
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
    public Boolean getWireless() { return wireless; }
    public void setWireless(Boolean wireless) { this.wireless = wireless; }
    public Boolean getNoiseCancelling() { return noiseCancelling; }
    public void setNoiseCancelling(Boolean noiseCancelling) { this.noiseCancelling = noiseCancelling; }
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return String.format("Headphone[id=%d, %s %s, ¥%.2f, 库存:%d]",
                id, brand, model, price, stock);
    }
}