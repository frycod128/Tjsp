package cn.yznu.abc4321.mapper;

import cn.yznu.abc4321.entity.Headphone;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 耳机Mapper接口
 */
public interface HeadphoneMapper {

    /**
     * 查询所有耳机
     */
    List<Headphone> findAll();

    /**
     * 根据ID查询耳机
     */
    Headphone findById(@Param("id") Integer id);

    /**
     * 根据品牌查询耳机
     */
    List<Headphone> findByBrand(@Param("brand") String brand);

    /**
     * 根据价格范围查询
     */
    List<Headphone> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 查询无线耳机
     */
    List<Headphone> findWirelessHeadphones();

    /**
     * 查询降噪耳机
     */
    List<Headphone> findNoiseCancellingHeadphones();

    /**
     * 多条件动态查询
     */
    List<Headphone> findDynamic(Headphone headphone);

    /**
     * 新增耳机
     */
    int insert(Headphone headphone);

    /**
     * 更新耳机
     */
    int update(Headphone headphone);

    /**
     * 删除耳机
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 批量删除
     */
    int batchDelete(@Param("ids") List<Integer> ids);

    /**
     * 更新库存
     */
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);

    /**
     * 统计各品牌耳机数量
     */
    List<Map<String, Object>> countByBrand();

    /**
     * 分页查询
     */
    List<Headphone> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取总数
     */
    long getTotalCount();
}