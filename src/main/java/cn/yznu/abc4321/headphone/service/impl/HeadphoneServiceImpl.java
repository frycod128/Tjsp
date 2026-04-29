package cn.yznu.abc4321.headphone.service.impl;

import cn.yznu.abc4321.headphone.dao.HeadphoneDao;
import cn.yznu.abc4321.headphone.dao.impl.HeadphoneDaoImpl;
import cn.yznu.abc4321.headphone.entity.Headphone;
import cn.yznu.abc4321.headphone.entity.PageInfo;
import cn.yznu.abc4321.headphone.service.HeadphoneService;
import java.util.List;

public class HeadphoneServiceImpl implements HeadphoneService {
    private HeadphoneDao headphoneDao = new HeadphoneDaoImpl();

    @Override
    public boolean addHeadphone(Headphone headphone) {
        try {
            return headphoneDao.insert(headphone) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteHeadphone(Integer id) {
        try {
            return headphoneDao.deleteById(id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateHeadphone(Headphone headphone) {
        try {
            return headphoneDao.update(headphone) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Headphone getHeadphoneById(Integer id) {
        try {
            return headphoneDao.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Headphone> getAllHeadphones() {
        try {
            return headphoneDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Headphone> searchByModel(String keyword) {
        try {
            return headphoneDao.findByModel(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PageInfo<Headphone> getHeadphonesByPage(int currentPage, int pageSize) {
        try {
            int totalCount = headphoneDao.getTotalCount();
            int start = (currentPage - 1) * pageSize;
            List<Headphone> list = headphoneDao.findByPage(start, pageSize);
            return new PageInfo<>(currentPage, pageSize, totalCount, list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PageInfo<Headphone> searchByModelWithPage(String keyword, int currentPage, int pageSize) {
        try {
            int totalCount = headphoneDao.getTotalCountByModel(keyword);
            int start = (currentPage - 1) * pageSize;
            List<Headphone> list = headphoneDao.findByModelWithPage(keyword, start, pageSize);
            return new PageInfo<>(currentPage, pageSize, totalCount, list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 