package cn.yznu.abc4321.common.dao;

import cn.yznu.abc4321.common.annotation.Column;
import cn.yznu.abc4321.common.annotation.Table;
import cn.yznu.abc4321.common.metadata.FieldMetadata;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseDaoImpl<T> implements BaseDao<T> {

    private Class<T> entityClass;
    private String tableName;
    private Map<String, FieldMetadata> fieldMap = new LinkedHashMap<>();
    private FieldMetadata idField;

    private String driver;
    private String url;
    private String username;
    private String password;

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() throws Exception {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) type.getActualTypeArguments()[0];
        parseEntityAnnotations();
        loadDbConfig();
    }

    private void parseEntityAnnotations() {
        Table tableAnno = entityClass.getAnnotation(Table.class);
        if (tableAnno != null) {
            this.tableName = tableAnno.name();
        } else {
            this.tableName = entityClass.getSimpleName().toLowerCase();
        }

        for (Field field : entityClass.getDeclaredFields()) {
            Column columnAnno = field.getAnnotation(Column.class);
            if (columnAnno != null && columnAnno.ignore()) {
                continue;
            }

            field.setAccessible(true);
            String columnName = (columnAnno != null && !columnAnno.name().isEmpty())
                    ? columnAnno.name()
                    : camelToSnake(field.getName());

            boolean isId = columnAnno != null && columnAnno.id();
            boolean autoIncrement = columnAnno != null && columnAnno.autoIncrement();

            FieldMetadata metadata = new FieldMetadata(
                    field.getName(), columnName, field.getType(), isId, autoIncrement, field
            );

            fieldMap.put(field.getName(), metadata);

            if (isId) {
                idField = metadata;
            }
        }

        if (idField == null) {
            for (FieldMetadata fm : fieldMap.values()) {
                if ("id".equalsIgnoreCase(fm.getFieldName())) {
                    idField = fm;
                    break;
                }
            }
        }
    }

    private void loadDbConfig() throws Exception {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db-config.json");
             InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            this.driver = json.get("driver").getAsString();
            this.url = json.get("url").getAsString();
            this.username = json.get("username").getAsString();
            this.password = json.get("password").getAsString();

            Class.forName(driver);
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private String camelToSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 类型转换核心方法 - 修复版本
     */
    private Object convertType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        // String 转换
        if (targetType == String.class) {
            return value.toString();
        }

        // 数值类型转换
        if (value instanceof Number) {
            Number num = (Number) value;

            if (targetType == Integer.class || targetType == int.class) {
                return num.intValue();
            }
            if (targetType == Long.class || targetType == long.class) {
                return num.longValue();
            }
            if (targetType == Double.class || targetType == double.class) {
                return num.doubleValue();
            }
            if (targetType == Float.class || targetType == float.class) {
                return num.floatValue();
            }
            if (targetType == Short.class || targetType == short.class) {
                return num.shortValue();
            }
            if (targetType == Byte.class || targetType == byte.class) {
                return num.byteValue();
            }
            if (targetType == BigDecimal.class) {
                return new BigDecimal(num.toString());
            }
            if (targetType == BigInteger.class) {
                return BigInteger.valueOf(num.longValue());
            }
        }

        // BigDecimal 转其他数值
        if (value instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) value;
            if (targetType == Integer.class || targetType == int.class) {
                return bd.intValue();
            }
            if (targetType == Long.class || targetType == long.class) {
                return bd.longValue();
            }
            if (targetType == Double.class || targetType == double.class) {
                return bd.doubleValue();
            }
            if (targetType == Float.class || targetType == float.class) {
                return bd.floatValue();
            }
        }

        // BigInteger 转换
        if (value instanceof BigInteger) {
            BigInteger bi = (BigInteger) value;
            if (targetType == Integer.class || targetType == int.class) {
                return bi.intValue();
            }
            if (targetType == Long.class || targetType == long.class) {
                return bi.longValue();
            }
        }

        // Boolean 转换（数据库 TINYINT）
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue() == 1;
            }
            if (value instanceof String) {
                return "true".equalsIgnoreCase((String) value) || "1".equals(value);
            }
        }

        // java.util.Date 和 Timestamp 转换
        if (targetType == java.util.Date.class && value instanceof Timestamp) {
            return new java.util.Date(((Timestamp) value).getTime());
        }
        if (targetType == Timestamp.class && value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }

        // 默认返回原值
        return value;
    }

    private void setParameter(PreparedStatement pstmt, int index, Object value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.NULL);
        } else if (value instanceof String) {
            pstmt.setString(index, (String) value);
        } else if (value instanceof Integer) {
            pstmt.setInt(index, (Integer) value);
        } else if (value instanceof Long) {
            pstmt.setLong(index, (Long) value);
        } else if (value instanceof Double) {
            pstmt.setDouble(index, (Double) value);
        } else if (value instanceof Float) {
            pstmt.setFloat(index, (Float) value);
        } else if (value instanceof Boolean) {
            pstmt.setInt(index, ((Boolean) value) ? 1 : 0);
        } else if (value instanceof BigDecimal) {
            pstmt.setBigDecimal(index, (BigDecimal) value);
        } else if (value instanceof java.util.Date) {
            pstmt.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        } else if (value instanceof Timestamp) {
            pstmt.setTimestamp(index, (Timestamp) value);
        } else {
            pstmt.setObject(index, value);
        }
    }

    /**
     * 从ResultSet构建实体对象 - 修复版本
     */
    private T buildEntity(ResultSet rs) throws Exception {
        T entity = entityClass.getDeclaredConstructor().newInstance();

        for (FieldMetadata fm : fieldMap.values()) {
            try {
                Object value = rs.getObject(fm.getColumnName());

                // 如果值为null，跳过
                if (value == null) {
                    continue;
                }

                // 类型转换
                Object convertedValue = convertType(value, fm.getFieldType());
                fm.getField().set(entity, convertedValue);

            } catch (SQLException e) {
                // 列不存在时忽略
                System.err.println("Warning: Column '" + fm.getColumnName() + "' not found in ResultSet");
            }
        }

        return entity;
    }

    @Override
    public int insert(T entity) throws Exception {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<FieldMetadata> insertFields = new ArrayList<>();

        for (FieldMetadata fm : fieldMap.values()) {
            if (fm.isId() && fm.isAutoIncrement()) {
                continue;
            }

            Object value = fm.getField().get(entity);
            if (value != null) {
                insertFields.add(fm);
                sql.append(fm.getColumnName()).append(",");
                values.append("?,");
            }
        }

        if (insertFields.isEmpty()) {
            throw new RuntimeException("没有需要插入的字段");
        }

        sql.deleteCharAt(sql.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql.append(")").append(values).append(")");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString(),
                     Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < insertFields.size(); i++) {
                FieldMetadata fm = insertFields.get(i);
                Object value = fm.getField().get(entity);
                setParameter(pstmt, i + 1, value);
            }

            int result = pstmt.executeUpdate();

            // 设置自增ID - 修复类型转换
            if (idField != null && idField.isAutoIncrement()) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Object generatedId = rs.getObject(1);
                        Object convertedId = convertType(generatedId, idField.getFieldType());
                        idField.getField().set(entity, convertedId);
                    }
                }
            }

            return result;
        }
    }

    @Override
    public int deleteById(Object id) throws Exception {
        if (idField == null) {
            throw new RuntimeException("未找到主键字段");
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idField.getColumnName() + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameter(pstmt, 1, id);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public int update(T entity) throws Exception {
        if (idField == null) {
            throw new RuntimeException("未找到主键字段");
        }

        Object idValue = idField.getField().get(entity);
        if (idValue == null) {
            throw new RuntimeException("主键值不能为空");
        }

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        List<FieldMetadata> updateFields = new ArrayList<>();

        for (FieldMetadata fm : fieldMap.values()) {
            if (fm.isId()) continue;

            Object value = fm.getField().get(entity);
            if (value != null) {
                updateFields.add(fm);
                sql.append(fm.getColumnName()).append(" = ?,");
            }
        }

        if (updateFields.isEmpty()) {
            return 0;
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(idField.getColumnName()).append(" = ?");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (FieldMetadata fm : updateFields) {
                setParameter(pstmt, index++, fm.getField().get(entity));
            }
            setParameter(pstmt, index, idValue);

            return pstmt.executeUpdate();
        }
    }

    @Override
    public T findById(Object id) throws Exception {
        if (idField == null) {
            throw new RuntimeException("未找到主键字段");
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + idField.getColumnName() + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameter(pstmt, 1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<T> findAll() throws Exception {
        String sql = "SELECT * FROM " + tableName;
        return query(sql);
    }

    @Override
    public List<T> findByLike(String fieldName, String keyword) throws Exception {
        FieldMetadata fm = fieldMap.get(fieldName);
        if (fm == null) {
            throw new RuntimeException("字段不存在: " + fieldName);
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + fm.getColumnName() + " LIKE ?";
        return query(sql, "%" + keyword + "%");
    }

    @Override
    public List<T> findByEqual(String fieldName, Object value) throws Exception {
        FieldMetadata fm = fieldMap.get(fieldName);
        if (fm == null) {
            throw new RuntimeException("字段不存在: " + fieldName);
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + fm.getColumnName() + " = ?";
        return query(sql, value);
    }

    @Override
    public List<T> query(String sql, Object... params) throws Exception {
        List<T> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                setParameter(pstmt, i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(buildEntity(rs));
                }
            }
        }
        return list;
    }
}