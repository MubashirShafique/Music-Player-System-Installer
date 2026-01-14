package com.mps.audio;

import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple MP3 player wrapper using JLayer.
 * Supports start (play) and stop. Runs playback on a dedicated thread.
 */
public class AudioPlayer {
    private Thread playThread;
    private Player player;
    private AtomicBoolean playing = new AtomicBoolean(false);

    public synchronized void playFile(File mp3File) throws Exception {
        stop(); // stop any current playback

        FileInputStream fis = new FileInputStream(mp3File);
        BufferedInputStream bis = new BufferedInputStream(fis);
        player = new Player(bis);

        playing.set(true);
        playThread = new Thread(() -> {
            try {
                player.play();
            } catch (Throwable t) {
                // swallow - caller can print if desired
            } finally {
                playing.set(false);
                closePlayer();
            }
        }, "AudioPlayer-Thread");
        playThread.start();
    }

    public synchronized void stop() {
        playing.set(false);
        if (player != null) {
            try {
                player.close();
            } catch (Exception ignored) {}
            player = null;
        }
        if (playThread != null) {
            try {
                playThread.interrupt();
            } catch (Exception ignored) {}
            playThread = null;
        }
    }

    public boolean isPlaying() {
        return playing.get();
    }

    private void closePlayer() {
        if (player != null) {
            try { player.close(); } catch (Exception ignored) {}
            player = null;
        }
    }
}
