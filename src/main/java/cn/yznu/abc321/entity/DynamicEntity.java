package cn.yznu.abc321.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态实体类 - 完全通用的Map包装
 * 不预设任何字段，所有数据都在fields Map中
 */
public class DynamicEntity {
    private Map<String, Object> fields = new HashMap<>();

    public DynamicEntity() {}

    public DynamicEntity(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public void put(String key, Object value) {
        fields.put(key, value);
    }

    public Object get(String key) {
        return fields.get(key);
    }

    @Override
    public String toString() {
        return "DynamicEntity" + fields.toString();
    }
}