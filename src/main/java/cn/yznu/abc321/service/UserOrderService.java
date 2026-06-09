package cn.yznu.abc321.service;

import cn.yznu.abc321.dao.PurchaseDao;
import cn.yznu.abc321.entity.PurchaseRecord;
import cn.yznu.abc321.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;
import java.util.List;

public class UserOrderService {

    /** 根据手机号查询购买记录，无结果返回空列表 */
    public List<PurchaseRecord> queryByPhone(String phone) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PurchaseDao dao = session.getMapper(PurchaseDao.class);
            return dao.findByPhone(phone);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
