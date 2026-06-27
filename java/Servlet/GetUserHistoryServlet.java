package servlet;

import dao.EntryDAO;
import model.Entry;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * GetUserHistoryServlet – GET /GetUserHistoryServlet
 * ต้อง login ก่อน
 * คืน JSON Array:
 * [{ "title":"...", "subject":"...", "uploadDate":"...", "fileCount":2 }, ...]
 */
@WebServlet("/GetUserHistoryServlet")
public class GetUserHistoryServlet extends HttpServlet {

    private final EntryDAO entryDao = new EntryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.send(resp, "[]");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        try {
            List<Entry> entries = entryDao.findByUserId(userId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < entries.size(); i++) {
                if (i > 0) sb.append(",");
                Entry e = entries.get(i);
                // description field ถูกใช้เก็บ fileCount จาก EntryDao.findByUserId()
                sb.append("{")
                  .append("\"title\":").append(JsonUtil.escapeJson(e.getTitle())).append(",")
                  .append("\"subject\":").append(JsonUtil.escapeJson(e.getSubject())).append(",")
                  .append("\"uploadDate\":").append(JsonUtil.escapeJson(e.getUploadDate())).append(",")
                  .append("\"fileCount\":").append(e.getDescription() != null ? e.getDescription() : "0")
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