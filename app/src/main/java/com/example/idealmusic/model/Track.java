package com.example.idealmusic.model;

import java.io.File;

public class Track {
    private File file;
    private String title;
    private String description;

    public Track(File file, String title, String description) {
        this.file = file;
        this.title = title;
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
