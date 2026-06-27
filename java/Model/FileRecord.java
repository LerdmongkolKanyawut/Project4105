package model;

/**
 * Model: files table
 */
public class FileRecord {

    private int fileId;
    private int entryId;
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;

    public FileRecord() {
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int v) {
        this.fileId = v;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int v) {
        this.entryId = v;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String v) {
        this.fileName = v;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String v) {
        this.filePath = v;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String v) {
        this.fileType = v;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long v) {
        this.fileSize = v;
    }
}
