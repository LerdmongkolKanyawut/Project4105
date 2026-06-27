package model;

/**
 * Model: comments table
 */
public class Comment {

    private int commentId;
    private int entryId;
    private int userId;
    private String comment;
    private String createdAt;
    private String authorName; // join จาก users

    public Comment() {
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int v) {
        this.commentId = v;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String v) {
        this.comment = v;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String v) {
        this.createdAt = v;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String v) {
        this.authorName = v;
    }
}
