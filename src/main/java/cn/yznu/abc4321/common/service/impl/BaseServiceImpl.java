package cn.yznu.abc4321.common.service.impl;

import cn.yznu.abc4321.common.dao.BaseDao;
import cn.yznu.abc4321.common.service.BaseService;
import cn.yznu.abc4321.headphone.entity.PageInfo;

import java.util.List;
import java.util.Map;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    protected abstract BaseDao<T> getDao();

    @Override
    public boolean add(T entity) {
        try {
            return getDao().insert(entity) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Object id) {
        try {
            return getDao().deleteById(id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(T entity) {
        try {
            return getDao().update(entity) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public T getById(Object id) {
        try {
            return getDao().findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> getAll() {
        try {
            return getDao().findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PageInfo<T> getByPage(int currentPage, int pageSize) {
        try {
            int totalCount = getDao().getTotalCount();
            int start = (currentPage - 1) * pageSize;
            List<T> list = getDao().findByPage(start, pageSize);
            return new PageInfo<>(currentPage, pageSize, totalCount, list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PageInfo<T> searchByColumn(String columnName, String keyword, boolean fuzzy,
                                      int currentPage, int pageSize) {
        try {
            int totalCount = getDao().getTotalCountByColumn(columnName, keyword, fuzzy);
            int start = (currentPage - 1) * pageSize;
            List<T> list = getDao().findByColumnWithPage(columnName, keyword, fuzzy, start, pageSize);
            return new PageInfo<>(currentPage, pageSize, totalCount, list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getColumns() {
        try {
            return getDao().getColumnMetadata();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}