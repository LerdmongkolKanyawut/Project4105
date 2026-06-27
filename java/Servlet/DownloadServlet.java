package servlet;

import dao.EntryDAO;
import model.FileRecord;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

/**
 * DownloadServlet – GET /DownloadServlet?fileId=N ส่งไฟล์กลับเป็น binary
 * download
 */
@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {

    private final EntryDAO entryDao = new EntryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String param = req.getParameter("fileId");
        if (param == null) {
            resp.sendError(400, "missing fileId");
            return;
        }

        try {
            int fileId = Integer.parseInt(param);
            FileRecord fr = entryDao.findFileById(fileId);
            if (fr == null) {
                resp.sendError(404, "File not found");
                return;
            }

            Path path = Paths.get(fr.getFilePath());
            if (!Files.exists(path)) {
                resp.sendError(404, "File not found on disk");
                return;
            }

            String mimeType = getServletContext().getMimeType(fr.getFileName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            resp.setContentType(mimeType);
            resp.setContentLengthLong(Files.size(path));
            resp.setHeader("Content-Disposition",
                    "attachment; filename=\"" + fr.getFileName().replace("\"", "") + "\"");

            try (OutputStream out = resp.getOutputStream()) {
                Files.copy(path, out);
            }

        } catch (NumberFormatException e) {
            resp.sendError(400, "invalid fileId");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Server error");
        }
    }
}
