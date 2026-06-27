package servlet;

import dao.CourseDAO;
import model.Course;
import util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/CoursesServlet")
public class CoursesServlet extends HttpServlet {

    private final CourseDAO courseDao = new CourseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            System.out.println("[CoursesServlet] called");

            List<Course> courses = courseDao.findAll();

            System.out.println("[CoursesServlet] course count = " + courses.size());

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < courses.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                Course c = courses.get(i);
                sb.append("{")
                        .append("\"courseId\":").append(c.getCourseId()).append(",")
                        .append("\"code\":").append(JsonUtil.escapeJson(c.getCode())).append(",")
                        .append("\"name\":").append(JsonUtil.escapeJson(c.getName())).append(",")
                        .append("\"credits\":").append(c.getCredits()).append(",")
                        .append("\"pr\":").append(JsonUtil.escapeJson(c.getPrerequisite())).append(",")
                        .append("\"exam\":").append(JsonUtil.escapeJson(c.getExamNote())).append(",")
                        .append("\"desc\":").append(JsonUtil.escapeJson(c.getDescription())).append(",");

                sb.append("\"schedules\":[");
                List<String> scheds = c.getSchedules();
                for (int j = 0; j < scheds.size(); j++) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(JsonUtil.escapeJson(scheds.get(j)));
                }
                sb.append("],");

                sb.append("\"links\":[");
                List<Course.CourseLink> links = c.getLinks();
                for (int j = 0; j < links.size(); j++) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    Course.CourseLink lk = links.get(j);
                    sb.append("{\"label\":").append(JsonUtil.escapeJson(lk.getLabel()))
                            .append(",\"url\":").append(JsonUtil.escapeJson(lk.getUrl())).append("}");
                }
                sb.append("]}");
            }
            sb.append("]");

            System.out.println("[CoursesServlet] JSON = " + sb.toString());

            JsonUtil.send(resp, sb.toString());

        } catch (Exception e) {
            System.out.println("[CoursesServlet] EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            JsonUtil.send(resp, "[]");
        }
    }
}
