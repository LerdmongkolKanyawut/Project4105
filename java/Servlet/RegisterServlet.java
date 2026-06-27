package servlet;

import dao.UserDAO;
import model.User;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

/**
 * RegisterServlet – POST /RegisterServlet รับ: firstname, lastname, email,
 * username, password, role คืน JSON: { "success":true } | { "success":false,
 * "message":"..." }
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private static final Set<String> VALID_ROLES
            = Set.of("Admin", "อาจารย์", "นักศึกษา", "บุคคลทั่วไป");

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        String firstname = req.getParameter("firstname");
        String lastname = req.getParameter("lastname");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        // Validation
        if (isBlank(firstname) || isBlank(lastname) || isBlank(email)
                || isBlank(username) || isBlank(password) || isBlank(role)) {
            JsonUtil.sendError(resp, "กรุณากรอกข้อมูลให้ครบทุกช่อง");
            return;
        }
        if (password.length() < 4) {
            JsonUtil.sendError(resp, "Password ต้องมีอย่างน้อย 4 ตัวอักษร");
            return;
        }
        if (!VALID_ROLES.contains(role)) {
            JsonUtil.sendError(resp, "ประเภทบัญชีไม่ถูกต้อง");
            return;
        }

        try {
            if (userDao.existsUsername(username.trim())) {
                JsonUtil.sendError(resp, "Username นี้ถูกใช้งานแล้ว");
                return;
            }
            if (userDao.existsEmail(email.trim())) {
                JsonUtil.sendError(resp, "Email นี้ถูกใช้งานแล้ว");
                return;
            }

            User user = new User();
            user.setFirstname(firstname.trim());
            user.setLastname(lastname.trim());
            user.setEmail(email.trim());
            user.setUsername(username.trim());
            user.setPassword(password);   // UserDao จะ hash ให้
            user.setRole(role);

            userDao.register(user);
            JsonUtil.sendSuccess(resp);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดภายในระบบ กรุณาลองใหม่");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
