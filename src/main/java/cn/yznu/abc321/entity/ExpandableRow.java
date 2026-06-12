package cn.yznu.abc321.entity;

import java.util.*;

/** 可展开行：本行数据 + 外键展开的子行 */
public class ExpandableRow {
    private String tableName;
    private Map<String, String> columnLabels;          // 列名→中文标签
    private Map<String, Object> rowData;               // 实际数据
    private Map<String, List<ExpandableRow>> children = new LinkedHashMap<>(); // 描述→子行列表

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public Map<String, String> getColumnLabels() { return columnLabels; }
    public void setColumnLabels(Map<String, String> columnLabels) { this.columnLabels = columnLabels; }
    public Map<String, Object> getRowData() { return rowData; }
    public void setRowData(Map<String, Object> rowData) { this.rowData = rowData; }
    public Map<String, List<ExpandableRow>> getChildren() { return children; }
    public void setChildren(Map<String, List<ExpandableRow>> children) { this.children = children; }
}
