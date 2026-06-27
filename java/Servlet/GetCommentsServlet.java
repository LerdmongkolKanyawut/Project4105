package servlet;

import dao.CommentDAO;
import model.Comment;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * GetCommentsServlet – GET /GetCommentsServlet?entryId=N คืน JSON Array: [{
 * "author":"...", "createdAt":"...", "comment":"..." }, ...]
 */
@WebServlet("/GetCommentsServlet")
public class GetCommentsServlet extends HttpServlet {

    private final CommentDAO commentDao = new CommentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String param = req.getParameter("entryId");
        if (param == null) {
            JsonUtil.send(resp, "[]");
            return;
        }

        try {
            int entryId = Integer.parseInt(param);
            List<Comment> comments = commentDao.findByEntryId(entryId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < comments.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                Comment c = comments.get(i);
                sb.append("{")
                        .append("\"author\":").append(JsonUtil.escapeJson(c.getAuthorName())).append(",")
                        .append("\"createdAt\":").append(JsonUtil.escapeJson(c.getCreatedAt())).append(",")
                        .append("\"comment\":").append(JsonUtil.escapeJson(c.getComment()))
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
