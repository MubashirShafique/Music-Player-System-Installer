package com.mps.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Small component stub: the frame uses a JList directly.
 * Provided so you can expand UI easily.
 */
public class PlaylistPanel extends JPanel {
    public PlaylistPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Playlist"), BorderLayout.NORTH);
    }
}
