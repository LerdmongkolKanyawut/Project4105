package servlet;

import dao.CommentDAO;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * AddCommentServlet – POST /AddCommentServlet รับ: entryId, comment คืน JSON: {
 * "success":true } | { "success":false, "message":"..." } ต้อง login ก่อน
 */
@WebServlet("/AddCommentServlet")
public class AddCommentServlet extends HttpServlet {

    private final CommentDAO commentDao = new CommentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.sendError(resp, "กรุณาเข้าสู่ระบบก่อนแสดงความคิดเห็น");
            return;
        }

        String entryParam = req.getParameter("entryId");
        String comment = req.getParameter("comment");

        if (entryParam == null || comment == null || comment.isBlank()) {
            JsonUtil.sendError(resp, "ข้อมูลไม่ครบถ้วน");
            return;
        }

        try {
            int entryId = Integer.parseInt(entryParam);
            int userId = (int) session.getAttribute("userId");
            commentDao.insert(entryId, userId, comment.trim());
            JsonUtil.sendSuccess(resp);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, "entryId ไม่ถูกต้อง");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ");
        }
    }
}
