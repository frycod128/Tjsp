package cn.yznu.abc321.dao;

import cn.yznu.abc321.entity.PurchaseRecord;
import cn.yznu.abc321.entity.User;
import java.util.List;

public interface PurchaseDao {
    List<PurchaseRecord> findByPhone(String phone);
    List<User> suggestByPhone(String keyword);
}
