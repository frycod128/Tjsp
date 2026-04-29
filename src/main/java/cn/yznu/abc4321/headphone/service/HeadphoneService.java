package cn.yznu.abc4321.headphone.service;

import cn.yznu.abc4321.headphone.entity.Headphone;
import cn.yznu.abc4321.headphone.entity.PageInfo;
import java.util.List;

public interface HeadphoneService {
    boolean addHeadphone(Headphone headphone);
    boolean deleteHeadphone(Integer id);
    boolean updateHeadphone(Headphone headphone);
    Headphone getHeadphoneById(Integer id);
    List<Headphone> getAllHeadphones();
    List<Headphone> searchByModel(String keyword);
    PageInfo<Headphone> getHeadphonesByPage(int currentPage, int pageSize);
    PageInfo<Headphone> searchByModelWithPage(String keyword, int currentPage, int pageSize);
}