package cn.yznu.abc321.dao;

import cn.yznu.abc321.entity.Headphone;
import cn.yznu.abc321.entity.HeadphoneSearchCriteria;
import java.util.List;

public interface HeadphoneDao {

    /** 动态SQL多条件AND查询 */
    List<Headphone> dynamicSearch(HeadphoneSearchCriteria criteria);

    /** 批量商品名模糊OR查询 */
    List<Headphone> searchByModels(List<String> models);
}
