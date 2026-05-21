package cn.yznu.abc4321.config;

import java.util.List;
import java.util.Map;

/**
 * 表配置类
 */
public class TableConfig {
    private String tableName;
    private String primaryKey;
    private Map<String, String> columns;
    private List<String> editableColumns;
    private Map<String, String> formTypes;
    private List<String> searchableColumns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public List<String> getEditableColumns() {
        return editableColumns;
    }

    public void setEditableColumns(List<String> editableColumns) {
        this.editableColumns = editableColumns;
    }

    public Map<String, String> getFormTypes() {
        return formTypes;
    }

    public void setFormTypes(Map<String, String> formTypes) {
        this.formTypes = formTypes;
    }

    public List<String> getSearchableColumns() {
        return searchableColumns;
    }

    public void setSearchableColumns(List<String> searchableColumns) {
        this.searchableColumns = searchableColumns;
    }

    public String getColumnLabel(String columnName) {
        if (columns != null && columns.containsKey(columnName)) {
            return columns.get(columnName);
        }
        return columnName;
    }

    public boolean isEditable(String columnName) {
        return editableColumns != null && editableColumns.contains(columnName);
    }
}