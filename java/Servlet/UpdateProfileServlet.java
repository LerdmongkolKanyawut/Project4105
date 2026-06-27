package servlet;

import dao.UserDAO;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * UpdateProfileServlet – POST /UpdateProfileServlet รับ: firstname, lastname,
 * email คืน JSON: { "success":true } | { "success":false, "message":"..." }
 */
@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.sendError(resp, "กรุณาเข้าสู่ระบบก่อน");
            return;
        }

        String firstname = req.getParameter("firstname");
        String lastname = req.getParameter("lastname");
        String email = req.getParameter("email");

        if (firstname == null || firstname.isBlank()) {
            JsonUtil.sendError(resp, "กรุณากรอกชื่อ");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        try {
            userDao.updateProfile(userId,
                    firstname.trim(),
                    lastname != null ? lastname.trim() : "",
                    email != null ? email.trim() : "");

            // อัปเดต session ด้วย
            session.setAttribute("firstname", firstname.trim());
            JsonUtil.sendSuccess(resp);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ");
        }
    }
}
