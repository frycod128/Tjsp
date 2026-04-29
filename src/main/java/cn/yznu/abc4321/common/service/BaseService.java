package cn.yznu.abc4321.common.service;

import cn.yznu.abc4321.headphone.entity.PageInfo;

import java.util.List;
import java.util.Map;

public interface BaseService<T> {
    boolean add(T entity);
    boolean delete(Object id);
    boolean update(T entity);
    T getById(Object id);
    List<T> getAll();
    PageInfo<T> getByPage(int currentPage, int pageSize);
    PageInfo<T> searchByColumn(String columnName, String keyword, boolean fuzzy,
                               int currentPage, int pageSize);
    List<Map<String, Object>> getColumns();
}