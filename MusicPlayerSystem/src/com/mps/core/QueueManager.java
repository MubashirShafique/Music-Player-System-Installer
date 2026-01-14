package com.mps.core;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles queued songs (temporary "play next" requests).
 */
public class QueueManager {
    private final Queue<Song> queue = new LinkedList<>();

    public void enqueue(Song song) {
        if (song != null) queue.add(song);
    }

    public Song poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }

    public int size() {
        return queue.size();
    }
}
