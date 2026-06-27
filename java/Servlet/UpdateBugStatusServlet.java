package servlet;

import dao.BugReportDAO;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

/**
 * UpdateBugStatusServlet – POST /UpdateBugStatusServlet (Admin only) รับ:
 * reportId, status (pending | in_progress | resolved)
 */
@WebServlet("/UpdateBugStatusServlet")
public class UpdateBugStatusServlet extends HttpServlet {

    private static final Set<String> VALID_STATUS
            = Set.of("pending", "in_progress", "resolved");

    private final BugReportDAO bugDao = new BugReportDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || !"Admin".equals(session.getAttribute("role"))) {
            JsonUtil.sendError(resp, "ไม่มีสิทธิ์");
            return;
        }

        String reportIdParam = req.getParameter("reportId");
        String status = req.getParameter("status");

        if (reportIdParam == null || status == null || !VALID_STATUS.contains(status)) {
            JsonUtil.sendError(resp, "ข้อมูลไม่ถูกต้อง");
            return;
        }

        try {
            int reportId = Integer.parseInt(reportIdParam.trim());
            bugDao.updateStatus(reportId, status);
            JsonUtil.sendSuccess(resp);
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, "reportId ไม่ถูกต้อง");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }
}
