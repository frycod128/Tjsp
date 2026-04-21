package cn.yznu.abc4321.headphone;

import org.junit.*;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;

public class HeadphoneDaoTest {
    private static HeadphoneDao dao;
    private static Integer testId;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new HeadphoneDaoImpl();
    }

    @Test
    public void test01Insert() throws Exception {
        Headphone hp = new Headphone();
        hp.setModel("Sundara");
        hp.setBrand("HIFIMAN");
        hp.setDriverSize(50.0);
        hp.setImpedance(37);
        hp.setSensitivity(94);
        hp.setFrequencyResponse("6Hz-75kHz");
        hp.setPrice(new BigDecimal("1899.00"));
        hp.setStock(25);
        hp.setWireless(false);
        hp.setNoiseCancelling(false);

        int result = dao.insert(hp);
        assertEquals(1, result);
        assertNotNull(hp.getId());
        testId = hp.getId();
        System.out.println("新增成功: " + hp);
    }

    @Test
    public void test02FindAll() throws Exception {
        List<Headphone> list = dao.findAll();
        assertTrue(list.size() >= 8);
        list.forEach(System.out::println);
    }

    @Test
    public void test03FindByLike() throws Exception {
        List<Headphone> list = dao.findByLike("model", "Pro");
        System.out.println("模糊查询 'Pro': " + list.size() + " 条");
        list.forEach(System.out::println);
    }

    @Test
    public void test04Update() throws Exception {
        // 使用已知存在的ID（因为test01可能失败）
        List<Headphone> all = dao.findAll();
        assertTrue("数据库至少有一条记录", all.size() > 0);

        Headphone hp = all.get(0);
        testId = hp.getId(); // 保存用于删除测试

        BigDecimal oldPrice = hp.getPrice();
        hp.setPrice(oldPrice.add(new BigDecimal("100.00")));

        int result = dao.update(hp);
        assertEquals(1, result);

        Headphone updated = dao.findById(hp.getId());
        assertEquals(hp.getPrice(), updated.getPrice());
        System.out.println("更新成功: " + updated);

        // 恢复原价
        hp.setPrice(oldPrice);
        dao.update(hp);
    }

    @Test
    public void test05Delete() throws Exception {
        // 先新增一条用于删除
        Headphone hp = new Headphone();
        hp.setModel("TestModel");
        hp.setBrand("TestBrand");
        hp.setDriverSize(40.0);
        hp.setImpedance(32);
        hp.setSensitivity(100);
        hp.setFrequencyResponse("20Hz-20kHz");
        hp.setPrice(new BigDecimal("999.00"));
        hp.setStock(10);
        hp.setWireless(false);
        hp.setNoiseCancelling(false);

        dao.insert(hp);
        Integer deleteId = hp.getId();
        assertNotNull(deleteId);

        int result = dao.deleteById(deleteId);
        assertEquals(1, result);
        assertNull(dao.findById(deleteId));
        System.out.println("删除成功: ID=" + deleteId);
    }
}