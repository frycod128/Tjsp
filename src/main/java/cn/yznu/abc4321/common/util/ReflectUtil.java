package cn.yznu.abc4321.common.util;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectUtil {

    /**
     * 获取实体类的字段名和数据库列名映射
     * 默认规则：驼峰转下划线 (driverSize -> driver_size)
     */
    public static Map<String, String> getFieldColumnMapping(Class<?> clazz, String excludeField) {
        Map<String, String> map = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            // 跳过主键字段
            if (fieldName.equals(excludeField)) continue;
            // 跳过序列化相关字段
            if ("serialVersionUID".equals(fieldName)) continue;

            String columnName = camelToSnake(fieldName);
            map.put(fieldName, columnName);
        }
        return map;
    }

    /**
     * 驼峰命名转下划线命名
     */
    public static String camelToSnake(String camel) {
        StringBuilder sb = new StringBuilder();
        for (char c : camel.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 根据字段名获取Field对象(递归查找父类)
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取所有字段(含父类)
     */
    public static Field[] getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!"serialVersionUID".equals(field.getName())) {
                    fieldMap.putIfAbsent(field.getName(), field);
                }
            }
            current = current.getSuperclass();
        }
        return fieldMap.values().toArray(new Field[0]);
    }
}