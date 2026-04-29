package cn.yznu.abc4321.headphone.service.impl;

import cn.yznu.abc4321.common.dao.BaseDao;
import cn.yznu.abc4321.common.service.impl.BaseServiceImpl;
import cn.yznu.abc4321.headphone.dao.HeadphoneDao;
import cn.yznu.abc4321.headphone.dao.impl.HeadphoneDaoImpl;
import cn.yznu.abc4321.headphone.entity.Headphone;
import cn.yznu.abc4321.headphone.service.HeadphoneService;

public class HeadphoneServiceImpl extends BaseServiceImpl<Headphone>
        implements HeadphoneService {
    private HeadphoneDao headphoneDao = new HeadphoneDaoImpl();

    @Override
    protected BaseDao<Headphone> getDao() {
        return headphoneDao;
    }
}