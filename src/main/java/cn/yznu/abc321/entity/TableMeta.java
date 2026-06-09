package cn.yznu.abc321.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/** 表元数据：是否可查 + 列名→中文标签映射 */
public class TableMeta {
    private boolean queryable = true;
    private Map<String, String> columns = new LinkedHashMap<>();

    public boolean isQueryable() { return queryable; }
    public void setQueryable(boolean queryable) { this.queryable = queryable; }
    public Map<String, String> getColumns() { return columns; }
    public void setColumns(Map<String, String> columns) { this.columns = columns; }
}
