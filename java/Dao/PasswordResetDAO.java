package dao;

import util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * PasswordResetDao – จัดการ password_reset_tokens
 */
public class PasswordResetDAO {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** สร้าง token ใหม่ (อายุ 1 ชั่วโมง) คืน token string */
    public String createToken(int userId) throws SQLException {
        // ลบ token เก่าของ user นี้ก่อน
        deleteByUserId(userId);

        String token = UUID.randomUUID().toString();
        String expires = LocalDateTime.now().plusHours(1).format(FMT);

        String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at)"
                   + " VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setString(3, expires);
            ps.executeUpdate();
        }
        return token;
    }

    /**
     * ตรวจ token ว่ายังใช้ได้หรือไม่
     * คืน userId ถ้าถูกต้อง หรือ -1 ถ้าไม่ถูกต้อง/หมดอายุ
     */
    public int validateToken(String token) throws SQLException {
        String sql = "SELECT user_id FROM password_reset_tokens"
                   + " WHERE token=? AND used=0 AND expires_at > NOW()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        }
        return -1;
    }

    /** ทำเครื่องหมายว่าใช้แล้ว */
    public void markUsed(String token) throws SQLException {
        String sql = "UPDATE password_reset_tokens SET used=1 WHERE token=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }

    private void deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}