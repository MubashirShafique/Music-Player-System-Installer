package com.mps.core;

import java.io.File;

/**
 * Represents a song record from the Access database (Table1).
 * Database fields: ID (int), Title (String), Path (String)
 * The Path in DB is usually relative to the project folder (e.g. "\Songs\Track.mp3")
 */
public class Song {

    private int id;
    private String title;
    private File file;

    /**
     * Constructor for database-based Song (with ID, title, and relative/absolute path)
     */
    public Song(int id, String title, String path) {
        this.id = id;
        this.title = title;

        // Normalize path (remove quotes, fix slashes, handle relative path)
        if (path != null) {
            path = path.replace("\"", "").replace("\\", File.separator);
            File f = new File(path);
            if (!f.isAbsolute()) {
                // If it's relative, resolve to project directory
                f = new File(System.getProperty("user.dir"), path);
            }
            this.file = f;
        } else {
            this.file = null;
        }
    }

    /**
     * Constructor for file-based songs (fallback when DB not used)
     */
    public Song(File file) {
        this.file = file;
        this.title = file != null ? file.getName() : "Unknown";
        this.id = -1;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        return (file != null) ? file.getAbsolutePath() : "N/A";
    }

    @Override
    public String toString() {
        return title;
    }
}
