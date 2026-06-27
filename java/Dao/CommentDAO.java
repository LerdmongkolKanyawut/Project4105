package dao;

import model.Comment;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CommentDao – CRUD สำหรับตาราง comments
 */
public class CommentDAO {

    /**
     * ดึง comments ทั้งหมดของ entry
     */
    public List<Comment> findByEntryId(int entryId) throws SQLException {
        String sql = "SELECT c.comment_id, c.entry_id, c.user_id, c.comment,"
                + " DATE_FORMAT(c.created_at,'%d/%m/%Y %H:%i') AS created_at,"
                + " CONCAT(u.firstname,' ',u.lastname) AS author"
                + " FROM comments c JOIN users u ON c.user_id=u.user_id"
                + " WHERE c.entry_id=? ORDER BY c.created_at";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setCommentId(rs.getInt("comment_id"));
                    c.setEntryId(rs.getInt("entry_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setComment(rs.getString("comment"));
                    c.setCreatedAt(rs.getString("created_at"));
                    c.setAuthorName(rs.getString("author"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    /**
     * เพิ่ม comment ใหม่
     */
    public boolean insert(int entryId, int userId, String comment) throws SQLException {
        String sql = "INSERT INTO comments (entry_id, user_id, comment) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.setInt(2, userId);
            ps.setString(3, comment);
            return ps.executeUpdate() > 0;
        }
    }
}
