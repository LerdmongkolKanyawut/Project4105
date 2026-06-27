package model;

/**
 * Model: entries table
 */
public class Entry {

    private int entryId;
    private int userId;
    private String title;
    private String subject;
    private String description;
    private String uploadDate;  // yyyy-MM-dd
    private String authorName;  // join จาก users (firstname + lastname)

    public Entry() {
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int v) {
        this.entryId = v;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        this.userId = v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        this.title = v;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String v) {
        this.subject = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String v) {
        this.uploadDate = v;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String v) {
        this.authorName = v;
    }
}
