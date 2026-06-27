package servlet;

import dao.UserDAO;
import model.User;
import util.JsonUtil;
import util.PasswordUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * ChangePasswordServlet – POST /ChangePasswordServlet รับ: oldPassword,
 * newPassword คืน JSON: { "success":true } | { "success":false, "message":"..."
 * }
 */
@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {

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

        String oldPw = req.getParameter("oldPassword");
        String newPw = req.getParameter("newPassword");

        if (oldPw == null || newPw == null || oldPw.isBlank() || newPw.isBlank()) {
            JsonUtil.sendError(resp, "กรุณากรอกข้อมูลให้ครบ");
            return;
        }
        if (newPw.length() < 4) {
            JsonUtil.sendError(resp, "รหัสผ่านใหม่ต้องมีอย่างน้อย 4 ตัวอักษร");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        try {
            User user = userDao.findById(userId);
            if (user == null || !PasswordUtil.verifyPassword(oldPw, user.getPassword())) {
                JsonUtil.sendError(resp, "รหัสผ่านปัจจุบันไม่ถูกต้อง");
                return;
            }
            userDao.updatePassword(userId, PasswordUtil.hashPassword(newPw));
            JsonUtil.sendSuccess(resp);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ");
        }
    }
}
