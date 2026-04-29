package cn.yznu.abc4321.common.dao.impl;

import cn.yznu.abc4321.common.dao.BaseDao;
import cn.yznu.abc4321.common.entity.BaseEntity;
import cn.yznu.abc4321.common.util.DBUtil;
import cn.yznu.abc4321.common.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {

    protected Class<T> entityClass;
    protected String tableName;
    protected String primaryKeyColumn;
    protected List<String> columnNames;  // 数据库列名(不含主键)
    protected Map<String, String> fieldColumnMap;  // 字段名 -> 列名

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        // 通过反射获取泛型实际类型
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) superClass;
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                this.entityClass = (Class<T>) typeArgs[0];
            }
        }
        initMetadata();
    }

    private void initMetadata() {
        try {
            T instance = entityClass.newInstance();
            this.tableName = instance.getTableName();
            this.primaryKeyColumn = instance.getPrimaryKeyColumn();
            this.fieldColumnMap = ReflectUtil.getFieldColumnMapping(entityClass, primaryKeyColumn);
            this.columnNames = new ArrayList<>(fieldColumnMap.values());
        } catch (Exception e) {
            throw new RuntimeException("初始化元数据失败", e);
        }
    }

    @Override
    public int insert(T entity) throws Exception {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");
        List<String> columns = new ArrayList<>(columnNames);
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        for (int i = 0; i < columns.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString(),
                     Statement.RETURN_GENERATED_KEYS)) {
            setParamsByColumns(pstmt, entity, columns);
            int result = pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setPrimaryKeyValue(rs.getObject(1));
                }
            }
            return result;
        }
    }

    @Override
    public int deleteById(Object id) throws Exception {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public int update(T entity) throws Exception {
        List<String> columns = new ArrayList<>(columnNames);
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(columns.get(i)).append(" = ?");
        }
        sql.append(" WHERE ").append(primaryKeyColumn).append(" = ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            setParamsByColumns(pstmt, entity, columns);
            pstmt.setObject(columns.size() + 1, entity.getPrimaryKeyValue());
            return pstmt.executeUpdate();
        }
    }

    @Override
    public T findById(Object id) throws Exception {
        String sql = "SELECT * FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<T> findAll() throws Exception {
        String sql = "SELECT * FROM " + tableName;
        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    @Override
    public List<T> findByPage(int start, int pageSize) throws Exception {
        String sql = "SELECT * FROM " + tableName + " ORDER BY " + primaryKeyColumn + " LIMIT ?, ?";
        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, start);
            pstmt.setInt(2, pageSize);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public int getTotalCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    @Override
    public List<T> findByColumn(String columnName, String keyword, boolean fuzzy) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
        if (fuzzy) {
            sql.append(columnName).append(" LIKE ?");
        } else {
            sql.append(columnName).append(" = ?");
        }
        sql.append(" ORDER BY ").append(primaryKeyColumn);

        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setString(1, fuzzy ? "%" + keyword + "%" : keyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<T> findByColumnWithPage(String columnName, String keyword, boolean fuzzy,
                                        int start, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
        if (fuzzy) {
            sql.append(columnName).append(" LIKE ?");
        } else {
            sql.append(columnName).append(" = ?");
        }
        sql.append(" ORDER BY ").append(primaryKeyColumn).append(" LIMIT ?, ?");

        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setString(1, fuzzy ? "%" + keyword + "%" : keyword);
            pstmt.setInt(2, start);
            pstmt.setInt(3, pageSize);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public int getTotalCountByColumn(String columnName, String keyword, boolean fuzzy) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " WHERE ");
        if (fuzzy) {
            sql.append(columnName).append(" LIKE ?");
        } else {
            sql.append(columnName).append(" = ?");
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setString(1, fuzzy ? "%" + keyword + "%" : keyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> getColumnMetadata() throws Exception {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT, COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = ? " +
                "ORDER BY ORDINAL_POSITION";
        List<Map<String, Object>> columns = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> col = new LinkedHashMap<>();
                    col.put("columnName", rs.getString("COLUMN_NAME"));
                    col.put("dataType", rs.getString("DATA_TYPE"));
                    col.put("comment", rs.getString("COLUMN_COMMENT"));
                    col.put("isPrimaryKey", "PRI".equals(rs.getString("COLUMN_KEY")));
                    columns.add(col);
                }
            }
        }
        return columns;
    }

    protected T mapResultSet(ResultSet rs) throws Exception {
        T entity = entityClass.newInstance();
        // 设置主键
        entity.setPrimaryKeyValue(rs.getObject(primaryKeyColumn));
        // 设置其他字段
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            String fieldName = entry.getKey();
            String columnName = entry.getValue();
            Field field = ReflectUtil.getField(entityClass, fieldName);
            if (field != null) {
                field.setAccessible(true);
                Object value = rs.getObject(columnName);
                if (value != null) {
                    setFieldValue(field, entity, value);
                }
            }
        }
        return entity;
    }

    protected void setParamsByColumns(PreparedStatement pstmt, T entity,
                                      List<String> columns) throws Exception {
        List<String> fieldNames = new ArrayList<>();
        Map<String, String> reverseMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        for (String col : columns) {
            fieldNames.add(reverseMap.get(col));
        }

        for (int i = 0; i < fieldNames.size(); i++) {
            Field field = ReflectUtil.getField(entityClass, fieldNames.get(i));
            if (field != null) {
                field.setAccessible(true);
                Object value = field.get(entity);
                pstmt.setObject(i + 1, value);
            }
        }
    }

    private void setFieldValue(Field field, Object entity, Object value) throws Exception {
        Class<?> type = field.getType();
        if (type == Integer.class || type == int.class) {
            field.set(entity, ((Number) value).intValue());
        } else if (type == Long.class || type == long.class) {
            field.set(entity, ((Number) value).longValue());
        } else if (type == Double.class || type == double.class) {
            field.set(entity, ((Number) value).doubleValue());
        } else if (type == BigDecimal.class) {
            field.set(entity, new BigDecimal(value.toString()));
        } else if (type == Boolean.class || type == boolean.class) {
            if (value instanceof Number) {
                field.set(entity, ((Number) value).intValue() == 1);
            } else {
                field.set(entity, Boolean.parseBoolean(value.toString()));
            }
        } else if (type == Timestamp.class) {
            field.set(entity, value);
        } else {
            field.set(entity, value);
        }
    }
}