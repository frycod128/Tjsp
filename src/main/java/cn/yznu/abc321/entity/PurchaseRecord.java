package cn.yznu.abc321.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 联合查询结果：用户 + 耳机 + 购买信息 */
public class PurchaseRecord {
    private String username;
    private String phone;
    private String model;
    private String brand;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime orderTime;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
}
