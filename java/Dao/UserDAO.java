package dao;

import model.User;
import util.DBUtil;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDao – CRUD สำหรับตาราง users
 */
public class UserDAO {

    /**
     * ค้นหาผู้ใช้จาก username (สำหรับ Login)
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * ค้นหาผู้ใช้จาก email (สำหรับ Reset Password)
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * ค้นหาผู้ใช้จาก userId
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * สมัครสมาชิกใหม่ คืน true ถ้าสำเร็จ
     */
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, firstname, lastname, email, role)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getLastname());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRole());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * ตรวจว่า username ซ้ำหรือไม่
     */
    public boolean existsUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * ตรวจว่า email ซ้ำหรือไม่
     */
    public boolean existsEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * แก้ไขข้อมูลส่วนตัว (firstname, lastname, email)
     */
    public boolean updateProfile(int userId, String firstname, String lastname, String email)
            throws SQLException {
        String sql = "UPDATE users SET firstname=?, lastname=?, email=? WHERE user_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, email);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * เปลี่ยนรหัสผ่าน
     */
    public boolean updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE user_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * อัปเดต avatar
     */
    public boolean updateAvatar(int userId, String avatarData) throws SQLException {
        String sql = "UPDATE users SET avatar=? WHERE user_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatarData);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * ดึงผู้ใช้ทั้งหมด (สำหรับ Admin)
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── mapper ──────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFirstname(rs.getString("firstname"));
        u.setLastname(rs.getString("lastname"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        u.setAvatar(rs.getString("avatar"));
        return u;
    }
}
