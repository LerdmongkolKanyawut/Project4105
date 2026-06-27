package dao;

import model.Entry;
import model.FileRecord;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EntryDao – CRUD สำหรับตาราง entries และ files
 */
public class EntryDAO {

    /**
     * ดึง entry ทั้งหมด (list view)
     */
    public List<Entry> findAll() throws SQLException {
        String sql = "SELECT e.entry_id, e.user_id, e.title, e.subject,"
                + " e.uploaded_date, CONCAT(u.firstname,' ',u.lastname) AS author"
                + " FROM entries e JOIN users u ON e.user_id=u.user_id"
                + " ORDER BY e.created_at DESC";
        List<Entry> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Entry e = new Entry();
                e.setEntryId(rs.getInt("entry_id"));
                e.setUserId(rs.getInt("user_id"));
                e.setTitle(rs.getString("title"));
                e.setSubject(rs.getString("subject"));
                e.setUploadDate(rs.getString("uploaded_date"));
                e.setAuthorName(rs.getString("author"));
                list.add(e);
            }
        }
        return list;
    }

    /**
     * ดึง entry เดียวพร้อม description (detail view)
     */
    public Entry findById(int entryId) throws SQLException {
        String sql = "SELECT e.*, CONCAT(u.firstname,' ',u.lastname) AS author"
                + " FROM entries e JOIN users u ON e.user_id=u.user_id"
                + " WHERE e.entry_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Entry e = new Entry();
                    e.setEntryId(rs.getInt("entry_id"));
                    e.setUserId(rs.getInt("user_id"));
                    e.setTitle(rs.getString("title"));
                    e.setSubject(rs.getString("subject"));
                    e.setDescription(rs.getString("description"));
                    e.setUploadDate(rs.getString("uploaded_date"));
                    e.setAuthorName(rs.getString("author"));
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * ดึง entries ของผู้ใช้คนเดียว (ประวัติ Upload)
     */
    public List<Entry> findByUserId(int userId) throws SQLException {
        String sql = "SELECT e.entry_id, e.title, e.subject, e.uploaded_date,"
                + " (SELECT COUNT(*) FROM files f WHERE f.entry_id=e.entry_id) AS file_count"
                + " FROM entries e WHERE e.user_id=?"
                + " ORDER BY e.created_at DESC";
        List<Entry> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entry e = new Entry();
                    e.setEntryId(rs.getInt("entry_id"));
                    e.setTitle(rs.getString("title"));
                    e.setSubject(rs.getString("subject"));
                    e.setUploadDate(rs.getString("uploaded_date"));
                    // fileCount เก็บชั่วคราวใน description field เพื่อส่งไป Servlet
                    e.setDescription(rs.getString("file_count"));
                    list.add(e);
                }
            }
        }
        return list;
    }

    /**
     * บันทึก entry ใหม่ คืน entryId ที่สร้าง
     *
     * @return entryId ที่ได้ หรือ -1 ถ้าล้มเหลว
     */
    public int insert(Entry entry) throws SQLException {
        String sql = "INSERT INTO entries (user_id, title, subject, description, uploaded_date)"
                + " VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getUserId());
            ps.setString(2, entry.getTitle());
            ps.setString(3, entry.getSubject());
            ps.setString(4, entry.getDescription());
            ps.setString(5, entry.getUploadDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    // ── Files ────────────────────────────────────────
    /**
     * บันทึกข้อมูลไฟล์
     */
    public void insertFile(FileRecord file) throws SQLException {
        String sql = "INSERT INTO files (entry_id, file_name, file_path, file_type, file_size)"
                + " VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, file.getEntryId());
            ps.setString(2, file.getFileName());
            ps.setString(3, file.getFilePath());
            ps.setString(4, file.getFileType());
            ps.setLong(5, file.getFileSize());
            ps.executeUpdate();
        }
    }

    /**
     * ดึงไฟล์ทั้งหมดของ entry
     */
    public List<FileRecord> findFilesByEntryId(int entryId) throws SQLException {
        String sql = "SELECT * FROM files WHERE entry_id=? ORDER BY uploaded_at";
        List<FileRecord> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FileRecord f = new FileRecord();
                    f.setFileId(rs.getInt("file_id"));
                    f.setEntryId(rs.getInt("entry_id"));
                    f.setFileName(rs.getString("file_name"));
                    f.setFilePath(rs.getString("file_path"));
                    f.setFileType(rs.getString("file_type"));
                    f.setFileSize(rs.getLong("file_size"));
                    list.add(f);
                }
            }
        }
        return list;
    }

    /**
     * ดึงไฟล์จาก fileId (สำหรับ Download)
     */
    public FileRecord findFileById(int fileId) throws SQLException {
        String sql = "SELECT * FROM files WHERE file_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fileId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FileRecord f = new FileRecord();
                    f.setFileId(rs.getInt("file_id"));
                    f.setEntryId(rs.getInt("entry_id"));
                    f.setFileName(rs.getString("file_name"));
                    f.setFilePath(rs.getString("file_path"));
                    f.setFileType(rs.getString("file_type"));
                    f.setFileSize(rs.getLong("file_size"));
                    return f;
                }
            }
        }
        return null;
    }
}
