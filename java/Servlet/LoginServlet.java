package servlet;

import dao.UserDAO;
import model.User;
import util.JsonUtil;
import util.PasswordUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * LoginServlet – POST /LoginServlet รับ: username, password
 * (x-www-form-urlencoded) คืน JSON: { "success":true, "username":"...",
 * "firstname":"...", "role":"..." } { "success":false, "message":"..." }
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            JsonUtil.sendError(resp, "กรุณากรอก Username และ Password");
            return;
        }

        try {
            User user = userDao.findByUsername(username.trim());
            if (user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
                JsonUtil.sendError(resp, "Username หรือ Password ไม่ถูกต้อง");
                return;
            }

            // สร้าง HTTP Session ฝั่ง server
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("firstname", user.getFirstname());
            session.setAttribute("role", user.getRole());

            JsonUtil.sendSuccess(resp,
                    "\"username\":" + JsonUtil.escapeJson(user.getUsername()) + ","
                    + "\"firstname\":" + JsonUtil.escapeJson(user.getFirstname()) + ","
                    + "\"role\":" + JsonUtil.escapeJson(user.getRole()));

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ กรุณาลองใหม่");
        }
    }
}
