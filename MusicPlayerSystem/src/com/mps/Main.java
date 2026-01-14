package com.mps;

import javax.swing.SwingUtilities;
import com.mps.ui.MusicPlayerFrame;
import com.mps.ui.SplashScreen;

public class Main {
    public static void main(String[] args) {
        // Show splash screen first
        SplashScreen splash = new SplashScreen();
        splash.showSplash();

        // After splash, launch main app
        SwingUtilities.invokeLater(() -> {
            MusicPlayerFrame frame = new MusicPlayerFrame();
            frame.setVisible(true);
        });
    }
}
