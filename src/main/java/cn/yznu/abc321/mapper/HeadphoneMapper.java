package cn.yznu.abc321.mapper;

import cn.yznu.abc321.entity.Headphone;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface HeadphoneMapper {

    List<Headphone> findAll();

    Headphone findById(@Param("id") Integer id);

    List<Headphone> findByBrand(@Param("brand") String brand);

    List<Headphone> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice);

    List<Headphone> findWirelessHeadphones();

    List<Headphone> findNoiseCancellingHeadphones();

    // 修改：使用Map参数避免实体类缺少字段的问题
    List<Headphone> findDynamic(Map<String, Object> params);

    int insert(Headphone headphone);

    int update(Headphone headphone);

    int deleteById(@Param("id") Integer id);

    int batchDelete(@Param("ids") List<Integer> ids);

    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);

    List<Map<String, Object>> countByBrand();

    List<Headphone> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    long getTotalCount();
}