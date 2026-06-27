package servlet;

import dao.BugReportDAO;
import model.BugReport;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * GetBugReportsServlet – GET /GetBugReportsServlet (Admin only)
 */
@WebServlet("/GetBugReportsServlet")
public class GetBugReportsServlet extends HttpServlet {

    private final BugReportDAO bugDao = new BugReportDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null
                || !"Admin".equals(session.getAttribute("role"))) {
            JsonUtil.send(resp, "[]");
            return;
        }
        try {
            List<BugReport> reports = bugDao.findAll();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < reports.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                BugReport b = reports.get(i);
                sb.append("{")
                        .append("\"reportId\":").append(b.getReportId()).append(",")
                        .append("\"subject\":").append(JsonUtil.escapeJson(b.getSubject())).append(",")
                        .append("\"detail\":").append(JsonUtil.escapeJson(b.getDetail())).append(",")
                        .append("\"reporterName\":").append(JsonUtil.escapeJson(b.getReporterName())).append(",")
                        .append("\"reporterEmail\":").append(JsonUtil.escapeJson(b.getReporterEmail())).append(",")
                        .append("\"status\":").append(JsonUtil.escapeJson(b.getStatus() != null ? b.getStatus() : "pending"))
                        .append("}");
            }
            sb.append("]");
            JsonUtil.send(resp, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.send(resp, "[]");
        }
    }
}
