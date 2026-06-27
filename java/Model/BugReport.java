package model;

/**
 * Model: bug_reports table
 */
public class BugReport {

    private int reportId;
    private String subject;
    private String detail;
    private String reporterName;
    private String reporterEmail;
    private Integer userId;   // null ถ้าไม่ได้ login
    private String status;    // pending | in_progress | resolved

    public BugReport() {
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int v) {
        this.reportId = v;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String v) {
        this.subject = v;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String v) {
        this.detail = v;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String v) {
        this.reporterName = v;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String v) {
        this.reporterEmail = v;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer v) {
        this.userId = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        this.status = v;
    }
}
