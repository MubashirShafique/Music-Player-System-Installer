package com.mps.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Playlist implemented using a doubly linked list (Node-based).
 * Supports navigation: next(), previous(), getCurrent()
 */
public class Playlist {

    private Node head;
    private Node tail;
    private Node current;
    private int size = 0;

    public Playlist() {
        this.head = null;
        this.tail = null;
        this.current = null;
    }

    /**
     * Add a new song at the end of the playlist.
     */
    public void add(Song song) {
        Node newNode = new Node(song);
        if (head == null) {
            head = newNode;
            tail = newNode;
            current = head;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
    }

    /**
     * Add a list of songs.
     */
    public void addAll(List<Song> songs) {
        if (songs == null) return;
        for (Song s : songs) {
            add(s);
        }
    }

    /**
     * Move to next song in linked list.
     */
    public Song next() {
        if (current != null && current.getNext() != null) {
            current = current.getNext();
        } else {
            // Loop back to start
            current = head;
        }
        return getCurrent();
    }

    /**
     * Move to previous song in linked list.
     */
    public Song previous() {
        if (current != null && current.getPrev() != null) {
            current = current.getPrev();
        } else {
            // Loop to end
            current = tail;
        }
        return getCurrent();
    }

    /**
     * Get currently playing song.
     */
    public Song getCurrent() {
        return (current != null) ? current.getSong() : null;
    }

    /**
     * Set the current song by index.
     */
    public void setIndex(int index) {
        Node temp = head;
        int i = 0;
        while (temp != null) {
            if (i == index) {
                current = temp;
                return;
            }
            temp = temp.getNext();
            i++;
        }
    }

    /**
     * Return all songs in playlist as a list (for JList UI).
     */
    public List<Song> getSongs() {
        List<Song> songs = new ArrayList<>();
        Node temp = head;
        while (temp != null) {
            songs.add(temp.getSong());
            temp = temp.getNext();
        }
        return songs;
    }

    /**
     * Clears the playlist.
     */
    public void clear() {
        head = tail = current = null;
        size = 0;
    }

    /**
     * Returns size of playlist.
     */
    public int size() {
        return size;
    }

    /**
     * Get current index position.
     */
    public int getCurrentIndex() {
        int index = 0;
        Node temp = head;
        while (temp != null) {
            if (temp == current) return index;
            temp = temp.getNext();
            index++;
        }
        return -1;
    }
}
