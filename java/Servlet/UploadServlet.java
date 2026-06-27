package servlet;

import dao.EntryDAO;
import model.Entry;
import model.FileRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/UploadServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 50 * 1024 * 1024, // 50 MB
        maxRequestSize = 200 * 1024 * 1024 // 200 MB
)
public class UploadServlet extends HttpServlet {

    private static final String DATE_FMT = "yyyy-MM-dd";
    private static final Set<String> ALLOWED = new HashSet<>(
            Arrays.asList("pdf", "docx", "doc", "pptx", "ppt", "xlsx", "xls", "txt", "zip", "rar"));

    private String uploadDir;
    private final EntryDAO entryDao = new EntryDAO();

    @Override
    public void init() {
        // Paths.get() สร้าง path ถูกต้องบนทุก OS ไม่ขึ้นกับ GlassFish deploy dir
        uploadDir = Paths.get(
                System.getProperty("user.home"),
                "Desktop", "Lab_COS", "COS4105", "uploads"
        ).toAbsolutePath().toString();
        new File(uploadDir).mkdirs();
        System.out.println("[UploadServlet] uploadDir = " + uploadDir);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // ── ตรวจ session ──────────────────────────────────────
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }
        int userId = (int) session.getAttribute("userId");

        // ── อ่าน ALL parts ครั้งเดียว ─────────────────────────
        Collection<Part> allParts;
        try {
            allParts = req.getParts();
        } catch (Exception e) {
            redirect(resp, "error", "อ่าน multipart ไม่ได้: " + e.getMessage());
            return;
        }

        // แยก text fields และ file parts จาก collection เดียวกัน
        String title = "";
        String subject = "";
        String desc = "";
        List<Part> fileParts = new ArrayList<>();

        for (Part part : allParts) {
            String name = part.getName();
            if (part.getSubmittedFileName() == null) {
                // text field
                try (InputStream is = part.getInputStream()) {
                    String val = new String(is.readAllBytes(), "UTF-8").trim();
                    if ("title".equals(name)) {
                        title = val;
                    } else if ("subject".equals(name)) {
                        subject = val;
                    } else if ("description".equals(name)) {
                        desc = val;
                    }
                }
            } else {
                // file part
                if (part.getSize() > 0) {
                    fileParts.add(part);
                }
            }
        }

        System.out.println("[UploadServlet] title=" + title);
        System.out.println("[UploadServlet] subject=" + subject);
        System.out.println("[UploadServlet] files=" + fileParts.size());

        // ── Validation ────────────────────────────────────────
        if (title.isBlank() || subject.isBlank()) {
            redirect(resp, "error", "กรุณากรอกหัวข้อและรหัสวิชา");
            return;
        }
        if (fileParts.isEmpty()) {
            redirect(resp, "error", "กรุณาเลือกไฟล์อย่างน้อย 1 ไฟล์");
            return;
        }

        try {
            // ── บันทึก entry ──────────────────────────────────
            Entry entry = new Entry();
            entry.setUserId(userId);
            entry.setTitle(title);
            entry.setSubject(subject.toUpperCase());
            entry.setDescription(desc);
            entry.setUploadDate(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FMT)));
            int entryId = entryDao.insert(entry);
            System.out.println("[UploadServlet] entryId = " + entryId);

            // ── บันทึกแต่ละไฟล์ ───────────────────────────────
            for (Part part : fileParts) {
                String original = part.getSubmittedFileName();
                if (original == null || original.isBlank()) {
                    continue;
                }

                // ตัด path ถ้า browser ส่งมาเป็น full path (IE เก่า)
                original = Paths.get(original).getFileName().toString();

                String ext = getExt(original).toLowerCase();
                if (!ALLOWED.contains(ext)) {
                    System.out.println("[UploadServlet] skip (not allowed ext): " + original);
                    continue;
                }

                String saved = entryId + "_" + System.currentTimeMillis() + "_" + sanitize(original);
                Path dest = Paths.get(uploadDir, saved);

                try (InputStream is = part.getInputStream()) {
                    Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
                }
                System.out.println("[UploadServlet] saved: " + dest);

                FileRecord fr = new FileRecord();
                fr.setEntryId(entryId);
                fr.setFileName(original);
                fr.setFilePath(dest.toString());
                fr.setFileType(ext.length() > 5 ? ext.substring(0, 5) : ext);
                fr.setFileSize(part.getSize());
                entryDao.insertFile(fr);
                System.out.println("[UploadServlet] DB insertFile OK: " + original);
            }

            redirect(resp, "success", "1");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[UploadServlet] ERROR: " + e.getClass().getName() + " - " + e.getMessage());
            redirect(resp, "error", "เกิดข้อผิดพลาดภายในระบบ: " + e.getMessage());
        }
    }

    private String getExt(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1) : "";
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9ก-๙._\\-]", "_");
    }

    private void redirect(HttpServletResponse resp, String key, String value) throws IOException {
        resp.sendRedirect("upload.html?" + key + "=" + java.net.URLEncoder.encode(value, "UTF-8"));
    }
}
