package cn.yznu.XXX0000.headphone;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HeadphoneDaoImpl implements HeadphoneDao {
    private String driver;
    private String url;
    private String username;
    private String password;

    public HeadphoneDaoImpl() throws Exception {
        loadConfig();
    }

    /**
     * 从JSON配置文件加载数据库连接参数
     */
    private void loadConfig() throws Exception {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db-config.json");
             InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            this.driver = json.get("driver").getAsString();
            this.url = json.get("url").getAsString();
            this.username = json.get("username").getAsString();
            this.password = json.get("password").getAsString();

            Class.forName(driver);
        }
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public int insert(Headphone hp) throws Exception {
        String sql = "INSERT INTO headphone(model, brand, driver_size, impedance, " +
                "sensitivity, frequency_response, price, stock, wireless, noise_cancelling) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, hp.getModel());
            pstmt.setString(2, hp.getBrand());
            pstmt.setDouble(3, hp.getDriverSize());
            pstmt.setObject(4, hp.getImpedance());
            pstmt.setObject(5, hp.getSensitivity());
            pstmt.setString(6, hp.getFrequencyResponse());
            pstmt.setBigDecimal(7, hp.getPrice());
            pstmt.setInt(8, hp.getStock());
            pstmt.setInt(9, hp.getWireless() ? 1 : 0);
            pstmt.setInt(10, hp.getNoiseCancelling() ? 1 : 0);

            int result = pstmt.executeUpdate();

            // 获取自增ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    hp.setId(rs.getInt(1));
                }
            }
            return result;
        }
    }

    @Override
    public int deleteById(Integer id) throws Exception {
        String sql = "DELETE FROM headphone WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public int update(Headphone hp) throws Exception {
        String sql = "UPDATE headphone SET model=?, brand=?, driver_size=?, impedance=?, " +
                "sensitivity=?, frequency_response=?, price=?, stock=?, " +
                "wireless=?, noise_cancelling=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hp.getModel());
            pstmt.setString(2, hp.getBrand());
            pstmt.setDouble(3, hp.getDriverSize());
            pstmt.setObject(4, hp.getImpedance());
            pstmt.setObject(5, hp.getSensitivity());
            pstmt.setString(6, hp.getFrequencyResponse());
            pstmt.setBigDecimal(7, hp.getPrice());
            pstmt.setInt(8, hp.getStock());
            pstmt.setInt(9, hp.getWireless() ? 1 : 0);
            pstmt.setInt(10, hp.getNoiseCancelling() ? 1 : 0);
            pstmt.setInt(11, hp.getId());

            return pstmt.executeUpdate();
        }
    }

    @Override
    public Headphone findById(Integer id) throws Exception {
        String sql = "SELECT * FROM headphone WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHeadphone(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Headphone> findAll() throws Exception {
        String sql = "SELECT * FROM headphone ORDER BY id";
        List<Headphone> list = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToHeadphone(rs));
            }
        }
        return list;
    }

    @Override
    public List<Headphone> findByModel(String keyword) throws Exception {
        String sql = "SELECT * FROM headphone WHERE model LIKE ? ORDER BY id";
        List<Headphone> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHeadphone(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Headphone> findByBrand(String brand) throws Exception {
        String sql = "SELECT * FROM headphone WHERE brand = ? ORDER BY model";
        List<Headphone> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brand);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHeadphone(rs));
                }
            }
        }
        return list;
    }

    /**
     * 将ResultSet映射为Headphone对象
     */
    private Headphone mapResultSetToHeadphone(ResultSet rs) throws SQLException {
        Headphone hp = new Headphone();
        hp.setId(rs.getInt("id"));
        hp.setModel(rs.getString("model"));
        hp.setBrand(rs.getString("brand"));
        hp.setDriverSize(rs.getDouble("driver_size"));

        Object impedance = rs.getObject("impedance");
        hp.setImpedance(impedance != null ? ((Number) impedance).intValue() : null);

        Object sensitivity = rs.getObject("sensitivity");
        hp.setSensitivity(sensitivity != null ? ((Number) sensitivity).intValue() : null);

        hp.setFrequencyResponse(rs.getString("frequency_response"));
        hp.setPrice(rs.getBigDecimal("price"));
        hp.setStock(rs.getInt("stock"));
        hp.setWireless(rs.getInt("wireless") == 1);
        hp.setNoiseCancelling(rs.getInt("noise_cancelling") == 1);
        hp.setCreateTime(rs.getTimestamp("create_time"));

        return hp;
    }
}