package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil – จัดการ Connection กับ MySQL Schema: project_4105
 */
public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/project_4105"
                                 + "?useSSL=false&allowPublicKeyRetrieval=true"
                                 + "&serverTimezone=Asia/Bangkok"
                                 + "&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "Lerdmongkol_24998"; // เปลี่ยนตามการตั้งค่าจริง

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * คืน Connection ใหม่ทุกครั้ง (ควรปิดใน try-with-resources)
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * ปิด AutoCloseable หลายตัวโดยไม่ throw exception
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
