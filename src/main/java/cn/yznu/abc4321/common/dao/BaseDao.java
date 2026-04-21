package cn.yznu.abc4321.common.dao;

import java.util.List;

public interface BaseDao<T> {

    /**
     * 新增实体
     */
    int insert(T entity) throws Exception;

    /**
     * 根据主键删除
     */
    int deleteById(Object id) throws Exception;

    /**
     * 更新实体
     */
    int update(T entity) throws Exception;

    /**
     * 根据主键查询
     */
    T findById(Object id) throws Exception;

    /**
     * 查询所有记录
     */
    List<T> findAll() throws Exception;

    /**
     * 根据指定字段模糊查询
     */
    List<T> findByLike(String fieldName, String keyword) throws Exception;

    /**
     * 根据指定字段精确查询
     */
    List<T> findByEqual(String fieldName, Object value) throws Exception;

    /**
     * 条件查询（通用）
     */
    List<T> query(String whereClause, Object... params) throws Exception;
}