package servlet;

import dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.JsonUtil;

import java.io.*;
import java.util.Base64;

/**
 * UploadAvatarServlet – POST /UploadAvatarServlet รับ multipart field "avatar"
 * (image/*) เก็บเป็น base64 data URL ใน DB column users.avatar คืน JSON: {
 * "success":true, "avatarUrl":"data:image/...;base64,..." }
 */
@WebServlet("/UploadAvatarServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5 MB
public class UploadAvatarServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.sendError(resp, "กรุณาเข้าสู่ระบบก่อน");
            return;
        }

        Part part = req.getPart("avatar");
        if (part == null || part.getSize() == 0) {
            JsonUtil.sendError(resp, "ไม่พบไฟล์รูปภาพ");
            return;
        }

        String mimeType = part.getContentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            JsonUtil.sendError(resp, "ประเภทไฟล์ไม่ถูกต้อง ต้องเป็นรูปภาพเท่านั้น");
            return;
        }

        try (InputStream is = part.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String dataUrl = "data:" + mimeType + ";base64," + base64;

            int userId = (int) session.getAttribute("userId");
            userDao.updateAvatar(userId, dataUrl);

            JsonUtil.sendSuccess(resp, "\"avatarUrl\":" + JsonUtil.escapeJson(dataUrl));

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, "เกิดข้อผิดพลาดในการบันทึกรูปภาพ");
        }
    }
}
