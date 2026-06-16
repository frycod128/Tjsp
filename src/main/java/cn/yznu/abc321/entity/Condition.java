package cn.yznu.abc321.entity;

/** 一个查询条件：列名 + 值 + 是否模糊 */
public class Condition {
    private String column;
    private String value;
    private boolean fuzzy;

    public Condition() {}
    public Condition(String column, String value, boolean fuzzy) {
        this.column = column;
        this.value = value;
        this.fuzzy = fuzzy;
    }

    public String getColumn() { return column; }
    public void setColumn(String column) { this.column = column; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public boolean isFuzzy() { return fuzzy; }
    public void setFuzzy(boolean fuzzy) { this.fuzzy = fuzzy; }
}
