package cn.yznu.abc4321;

import cn.yznu.abc4321.entity.Headphone;
import cn.yznu.abc4321.mapper.HeadphoneMapper;
import cn.yznu.abc4321.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeadphoneTest {

    private SqlSession sqlSession;
    private HeadphoneMapper mapper;

    @Before
    public void setUp() {
        sqlSession = MyBatisUtil.getSqlSession(false);
        mapper = sqlSession.getMapper(HeadphoneMapper.class);
        System.out.println("================== 开始测试 ==================");
    }

    @After
    public void tearDown() {
        if (sqlSession != null) {
            sqlSession.close();
        }
        System.out.println("================== 测试结束 ==================\n");
    }

    @Test
    public void test01FindAll() {
        System.out.println("测试1: 查询所有耳机");
        List<Headphone> list = mapper.findAll();
        assertNotNull(list);
        assertTrue(list.size() > 0);
        System.out.println("共查询到 " + list.size() + " 条记录");
        for (Headphone h : list) {
            System.out.println(h);
        }
    }

    @Test
    public void test02FindById() {
        System.out.println("测试2: 根据ID查询耳机");
        Headphone headphone = mapper.findById(1);
        assertNotNull(headphone);
        assertEquals("WH-1000XM5", headphone.getModel());
        System.out.println("查询结果: " + headphone);
    }

    @Test
    public void test03FindByBrand() {
        System.out.println("测试3: 根据品牌查询耳机");
        List<Headphone> list = mapper.findByBrand("Sony");
        assertNotNull(list);
        assertTrue(list.size() > 0);
        System.out.println("Sony品牌共 " + list.size() + " 款耳机");
        for (Headphone h : list) {
            System.out.println("  - " + h.getModel() + " ¥" + h.getPrice());
        }
    }

    @Test
    public void test04FindByPriceRange() {
        System.out.println("测试4: 根据价格范围查询");
        List<Headphone> list = mapper.findByPriceRange(new BigDecimal("500"), new BigDecimal("1500"));
        assertNotNull(list);
        System.out.println("价格范围 500-1500 的耳机共 " + list.size() + " 款");
        for (Headphone h : list) {
            System.out.println("  - " + h.getModel() + " ¥" + h.getPrice());
        }
    }

    @Test
    public void test05FindWireless() {
        System.out.println("测试5: 查询无线耳机");
        List<Headphone> list = mapper.findWirelessHeadphones();
        assertNotNull(list);
        System.out.println("无线耳机共 " + list.size() + " 款");
        for (Headphone h : list) {
            System.out.println("  - " + h.getModel() + " (无线:" + h.getWirelessStr() + ")");
        }
    }

    @Test
    public void test06FindNoiseCancelling() {
        System.out.println("测试6: 查询降噪耳机");
        List<Headphone> list = mapper.findNoiseCancellingHeadphones();
        assertNotNull(list);
        System.out.println("降噪耳机共 " + list.size() + " 款");
        for (Headphone h : list) {
            System.out.println("  - " + h.getModel() + " (降噪:" + h.getNoiseCancellingStr() + ")");
        }
    }

    @Test
    public void test07DynamicQuery() {
        System.out.println("测试7: 动态条件查询");
        Headphone condition = new Headphone();
        condition.setBrand("Sennheiser");
        condition.setWireless(0);

        List<Headphone> list = mapper.findDynamic(condition);
        assertNotNull(list);
        System.out.println("条件查询结果:");
        for (Headphone h : list) {
            System.out.println("  - " + h);
        }
    }

    @Test
    public void test08Insert() {
        System.out.println("测试8: 新增耳机");
        Headphone newHeadphone = new Headphone();
        newHeadphone.setModel("Test Model X1");
        newHeadphone.setBrand("TestBrand");
        newHeadphone.setDriverSize(40.0);
        newHeadphone.setImpedance(32);
        newHeadphone.setSensitivity(100);
        newHeadphone.setFrequencyResponse("20Hz-20kHz");
        newHeadphone.setPrice(new BigDecimal("999.00"));
        newHeadphone.setStock(10);
        newHeadphone.setWireless(1);
        newHeadphone.setNoiseCancelling(0);

        int rows = mapper.insert(newHeadphone);
        sqlSession.commit();

        assertEquals(1, rows);
        assertNotNull(newHeadphone.getId());
        System.out.println("新增成功，ID: " + newHeadphone.getId());

        // 验证新增
        Headphone saved = mapper.findById(newHeadphone.getId());
        assertNotNull(saved);
        assertEquals("Test Model X1", saved.getModel());

        // 清理测试数据
        mapper.deleteById(newHeadphone.getId());
        sqlSession.commit();
        System.out.println("测试数据已清理");
    }

    @Test
    public void test09Update() {
        System.out.println("测试9: 更新耳机信息");
        Headphone headphone = mapper.findById(1);
        assertNotNull(headphone);

        String oldModel = headphone.getModel();
        headphone.setModel(oldModel + "_Updated");
        headphone.setPrice(new BigDecimal("2499.00"));

        int rows = mapper.update(headphone);
        sqlSession.commit();
        assertEquals(1, rows);

        // 验证更新
        Headphone updated = mapper.findById(1);
        assertEquals(oldModel + "_Updated", updated.getModel());
        assertEquals(new BigDecimal("2499.00"), updated.getPrice());
        System.out.println("更新成功: " + updated);

        // 恢复数据
        headphone.setModel(oldModel);
        headphone.setPrice(new BigDecimal("2299.00"));
        mapper.update(headphone);
        sqlSession.commit();
        System.out.println("数据已恢复");
    }

    @Test
    public void test10UpdateStock() {
        System.out.println("测试10: 更新库存");
        Headphone before = mapper.findById(2);
        int oldStock = before.getStock();

        int newStock = oldStock + 5;
        int rows = mapper.updateStock(2, newStock);
        sqlSession.commit();
        assertEquals(1, rows);

        Headphone after = mapper.findById(2);
        assertEquals(newStock, after.getStock().intValue());
        System.out.println("库存更新: " + oldStock + " -> " + newStock);

        // 恢复
        mapper.updateStock(2, oldStock);
        sqlSession.commit();
        System.out.println("库存已恢复");
    }

    @Test
    public void test11CountByBrand() {
        System.out.println("测试11: 统计各品牌耳机数量");
        List<Map<String, Object>> stats = mapper.countByBrand();
        assertNotNull(stats);
        System.out.println("品牌统计结果:");
        for (Map<String, Object> stat : stats) {
            System.out.println("  品牌: " + stat.get("brand") +
                    ", 数量: " + stat.get("count") +
                    ", 均价: ¥" + stat.get("avgPrice"));
        }
    }

    @Test
    public void test12PageQuery() {
        System.out.println("测试12: 分页查询");
        long total = mapper.getTotalCount();
        System.out.println("总记录数: " + total);

        int pageSize = 3;
        int pageNum = 1;
        List<Headphone> page1 = mapper.findByPage(0, pageSize);
        assertEquals(pageSize, page1.size());
        System.out.println("第1页数据:");
        for (Headphone h : page1) {
            System.out.println("  - " + h.getModel());
        }

        if (total > pageSize) {
            List<Headphone> page2 = mapper.findByPage(pageSize, pageSize);
            System.out.println("第2页数据:");
            for (Headphone h : page2) {
                System.out.println("  - " + h.getModel());
            }
        }
    }

    @Test
    public void test13BatchDelete() {
        System.out.println("测试13: 批量删除");
        // 先插入测试数据
        List<Integer> testIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Headphone temp = new Headphone();
            temp.setModel("BatchTest" + i);
            temp.setBrand("Test");
            temp.setPrice(new BigDecimal("100.00"));
            temp.setStock(1);
            mapper.insert(temp);
            sqlSession.commit();
            testIds.add(temp.getId());
        }
        System.out.println("插入测试数据: " + testIds);

        // 批量删除
        int rows = mapper.batchDelete(testIds);
        sqlSession.commit();
        assertEquals(3, rows);
        System.out.println("批量删除成功，删除 " + rows + " 条");

        // 验证删除
        for (Integer id : testIds) {
            assertNull(mapper.findById(id));
        }
        System.out.println("验证通过");
    }

    @Test
    public void test14ComplexQuery() {
        System.out.println("测试14: 复杂查询 - 无线降噪耳机");
        Headphone condition = new Headphone();
        condition.setWireless(1);
        condition.setNoiseCancelling(1);

        List<Headphone> list = mapper.findDynamic(condition);
        System.out.println("无线+降噪耳机共 " + list.size() + " 款:");
        for (Headphone h : list) {
            System.out.println("  - " + h.getBrand() + " " + h.getModel() +
                    " ¥" + h.getPrice());
        }

        // 找出最贵的耳机
        Headphone mostExpensive = null;
        for (Headphone h : mapper.findAll()) {
            if (mostExpensive == null || h.getPrice().compareTo(mostExpensive.getPrice()) > 0) {
                mostExpensive = h;
            }
        }
        System.out.println("最贵耳机: " + mostExpensive);
    }
}