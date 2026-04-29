package cn.yznu.abc4321.common.dao;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {
    int insert(T entity) throws Exception;
    int deleteById(Object id) throws Exception;
    int update(T entity) throws Exception;
    T findById(Object id) throws Exception;
    List<T> findAll() throws Exception;
    List<T> findByPage(int start, int pageSize) throws Exception;
    int getTotalCount() throws Exception;
    List<T> findByColumn(String columnName, String keyword, boolean fuzzy) throws Exception;
    List<T> findByColumnWithPage(String columnName, String keyword, boolean fuzzy,
                                 int start, int pageSize) throws Exception;
    int getTotalCountByColumn(String columnName, String keyword, boolean fuzzy) throws Exception;
    List<Map<String, Object>> getColumnMetadata() throws Exception;
}