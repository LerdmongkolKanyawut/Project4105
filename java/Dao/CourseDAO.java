package dao;

import model.Course;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDao – ดึงข้อมูลตาราง courses + course_schedules + course_links
 */
public class CourseDAO {

    /**
     * ดึงวิชาทั้งหมดพร้อม schedules และ links
     */
    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();

        String sqlCourses = "SELECT * FROM courses ORDER BY code";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlCourses); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCode(rs.getString("code"));
                c.setName(rs.getString("name"));
                c.setCredits(rs.getInt("credit"));           // DB ใช้ "credit"
                c.setPrerequisite(rs.getString("pre_requiste")); // DB ใช้ "pre_requiste"
                c.setDescription(rs.getString("description"));
                c.setExamNote(rs.getString("exam_note"));
                c.setSchedules(new ArrayList<>());
                c.setLinks(new ArrayList<>());
                courses.add(c);
                System.out.println("[CourseDao] loaded: " + c.getCode()); // ← เพิ่มบรรทัดนี้
            }

        } catch (SQLException e) {
            System.out.println("[CourseDao] ERROR: " + e.getMessage()); // ← เพิ่มบรรทัดนี้
            throw e;
        }

        if (courses.isEmpty()) {
            return courses;
        }

        // 2) ดึง schedules ทั้งหมดแล้ว match กับ course
        String sqlSched = "SELECT course_id, section, day_of_week,"
                + " TIME_FORMAT(time_start,'%H:%i') AS ts,"
                + " TIME_FORMAT(time_end,'%H:%i') AS te, room"
                + " FROM course_schedules ORDER BY course_id";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlSched); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cid = rs.getInt("course_id");
                String text = buildScheduleText(
                        rs.getString("section"), rs.getString("day_of_week"),
                        rs.getString("ts"), rs.getString("te"), rs.getString("room"));
                courses.stream().filter(c -> c.getCourseId() == cid)
                        .findFirst().ifPresent(c -> c.getSchedules().add(text));
            }
        }

        // 3) ดึง links ทั้งหมด
        String sqlLinks = "SELECT course_id, label, url FROM course_links ORDER BY course_id";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlLinks); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cid = rs.getInt("course_id");
                Course.CourseLink lk = new Course.CourseLink(
                        rs.getString("label"), rs.getString("url"));
                courses.stream().filter(c -> c.getCourseId() == cid)
                        .findFirst().ifPresent(c -> c.getLinks().add(lk));
            }
        }

        return courses;
    }

    private String buildScheduleText(String section, String day, String start, String end, String room) {
        StringBuilder sb = new StringBuilder();
        if (section != null && !section.isEmpty()) {
            sb.append(section).append(" ");
        }
        sb.append(day);
        // แสดงเวลาเฉพาะเมื่อไม่ใช่ 00:00
        boolean hasTime = start != null && end != null
                && !start.equals("00:00") && !end.equals("00:00");
        if (hasTime) {
            sb.append(" ").append(start).append("-").append(end);
        }
        if (room != null && !room.isEmpty()) {
            sb.append(" ").append(room);
        }
        return sb.toString();
    }

    /* ── INSERT ─────────────────────────────────────────────── */
    public void insert(String code, String name, int credit, String pr,
            String exam, String desc,
            java.util.List<String> schedules,
            java.util.List<String[]> links) throws SQLException {

        String sql = "INSERT INTO courses (code, name, credit, pre_requiste, exam_note, description)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        int courseId;
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql,
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, credit);
            ps.setString(4, pr.isBlank() ? null : pr);
            ps.setString(5, exam.isBlank() ? null : exam);
            ps.setString(6, desc.isBlank() ? null : desc);
            ps.executeUpdate();
            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("ไม่ได้รับ generated key");
                }
                courseId = keys.getInt(1);
            }
        }
        insertSchedules(courseId, schedules);
        insertLinks(courseId, links);
    }

    /* ── UPDATE ─────────────────────────────────────────────── */
    public void update(int courseId, String code, String name, int credit, String pr,
            String exam, String desc,
            java.util.List<String> schedules,
            java.util.List<String[]> links) throws SQLException {

        String sql = "UPDATE courses SET code=?, name=?, credit=?, pre_requiste=?,"
                + " exam_note=?, description=? WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, credit);
            ps.setString(4, pr.isBlank() ? null : pr);
            ps.setString(5, exam.isBlank() ? null : exam);
            ps.setString(6, desc.isBlank() ? null : desc);
            ps.setInt(7, courseId);
            ps.executeUpdate();
        }
        // ลบของเดิมแล้วใส่ใหม่
        deleteSchedules(courseId);
        deleteLinks(courseId);
        insertSchedules(courseId, schedules);
        insertLinks(courseId, links);
    }

    /* ── DELETE ─────────────────────────────────────────────── */
    public void delete(int courseId) throws SQLException {
        // course_schedules และ course_links มี ON DELETE CASCADE อยู่แล้ว
        // ถ้าไม่มีให้ลบก่อน
        deleteSchedules(courseId);
        deleteLinks(courseId);
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    /* ── helpers ─────────────────────────────────────────────── */
    private void insertSchedules(int courseId, java.util.List<String> schedules) throws SQLException {
        if (schedules == null || schedules.isEmpty()) {
            return;
        }
        // string format: "Sec.1 วันอังคาร 11:30-13:20 SCL209"
        // parse แต่ละส่วนก่อน INSERT
        String sql = "INSERT INTO course_schedules (course_id, section, day_of_week, time_start, time_end, room)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String s : schedules) {
                String[] parts = s.trim().split("\\s+");
                String section = "";
                String day = "";
                String timeStart = "00:00";
                String timeEnd = "00:00";
                String room = "";
                for (String p : parts) {
                    if (p.matches(".*วัน.*")) {
                        day = p;
                    } else if (p.matches("\\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}")) {
                        String[] t = p.split("-");
                        timeStart = t[0];
                        timeEnd = t[1];
                    } else if (p.matches("(?i)(Lec|Lab|Sec).*")) {
                        section = p;
                    } else if (!p.isEmpty()) {
                        room = p;
                    }
                }
                ps.setInt(1, courseId);
                ps.setString(2, section.isEmpty() ? null : section);
                ps.setString(3, day.isEmpty() ? s : day);   // fallback เก็บ string เต็ม
                ps.setString(4, timeStart);
                ps.setString(5, timeEnd);
                ps.setString(6, room.isEmpty() ? null : room);
                ps.executeUpdate();
            }
        }
    }

    private void insertLinks(int courseId, java.util.List<String[]> links) throws SQLException {
        if (links == null || links.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO course_links (course_id, label, url) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] lk : links) {
                ps.setInt(1, courseId);
                ps.setString(2, lk[0]);
                ps.setString(3, lk[1]);
                ps.executeUpdate();
            }
        }
    }

    private void deleteSchedules(int courseId) throws SQLException {
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM course_schedules WHERE course_id=?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    private void deleteLinks(int courseId) throws SQLException {
        try (Connection conn = DBUtil.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM course_links WHERE course_id=?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }
}