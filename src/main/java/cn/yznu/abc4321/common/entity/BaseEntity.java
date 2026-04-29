package cn.yznu.abc4321.common.entity;

/**
 * 基础实体接口，所有实体类必须实现
 * 提供获取表名和主键值的通用方法
 */
public interface BaseEntity {
    /** 获取表名 */
    String getTableName();
    /** 获取主键列名 */
    String getPrimaryKeyColumn();
    /** 获取主键值 */
    Object getPrimaryKeyValue();
    /** 设置主键值 */
    void setPrimaryKeyValue(Object value);
}