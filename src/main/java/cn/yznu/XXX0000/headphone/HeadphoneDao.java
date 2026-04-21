package cn.yznu.XXX0000.headphone;

import java.util.List;

public interface HeadphoneDao {
    /**
     * 新增耳机
     */
    int insert(Headphone headphone) throws Exception;

    /**
     * 根据ID删除耳机
     */
    int deleteById(Integer id) throws Exception;

    /**
     * 更新耳机信息
     */
    int update(Headphone headphone) throws Exception;

    /**
     * 根据ID查询耳机
     */
    Headphone findById(Integer id) throws Exception;

    /**
     * 查询所有耳机
     */
    List<Headphone> findAll() throws Exception;

    /**
     * 根据型号名称模糊查询
     */
    List<Headphone> findByModel(String keyword) throws Exception;

    /**
     * 根据品牌查询
     */
    List<Headphone> findByBrand(String brand) throws Exception;
}