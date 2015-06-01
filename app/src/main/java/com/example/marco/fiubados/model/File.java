package com.example.marco.fiubados.model;

/**
 * Modelo del archivo (basado en un link, no almacena el archivo completo)
 */
public class File {
    private static final String FILE_TYPE_YOUTUBE   = "file_type_youtube";
    private static final String FILE_TYPE_PDF       = "file_type_pdf";
    private static final String FILE_TYPE_OTHER     = "file_type_other";

    private String name;
    private String url;
    private String type;
    private User uploader;

    public File(String name, String link, User uploader) {
        this.name = name;
        this.url = link;
        this.uploader = uploader;
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
}
