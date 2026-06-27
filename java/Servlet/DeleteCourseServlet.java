package servlet;

import dao.CourseDAO;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * DeleteCourseServlet – POST /DeleteCourseServlet (Admin only) รับ: courseId
 * (x-www-form-urlencoded)
 */
@WebServlet("/DeleteCourseServlet")
public class DeleteCourseServlet extends HttpServlet {

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

        String param = req.getParameter("courseId");
        if (param == null || param.isBlank()) {
            JsonUtil.sendError(resp, "ไม่พบ courseId");
            return;
        }

        try {
            int courseId = Integer.parseInt(param.trim());
            courseDao.delete(courseId);
            JsonUtil.sendSuccess(resp);
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, "courseId ไม่ถูกต้อง");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }
}
