package cn.yznu.abc4321.headphone.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            loadConfig();
        } catch (Exception e) {
            throw new RuntimeException("数据库配置加载失败", e);
        }
    }

    private static void loadConfig() throws Exception {
        try (InputStream is = DBUtil.class.getClassLoader()
                .getResourceAsStream("db-config.json");
             InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            driver = json.get("driver").getAsString();
            url = json.get("url").getAsString();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();
            Class.forName(driver);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}