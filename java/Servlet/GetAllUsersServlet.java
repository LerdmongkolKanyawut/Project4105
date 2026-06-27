package servlet;

import dao.UserDAO;
import model.User;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * GetAllUsersServlet – GET /GetAllUsersServlet (Admin only)
 */
@WebServlet("/GetAllUsersServlet")
public class GetAllUsersServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null
                || !"Admin".equals(session.getAttribute("role"))) {
            JsonUtil.send(resp, "[]");
            return;
        }
        try {
            List<User> users = userDao.findAll();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                User u = users.get(i);
                sb.append("{")
                        .append("\"userId\":").append(u.getUserId()).append(",")
                        .append("\"username\":").append(JsonUtil.escapeJson(u.getUsername())).append(",")
                        .append("\"firstname\":").append(JsonUtil.escapeJson(u.getFirstname())).append(",")
                        .append("\"lastname\":").append(JsonUtil.escapeJson(u.getLastname())).append(",")
                        .append("\"email\":").append(JsonUtil.escapeJson(u.getEmail())).append(",")
                        .append("\"role\":").append(JsonUtil.escapeJson(u.getRole()))
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
