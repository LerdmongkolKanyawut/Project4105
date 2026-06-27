package servlet;

import dao.UserDAO;
import model.User;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * GetProfileServlet – GET /GetProfileServlet ต้อง login ก่อน (มี session) คืน
 * JSON: { "username":"...", "firstname":"...", "lastname":"...", "email":"...",
 * "role":"...", "avatarUrl":"..." }
 */
@WebServlet("/GetProfileServlet")
public class GetProfileServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.send(resp, "{\"error\":\"unauthorized\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        try {
            User user = userDao.findById(userId);
            if (user == null) {
                JsonUtil.send(resp, "{\"error\":\"user not found\"}");
                return;
            }

            String avatarUrl = (user.getAvatar() != null && !user.getAvatar().isBlank())
                    ? "UploadAvatarServlet?userId=" + userId // หรือ data URL ถ้าเก็บ base64
                    : "";

            JsonUtil.send(resp,
                    "{\"username\":" + JsonUtil.escapeJson(user.getUsername()) + ","
                    + "\"firstname\":" + JsonUtil.escapeJson(user.getFirstname()) + ","
                    + "\"lastname\":" + JsonUtil.escapeJson(user.getLastname()) + ","
                    + "\"email\":" + JsonUtil.escapeJson(user.getEmail()) + ","
                    + "\"role\":" + JsonUtil.escapeJson(user.getRole()) + ","
                    + "\"avatarUrl\":" + JsonUtil.escapeJson(avatarUrl) + "}");

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.send(resp, "{\"error\":\"server error\"}");
        }
    }
}
