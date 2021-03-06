package com.example.marco.fiubados.model;

/**
 * Modelo del archivo (basado en un link, no almacena el archivo completo)
 */
public class File extends DatabaseObject{
    private static final String FILE_TYPE_YOUTUBE   = "file_type_youtube";
    private static final String FILE_TYPE_PDF       = "file_type_pdf";
    private static final String FILE_TYPE_OTHER     = "file_type_other";

    private String name;
    private String url;
    private String type;
    private User uploader;
    private String uploaderFullName;
    private String creationDate;

    public File(String id) {
        super(id);
    }

    public File(String id,String name, String link, User uploader) {
        super(id);
        this.name = name;
        this.url = link;
        this.uploader = uploader;
        this.type = FILE_TYPE_OTHER;
    }

    public File(String id,String name, String link, String uploaderFullName) {
        super(id);
        this.name = name;
        this.url = link;
        this.uploaderFullName = uploaderFullName;
        this.type = FILE_TYPE_OTHER;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public String getUploaderFullName() {
        return this.uploaderFullName;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationDate() {
        return creationDate;
    }
}
