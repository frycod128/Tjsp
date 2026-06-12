package cn.yznu.abc321.entity;

/** 一条外键关系 */
public class FkInfo {
    private String columnName;            // 本表列
    private String referencedTable;       // 引用表
    private String referencedColumn;      // 引用列

    public FkInfo() {}
    public FkInfo(String columnName, String referencedTable, String referencedColumn) {
        this.columnName = columnName;
        this.referencedTable = referencedTable;
        this.referencedColumn = referencedColumn;
    }

    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }
    public String getReferencedTable() { return referencedTable; }
    public void setReferencedTable(String referencedTable) { this.referencedTable = referencedTable; }
    public String getReferencedColumn() { return referencedColumn; }
    public void setReferencedColumn(String referencedColumn) { this.referencedColumn = referencedColumn; }
}
