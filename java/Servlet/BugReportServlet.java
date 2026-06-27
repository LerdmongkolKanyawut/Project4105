package servlet;

import dao.BugReportDAO;
import model.BugReport;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * BugReportServlet – POST /BugReportServlet รับ: subject*, detail*, name, email
 * คืน JSON: { "success":true } | { "success":false, "message":"..." }
 */
@WebServlet("/BugReportServlet")
public class BugReportServlet extends HttpServlet {

    private final BugReportDAO bugReportDao = new BugReportDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        String subject = req.getParameter("subject");
        String detail = req.getParameter("detail");
        String name = req.getParameter("name");
        String email = req.getParameter("email");

        if (subject == null || subject.isBlank()
                || detail == null || detail.isBlank()) {
            JsonUtil.sendError(resp, "กรุณากรอกหัวข้อและรายละเอียด");
            return;
        }

        // ดึง userId จาก session ถ้า login อยู่
        Integer userId = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            userId = (Integer) session.getAttribute("userId");
        }

        try {
            BugReport report = new BugReport();
            report.setSubject(subject.trim());
            report.setDetail(detail.trim());
            report.setReporterName(name != null ? name.trim() : null);
            report.setReporterEmail(email != null ? email.trim() : null);
            report.setUserId(userId);

            bugReportDao.insert(report);
            JsonUtil.sendSuccess(resp);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ กรุณาลองใหม่");
        }
    }
}
