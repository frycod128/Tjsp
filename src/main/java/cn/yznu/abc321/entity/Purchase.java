package cn.yznu.abc321.entity;

import java.time.LocalDateTime;

public class Purchase {
    private Integer id;
    private Integer userId;
    private Integer headphoneId;
    private Integer quantity;
    private LocalDateTime orderTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getHeadphoneId() { return headphoneId; }
    public void setHeadphoneId(Integer headphoneId) { this.headphoneId = headphoneId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
}
