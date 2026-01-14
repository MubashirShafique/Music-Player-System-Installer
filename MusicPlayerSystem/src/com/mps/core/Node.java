package com.mps.core;

/**
 * Node for doubly linked list used in Playlist navigation (next/previous).
 * Each node holds a Song object and links to the next and previous nodes.
 */
public class Node {
    private Song song;
    private Node next;
    private Node prev;

    public Node(Song song) {
        this.song = song;
        this.next = null;
        this.prev = null;
    }

    public Song getSong() {
        return song;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
