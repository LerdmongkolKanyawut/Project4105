package util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JsonUtil – ช่วยส่ง JSON response ให้ Servlet
 */
public class JsonUtil {

    public static void sendSuccess(HttpServletResponse resp) throws IOException {
        send(resp, "{\"success\":true}");
    }

    public static void sendSuccess(HttpServletResponse resp, String extraJson) throws IOException {
        // extraJson เช่น "\"key\":\"value\""
        send(resp, "{\"success\":true," + extraJson + "}");
    }

    public static void sendError(HttpServletResponse resp, String message) throws IOException {
        send(resp, "{\"success\":false,\"message\":" + escapeJson(message) + "}");
    }

    public static void send(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    /**
     * escape string สำหรับใส่ใน JSON value
     */
    public static String escapeJson(String s) {
        if (s == null) {
            return "null";
        }
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }
}
