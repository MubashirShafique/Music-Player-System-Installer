package com.mps.db;

import com.mps.core.Song;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection conn;

    public boolean connectToAccess(File dbFile) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String url = "jdbc:ucanaccess://" + dbFile.getAbsolutePath();
            conn = DriverManager.getConnection(url);
            return true;
        } catch (Exception e) {
            System.err.println("❌ DB connect fail: " + e.getMessage());
            conn = null;
            return false;
        }
    }

    public List<Song> loadSongsFromDb() {
        List<Song> results = new ArrayList<>();
        if (conn == null) return results;

        String query = "SELECT ID, Title, Path FROM Table1";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("Title");
                String path = rs.getString("Path");
                Song song = new Song(id, title, path);

                File f = song.getFile();
                if (f != null && f.exists()) results.add(song);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading songs: " + e.getMessage());
        }

        return results;
    }

    // 🟢 Insert with RELATIVE PATH
    public void insertSong(Song song) {
        if (conn == null || song == null) return;

        String relativePath = "\\Songs\\" + song.getFile().getName();
        String sql = "INSERT INTO Table1 (Title, Path) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, song.getTitle());
            ps.setString(2, relativePath);
            ps.executeUpdate();
            System.out.println("✅ Added song to DB: " + song.getTitle() + " (" + relativePath + ")");
        } catch (SQLException e) {
            System.err.println("❌ Insert failed: " + e.getMessage());
        }
    }

    // 🔴 Delete by ID or by Title if ID = -1
    public void deleteSong(Song song) {
        if (conn == null || song == null) return;

        String sql;
        if (song.getId() > 0)
            sql = "DELETE FROM Table1 WHERE ID = ?";
        else
            sql = "DELETE FROM Table1 WHERE Title = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (song.getId() > 0) ps.setInt(1, song.getId());
            else ps.setString(1, song.getTitle());
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("🗑️ Deleted song: " + song.getTitle());
            else System.out.println("⚠️ No DB record found for: " + song.getTitle());
        } catch (SQLException e) {
            System.err.println("❌ Delete failed: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.err.println("⚠️ DB close fail: " + e.getMessage());
        }
    }
}
