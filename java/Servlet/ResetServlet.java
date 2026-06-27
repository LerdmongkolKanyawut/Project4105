package servlet;

import dao.PasswordResetDAO;
import dao.UserDAO;
import model.User;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * ResetServlet – POST /ResetServlet รับ: email คืน JSON: { "success":true,
 * "message":"ส่งลิงก์..." } { "success":false, "message":"ไม่พบ email..." }
 *
 * NOTE: ตัวอย่างนี้สร้าง token และ log ลง console ในระบบจริงให้ส่งอีเมลผ่าน
 * JavaMail หรือ SMTP service
 */
@WebServlet("/ResetServlet")
public class ResetServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();
    private final PasswordResetDAO resetDao = new PasswordResetDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        if (email == null || !email.contains("@")) {
            JsonUtil.sendError(resp, "กรุณากรอก Email ที่ถูกต้อง");
            return;
        }

        try {
            User user = userDao.findByEmail(email.trim());
            // คืน success เสมอ เพื่อไม่ให้ leak ว่า email มีในระบบหรือไม่
            if (user != null) {
                String token = resetDao.createToken(user.getUserId());
                // TODO: ส่งอีเมลจริง ตัวอย่าง link:
                String resetLink = req.getScheme() + "://" + req.getServerName()
                        + ":" + req.getServerPort()
                        + req.getContextPath() + "/new-password.html?token=" + token;
                System.out.println("[ResetServlet] Reset link for " + email + " : " + resetLink);
            }

            JsonUtil.sendSuccess(resp,
                    "\"message\":" + JsonUtil.escapeJson(
                            "ส่งลิงก์รีเซ็ตรหัสผ่านไปยัง " + email + " เรียบร้อยแล้ว (ถ้า email นี้มีในระบบ)"));

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ กรุณาลองใหม่");
        }
    }
}
