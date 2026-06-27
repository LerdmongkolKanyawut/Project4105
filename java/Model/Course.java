package model;

import java.util.List;

/**
 * Model: courses + course_schedules + course_links
 */
public class Course {

    private int courseId;
    private String code;
    private String name;
    private int credits;
    private String prerequisite;
    private String description;
    private String examNote;
    private List<String> schedules;  // "วันอังคาร 11:30-13:20 SCL209"
    private List<CourseLink> links;

    public Course() {
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int v) {
        this.courseId = v;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String v) {
        this.code = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        this.name = v;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int v) {
        this.credits = v;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String v) {
        this.prerequisite = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public String getExamNote() {
        return examNote;
    }

    public void setExamNote(String v) {
        this.examNote = v;
    }

    public List<String> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<String> v) {
        this.schedules = v;
    }

    public List<CourseLink> getLinks() {
        return links;
    }

    public void setLinks(List<CourseLink> v) {
        this.links = v;
    }

    /**
     * inner class สำหรับ course_links
     */
    public static class CourseLink {

        private String label;
        private String url;

        public CourseLink(String label, String url) {
            this.label = label;
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public String getUrl() {
            return url;
        }
    }
}
