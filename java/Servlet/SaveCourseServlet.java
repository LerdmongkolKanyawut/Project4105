package servlet;

import dao.CourseDAO;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * SaveCourseServlet – POST /SaveCourseServlet (Admin only) รับ JSON body: {
 * courseId, code, name, credit, pr, exam, desc, schedules[], links[] } courseId
 * = null → INSERT, courseId = number → UPDATE
 */
@WebServlet("/SaveCourseServlet")
public class SaveCourseServlet extends HttpServlet {

    private final CourseDAO courseDao = new CourseDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || !"Admin".equals(session.getAttribute("role"))) {
            JsonUtil.sendError(resp, "ไม่มีสิทธิ์");
            return;
        }

        // อ่าน JSON body
        String body;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            body = sb.toString();
        }

        try {
            // parse JSON แบบ simple (ไม่ใช้ library เพิ่ม)
            Integer courseId = parseNullableInt(body, "courseId");
            String code = parseStr(body, "code");
            String name = parseStr(body, "name");
            int credit = parseInt(body, "credit");
            String pr = parseStr(body, "pr");
            String exam = parseStr(body, "exam");
            String desc = parseStr(body, "desc");
            List<String> scheds = parseStrArray(body, "schedules");
            List<String[]> links = parseLinkArray(body);  // [{label,url}, ...]

            if (code.isBlank() || name.isBlank() || credit <= 0) {
                JsonUtil.sendError(resp, "กรุณากรอกข้อมูลให้ครบ");
                return;
            }

            if (courseId == null) {
                courseDao.insert(code, name, credit, pr, exam, desc, scheds, links);
            } else {
                courseDao.update(courseId, code, name, credit, pr, exam, desc, scheds, links);
            }
            JsonUtil.sendSuccess(resp);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    /* ── Simple JSON parsers ── */
    private String parseStr(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return "";
        }
        int colon = json.indexOf(':', idx + pattern.length());
        if (colon < 0) {
            return "";
        }
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) {
            return "";
        }
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) {
            return "";
        }
        return json.substring(q1 + 1, q2)
                .replace("\\n", "\n").replace("\\r", "\r")
                .replace("\\t", "\t").replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private int parseInt(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return 0;
        }
        int colon = json.indexOf(':', idx + pattern.length());
        if (colon < 0) {
            return 0;
        }
        int start = colon + 1;
        while (start < json.length() && (json.charAt(start) == ' ')) {
            start++;
        }
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)))) {
            end++;
        }
        if (start == end) {
            return 0;
        }
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) {
            return 0;
        }
    }

    private Integer parseNullableInt(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return null;
        }
        int colon = json.indexOf(':', idx + pattern.length());
        if (colon < 0) {
            return null;
        }
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ') {
            start++;
        }
        if (json.startsWith("null", start)) {
            return null;
        }
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        if (start == end) {
            return null;
        }
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseStrArray(String json, String key) {
        List<String> list = new ArrayList<>();
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return list;
        }
        int arrStart = json.indexOf('[', idx);
        int arrEnd = json.indexOf(']', arrStart);
        if (arrStart < 0 || arrEnd < 0) {
            return list;
        }
        String arr = json.substring(arrStart + 1, arrEnd);
        int pos = 0;
        while (pos < arr.length()) {
            int q1 = arr.indexOf('"', pos);
            if (q1 < 0) {
                break;
            }
            int q2 = q1 + 1;
            while (q2 < arr.length() && arr.charAt(q2) != '"') {
                if (arr.charAt(q2) == '\\') {
                    q2++;
                }
                q2++;
            }
            list.add(arr.substring(q1 + 1, q2)
                    .replace("\\\"", "\"").replace("\\\\", "\\"));
            pos = q2 + 1;
        }
        return list;
    }

    private List<String[]> parseLinkArray(String json) {
        List<String[]> list = new ArrayList<>();
        int idx = json.indexOf("\"links\"");
        if (idx < 0) {
            return list;
        }
        int arrStart = json.indexOf('[', idx);
        int arrEnd = json.lastIndexOf(']');
        if (arrStart < 0 || arrEnd < 0 || arrEnd <= arrStart) {
            return list;
        }
        String arr = json.substring(arrStart + 1, arrEnd);
        // แต่ละ element คือ {"label":"...","url":"..."}
        int pos = 0;
        while (pos < arr.length()) {
            int objStart = arr.indexOf('{', pos);
            int objEnd = arr.indexOf('}', objStart);
            if (objStart < 0 || objEnd < 0) {
                break;
            }
            String obj = arr.substring(objStart, objEnd + 1);
            String label = parseStr(obj, "label");
            String url = parseStr(obj, "url");
            if (!label.isBlank() && !url.isBlank()) {
                list.add(new String[]{label, url});
            }
            pos = objEnd + 1;
        }
        return list;
    }
}
