package cn.yznu.abc4321.common.metadata;

public class FieldMetadata {
    private String fieldName;      // Java字段名
    private String columnName;     // 数据库列名
    private Class<?> fieldType;    // 字段类型
    private boolean isId;          // 是否主键
    private boolean autoIncrement; // 是否自增
    private java.lang.reflect.Field field; // 反射Field对象

    // 构造器和Getter/Setter
    public FieldMetadata(String fieldName, String columnName, Class<?> fieldType,
                         boolean isId, boolean autoIncrement, java.lang.reflect.Field field) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.fieldType = fieldType;
        this.isId = isId;
        this.autoIncrement = autoIncrement;
        this.field = field;
    }

    public String getFieldName() { return fieldName; }
    public String getColumnName() { return columnName; }
    public Class<?> getFieldType() { return fieldType; }
    public boolean isId() { return isId; }
    public boolean isAutoIncrement() { return autoIncrement; }
    public java.lang.reflect.Field getField() { return field; }
}