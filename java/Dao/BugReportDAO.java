package dao;

import model.BugReport;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BugReportDao – INSERT bug_reports
 */
public class BugReportDAO {

    /**
     * ดึง bug reports ทั้งหมด (สำหรับ Admin)
     */
    public List<BugReport> findAll() throws SQLException {
        String sql = "SELECT * FROM bug_reports ORDER BY report_id DESC";
        List<BugReport> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BugReport b = new BugReport();
                b.setReportId(rs.getInt("report_id"));
                b.setSubject(rs.getString("subject"));
                b.setDetail(rs.getString("detail"));
                b.setReporterName(rs.getString("reporter_name"));
                b.setReporterEmail(rs.getString("reporter_email"));
                b.setStatus(rs.getString("status") != null ? rs.getString("status") : "pending");
                int uid = rs.getInt("user_id");
                b.setUserId(rs.wasNull() ? null : uid);
                list.add(b);
            }
        }
        return list;
    }

    /**
     * อัปเดตสถานะ bug report
     */
    public boolean updateStatus(int reportId, String status) throws SQLException {
        String sql = "UPDATE bug_reports SET status=? WHERE report_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * บันทึก bug report ใหม่ คืน true ถ้าสำเร็จ
     */
    public boolean insert(BugReport report) throws SQLException {
        String sql = "INSERT INTO bug_reports (subject, detail, reporter_name, reporter_email, user_id, status)"
                + " VALUES (?, ?, ?, ?, ?, 'pending')";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, report.getSubject());
            ps.setString(2, report.getDetail());
            ps.setString(3, report.getReporterName());
            ps.setString(4, report.getReporterEmail());
            if (report.getUserId() != null) {
                ps.setInt(5, report.getUserId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        }
    }
}
