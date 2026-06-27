package servlet;

import dao.EntryDAO;
import model.Entry;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * GetEntriesServlet – GET /GetEntriesServlet
 * คืน JSON Array ของ entries ทั้งหมด
 * [{ "entryId":1, "title":"...", "author":"...", "subject":"...", "uploadDate":"..." }, ...]
 */
@WebServlet("/GetEntriesServlet")
public class GetEntriesServlet extends HttpServlet {

    private final EntryDAO entryDao = new EntryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            List<Entry> entries = entryDao.findAll();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < entries.size(); i++) {
                Entry e = entries.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"entryId\":").append(e.getEntryId()).append(",")
                  .append("\"title\":").append(JsonUtil.escapeJson(e.getTitle())).append(",")
                  .append("\"author\":").append(JsonUtil.escapeJson(e.getAuthorName())).append(",")
                  .append("\"subject\":").append(JsonUtil.escapeJson(e.getSubject())).append(",")
                  .append("\"uploadDate\":").append(JsonUtil.escapeJson(e.getUploadDate()))
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