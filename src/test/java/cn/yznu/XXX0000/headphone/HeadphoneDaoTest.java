package cn.yznu.XXX0000.headphone;

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

        // 先查询一个已知存在的ID（取第一条数据）
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
                    hp.getId(), hp.getBrand(), hp.getModel(),
                    hp.getPrice(), hp.getStock());
        }
    }

    @Test
    public void test04FindByModel() throws Exception {
        System.out.println("测试根据型号模糊查询");

        // 测试模糊查询 "Pro"
        String keyword = "Pro";
        List<Headphone> list = headphoneDao.findByModel(keyword);

        assertNotNull(list);
        System.out.println("模糊查询 '" + keyword + "' 的结果 (" + list.size() + " 条):");
        for (Headphone hp : list) {
            System.out.println("  " + hp.getBrand() + " " + hp.getModel());
            assertTrue("型号应包含关键词",
                    hp.getModel().toLowerCase().contains(keyword.toLowerCase()));
        }

        // 测试模糊查询 "HD"
        keyword = "HD";
        list = headphoneDao.findByModel(keyword);
        System.out.println("\n模糊查询 '" + keyword + "' 的结果 (" + list.size() + " 条):");
        for (Headphone hp : list) {
            System.out.println("  " + hp.getBrand() + " " + hp.getModel());
        }
    }

    @Test
    public void test05FindByBrand() throws Exception {
        System.out.println("测试根据品牌查询");

        String brand = "Sony";
        List<Headphone> list = headphoneDao.findByBrand(brand);

        assertNotNull(list);
        System.out.println("品牌 '" + brand + "' 的耳机 (" + list.size() + " 条):");
        for (Headphone hp : list) {
            System.out.println("  " + hp.getModel() + " - ¥" + hp.getPrice());
            assertEquals(brand, hp.getBrand());
        }
    }

    @Test
    public void test06Update() throws Exception {
        System.out.println("测试更新耳机信息");

        // 先查询一条记录
        List<Headphone> all = headphoneDao.findAll();
        Headphone hp = all.get(0);

        System.out.println("更新前: " + hp);

        // 修改价格和库存
        BigDecimal oldPrice = hp.getPrice();
        Integer oldStock = hp.getStock();

        hp.setPrice(oldPrice.add(new BigDecimal("100.00")));
        hp.setStock(oldStock + 10);

        int result = headphoneDao.update(hp);
        assertEquals(1, result);

        // 重新查询验证
        Headphone updated = headphoneDao.findById(hp.getId());
        System.out.println("更新后: " + updated);

        assertEquals(hp.getPrice(), updated.getPrice());
        assertEquals(hp.getStock(), updated.getStock());

        // 恢复原值
        hp.setPrice(oldPrice);
        hp.setStock(oldStock);
        headphoneDao.update(hp);
    }

    @Test
    public void test07Delete() throws Exception {
        System.out.println("测试删除耳机");

        // 使用test01Insert中新增的记录ID
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