package cn.yznu.abc321.entity;

import java.util.*;

/** 表元数据 */
public class TableMeta {
    private boolean queryable = true;
    private Map<String, String> columns = new LinkedHashMap<>(); // 列名→中文标签
    private List<String> fuzzyColumns = new ArrayList<>();       // 适合模糊查询的列

    public boolean isQueryable() { return queryable; }
    public void setQueryable(boolean queryable) { this.queryable = queryable; }
    public Map<String, String> getColumns() { return columns; }
    public void setColumns(Map<String, String> columns) { this.columns = columns; }
    public List<String> getFuzzyColumns() { return fuzzyColumns; }
    public void setFuzzyColumns(List<String> fuzzyColumns) { this.fuzzyColumns = fuzzyColumns; }
}
