package cn.yznu.abc4321.headphone.dao;

import cn.yznu.abc4321.headphone.entity.Headphone;
import java.util.List;

public interface HeadphoneDao {
    // 新增耳机
    int insert(Headphone headphone) throws Exception;

    // 根据ID删除耳机
    int deleteById(Integer id) throws Exception;

    // 更新耳机信息
    int update(Headphone headphone) throws Exception;

    // 根据ID查询耳机
    Headphone findById(Integer id) throws Exception;

    // 查询所有耳机
    List<Headphone> findAll() throws Exception;

    // 根据型号模糊查询
    List<Headphone> findByModel(String keyword) throws Exception;

    // 分页查询所有耳机
    List<Headphone> findByPage(int start, int pageSize) throws Exception;

    // 获取总记录数
    int getTotalCount() throws Exception;

    // 分页模糊查询
    List<Headphone> findByModelWithPage(String keyword, int start, int pageSize) throws Exception;

    // 获取模糊查询总记录数
    int getTotalCountByModel(String keyword) throws Exception;
}