package cn.yznu.abc321.dao;

import cn.yznu.abc321.entity.PurchaseRecord;
import java.util.List;

public interface PurchaseDao {
    /** 根据手机号查询该用户全部购买记录 */
    List<PurchaseRecord> findByPhone(String phone);
}
