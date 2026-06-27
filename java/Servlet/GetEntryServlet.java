package servlet;

import dao.EntryDAO;
import model.Entry;
import model.FileRecord;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * GetEntryServlet – GET /GetEntryServlet?entryId=N คืน JSON: { "title":"...",
 * "author":"...", "uploadDate":"...", "subject":"...", "desc":"...", "files":[{
 * "fileId":1,"fileName":"...","fileType":"..." }, ...] }
 */
@WebServlet("/GetEntryServlet")
public class GetEntryServlet extends HttpServlet {

    private final EntryDAO entryDao = new EntryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String param = req.getParameter("entryId");
        if (param == null) {
            JsonUtil.sendError(resp, "missing entryId");
            return;
        }

        try {
            int entryId = Integer.parseInt(param);
            Entry entry = entryDao.findById(entryId);
            if (entry == null) {
                JsonUtil.sendError(resp, "ไม่พบข้อมูล");
                return;
            }

            List<FileRecord> files = entryDao.findFilesByEntryId(entryId);
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"title\":").append(JsonUtil.escapeJson(entry.getTitle())).append(",")
                    .append("\"author\":").append(JsonUtil.escapeJson(entry.getAuthorName())).append(",")
                    .append("\"uploadDate\":").append(JsonUtil.escapeJson(entry.getUploadDate())).append(",")
                    .append("\"subject\":").append(JsonUtil.escapeJson(entry.getSubject())).append(",")
                    .append("\"desc\":").append(JsonUtil.escapeJson(entry.getDescription())).append(",")
                    .append("\"files\":[");
            for (int i = 0; i < files.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                FileRecord f = files.get(i);
                sb.append("{\"fileId\":").append(f.getFileId()).append(",")
                        .append("\"fileName\":").append(JsonUtil.escapeJson(f.getFileName())).append(",")
                        .append("\"fileType\":").append(JsonUtil.escapeJson(f.getFileType())).append("}");
            }
            sb.append("]}");
            JsonUtil.send(resp, sb.toString());

        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, "entryId ไม่ถูกต้อง");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ");
        }
    }
}
