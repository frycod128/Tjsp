package cn.yznu.abc4321.headphone.servlet;

import cn.yznu.abc4321.common.service.BaseService;
import cn.yznu.abc4321.common.servlet.BaseServlet;
import cn.yznu.abc4321.headphone.entity.Headphone;
import cn.yznu.abc4321.headphone.service.HeadphoneService;
import cn.yznu.abc4321.headphone.service.impl.HeadphoneServiceImpl;

import javax.servlet.annotation.WebServlet;

@WebServlet("/headphone")
public class HeadphoneServlet extends BaseServlet<Headphone> {
    private HeadphoneService headphoneService = new HeadphoneServiceImpl();

    @Override
    protected BaseService<Headphone> getService() {
        return headphoneService;
    }

    @Override
    protected String getEntityName() {
        return "headphone";
    }

    @Override
    protected String getViewPath() {
        return "/WEB-INF/headphone/";
    }

    @Override
    protected Headphone createEntity() {
        return new Headphone();
    }
}