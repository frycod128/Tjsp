package cn.yznu.abc4321.headphone.dao.impl;

import cn.yznu.abc4321.headphone.dao.HeadphoneDao;
import cn.yznu.abc4321.headphone.entity.Headphone;
import cn.yznu.abc4321.headphone.util.DBUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HeadphoneDaoImpl implements HeadphoneDao {

    @Override
    public int insert(Headphone hp) throws Exception {
        String sql = "INSERT INTO headphone(model, brand, driver_size, impedance, " +
                "sensitivity, frequency_response, price, stock, wireless, noise_cancelling) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setHeadphoneParams(pstmt, hp);
            int result = pstmt.executeUpdate();
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
        try (Connection conn = DBUtil.getConnection();
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
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setHeadphoneParams(pstmt, hp);
            pstmt.setInt(11, hp.getId());
            return pstmt.executeUpdate();
        }
    }

    @Override
    public Headphone findById(Integer id) throws Exception {
        String sql = "SELECT * FROM headphone WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
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
        try (Connection conn = DBUtil.getConnection();
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
        try (Connection conn = DBUtil.getConnection();
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
    public List<Headphone> findByPage(int start, int pageSize) throws Exception {
        String sql = "SELECT * FROM headphone ORDER BY id LIMIT ?, ?";
        List<Headphone> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, start);
            pstmt.setInt(2, pageSize);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHeadphone(rs));
                }
            }
        }
        return list;
    }

    @Override
    public int getTotalCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM headphone";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Headphone> findByModelWithPage(String keyword, int start, int pageSize) throws Exception {
        String sql = "SELECT * FROM headphone WHERE model LIKE ? ORDER BY id LIMIT ?, ?";
        List<Headphone> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setInt(2, start);
            pstmt.setInt(3, pageSize);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHeadphone(rs));
                }
            }
        }
        return list;
    }

    @Override
    public int getTotalCountByModel(String keyword) throws Exception {
        String sql = "SELECT COUNT(*) FROM headphone WHERE model LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private void setHeadphoneParams(PreparedStatement pstmt, Headphone hp) throws SQLException {
        pstmt.setString(1, hp.getModel());
        pstmt.setString(2, hp.getBrand());
        pstmt.setDouble(3, hp.getDriverSize());
        pstmt.setObject(4, hp.getImpedance());
        pstmt.setObject(5, hp.getSensitivity());
        pstmt.setString(6, hp.getFrequencyResponse());
        pstmt.setBigDecimal(7, hp.getPrice());
        pstmt.setInt(8, hp.getStock());
        pstmt.setInt(9, hp.getWireless() != null && hp.getWireless() ? 1 : 0);
        pstmt.setInt(10, hp.getNoiseCancelling() != null && hp.getNoiseCancelling() ? 1 : 0);
    }

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