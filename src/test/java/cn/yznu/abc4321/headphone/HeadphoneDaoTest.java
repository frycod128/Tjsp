package cn.yznu.abc4321.headphone;

import cn.yznu.abc4321.headphone.dao.HeadphoneDao;
import cn.yznu.abc4321.headphone.dao.impl.HeadphoneDaoImpl;
import cn.yznu.abc4321.headphone.entity.Headphone;
import org.junit.*;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;

public class HeadphoneDaoTest {
    private static HeadphoneDao headphoneDao;
    private static Integer testId;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        headphoneDao = new HeadphoneDaoImpl();
        System.out.println("===== 开始测试 HeadphoneDao =====");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println("===== HeadphoneDao 测试完成 =====");
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("\n--- 执行测试方法 ---");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("--- 测试方法执行完毕 ---");
    }

    @Test
    public void test01Insert() throws Exception {
        System.out.println("测试新增耳机");
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

        int result = headphoneDao.insert(hp);
        assertEquals(1, result);
        assertNotNull(hp.getId());
        testId = hp.getId();
        System.out.println("新增成功，ID: " + testId + ", 型号: " + hp.getModel());
    }

    @Test
    public void test02FindById() throws Exception {
        System.out.println("测试根据ID查询耳机");
        List<Headphone> all = headphoneDao.findAll();
        assertTrue("数据库至少有一条记录", all.size() > 0);

        Integer id = all.get(0).getId();
        Headphone hp = headphoneDao.findById(id);
        assertNotNull(hp);
        assertEquals(id, hp.getId());
        System.out.println("查询到: " + hp);
    }

    @Test
    public void test03FindAll() throws Exception {
        System.out.println("测试查询全部耳机");
        List<Headphone> list = headphoneDao.findAll();
        assertNotNull(list);
        assertTrue("查询结果应不少于8条", list.size() >= 8);
        System.out.println("共查询到 " + list.size() + " 条记录:");
        for (Headphone hp : list) {
            System.out.printf("  [%d] %s %s - ¥%.2f (库存:%d)%n",
                    hp.getId(), hp.getBrand(), hp.getModel(), hp.getPrice(), hp.getStock());
        }
    }

    @Test
    public void test04FindByModel() throws Exception {
        System.out.println("测试根据型号模糊查询");
        String keyword = "Pro";
        List<Headphone> list = headphoneDao.findByModel(keyword);
        assertNotNull(list);
        System.out.println("模糊查询 '" + keyword + "' 的结果 (" + list.size() + " 条):");
        for (Headphone hp : list) {
            System.out.println("  " + hp.getBrand() + " " + hp.getModel());
            assertTrue("型号应包含关键词", hp.getModel().toLowerCase().contains(keyword.toLowerCase()));
        }

        keyword = "HD";
        list = headphoneDao.findByModel(keyword);
        System.out.println("\n模糊查询 '" + keyword + "' 的结果 (" + list.size() + " 条):");
        for (Headphone hp : list) {
            System.out.println("  " + hp.getBrand() + " " + hp.getModel());
        }
    }

    @Test
    public void test05FindByPage() throws Exception {
        System.out.println("测试分页查询");
        int pageSize = 3;
        int totalCount = headphoneDao.getTotalCount();
        System.out.println("总记录数: " + totalCount);

        List<Headphone> page1 = headphoneDao.findByPage(0, pageSize);
        System.out.println("第1页 (" + page1.size() + " 条):");
        for (Headphone hp : page1) {
            System.out.println("  " + hp.getId() + " - " + hp.getBrand() + " " + hp.getModel());
        }
        assertEquals(pageSize, page1.size());

        List<Headphone> page2 = headphoneDao.findByPage(pageSize, pageSize);
        System.out.println("第2页 (" + page2.size() + " 条):");
        for (Headphone hp : page2) {
            System.out.println("  " + hp.getId() + " - " + hp.getBrand() + " " + hp.getModel());
        }
        assertTrue(page2.size() > 0);
    }

    @Test
    public void test06FindByModelWithPage() throws Exception {
        System.out.println("测试分页模糊查询");
        String keyword = "0";
        int pageSize = 2;
        int totalCount = headphoneDao.getTotalCountByModel(keyword);
        System.out.println("模糊查询 '" + keyword + "' 总记录数: " + totalCount);

        List<Headphone> page1 = headphoneDao.findByModelWithPage(keyword, 0, pageSize);
        System.out.println("第1页 (" + page1.size() + " 条):");
        for (Headphone hp : page1) {
            System.out.println("  " + hp.getBrand() + " " + hp.getModel());
        }
        assertNotNull(page1);
    }

    @Test
    public void test07Update() throws Exception {
        System.out.println("测试更新耳机信息");
        List<Headphone> all = headphoneDao.findAll();
        Headphone hp = all.get(0);
        System.out.println("更新前: " + hp);

        BigDecimal oldPrice = hp.getPrice();
        Integer oldStock = hp.getStock();
        hp.setPrice(oldPrice.add(new BigDecimal("50.00")));
        hp.setStock(oldStock + 5);

        int result = headphoneDao.update(hp);
        assertEquals(1, result);

        Headphone updated = headphoneDao.findById(hp.getId());
        System.out.println("更新后: " + updated);
        assertEquals(hp.getPrice(), updated.getPrice());
        assertEquals(hp.getStock(), updated.getStock());

        hp.setPrice(oldPrice);
        hp.setStock(oldStock);
        headphoneDao.update(hp);
    }

    @Test
    public void test08Delete() throws Exception {
        System.out.println("测试删除耳机");
        if (testId != null) {
            Headphone beforeDelete = headphoneDao.findById(testId);
            assertNotNull("要删除的记录应存在", beforeDelete);
            System.out.println("准备删除: " + beforeDelete);

            int result = headphoneDao.deleteById(testId);
            assertEquals(1, result);

            Headphone afterDelete = headphoneDao.findById(testId);
            assertNull("删除后查询应返回null", afterDelete);
            System.out.println("删除成功");
        } else {
            System.out.println("没有需要删除的测试记录");
        }
    }
}