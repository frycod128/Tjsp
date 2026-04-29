package cn.yznu.abc4321.headphone.entity;

import java.util.List;

public class PageInfo<T> {
    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private List<T> list;

    public PageInfo() {}

    public PageInfo(int currentPage, int pageSize, int totalCount, List<T> list) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
        this.list = list;
    }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public int getTotalPage() { return totalPage; }
    public void setTotalPage(int totalPage) { this.totalPage = totalPage; }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}