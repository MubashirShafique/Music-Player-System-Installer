package com.mps.core;


import java.util.ArrayList;
import java.util.List;

/**
 * Handles searching functionality within the playlist.
 * Can search by title or partial keyword (case-insensitive).
 */
public class SearchManager {

    /**
     * Search songs by title keyword (case-insensitive)
     *
     * @param songs the list of all songs
     * @param keyword the word to search (part of title)
     * @return list of matched songs
     */
    public List<Song> searchByTitle(List<Song> songs, String keyword) {
        List<Song> results = new ArrayList<>();
        if (songs == null || songs.isEmpty() || keyword == null || keyword.isEmpty()) {
            return results;
        }

        String key = keyword.toLowerCase();

        for (Song s : songs) {
            if (s.getTitle().toLowerCase().contains(key)) {
                results.add(s);
            }
        }

        return results;
    }

    /**
     * Finds a song by exact title match (case-insensitive)
     *
     * @param songs the list of songs
     * @param title exact title to find
     * @return Song if found, otherwise null
     */
    public Song findExact(List<Song> songs, String title) {
        if (songs == null || title == null) return null;

        for (Song s : songs) {
            if (s.getTitle().equalsIgnoreCase(title)) {
                return s;
            }
        }
        return null;
    }
}
