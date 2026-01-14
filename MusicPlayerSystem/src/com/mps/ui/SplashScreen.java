package com.mps.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;

public class SplashScreen extends JWindow {
    private final Image logo;
    private double angle = 0;
    private final JProgressBar progressBar;

    public SplashScreen() {
        // Load logo image
        logo = new ImageIcon(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "27648.png").getImage();

        setSize(500, 350);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 15));
        progressBar.setForeground(Color.CYAN);
        progressBar.setBackground(Color.DARK_GRAY);
        add(progressBar, BorderLayout.SOUTH);
    }

    public void showSplash() {
        setVisible(true);

        // Create animation thread
        Timer rotationTimer = new Timer(30, e -> {
            angle += 5;
            repaint();
        });
        rotationTimer.start();

        // Simulate loading
        for (int i = 0; i <= 100; i++) {
            progressBar.setValue(i);
            try {
                Thread.sleep(40); // adjust for slower or faster loading
            } catch (InterruptedException ignored) {}
        }

        rotationTimer.stop();
        setVisible(false);
        dispose();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        // Draw title text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        String title = "🎵 Music Player System";
        int textWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (w - textWidth) / 2, 50);

        // Rotate logo
        int logoSize = 120;
        int centerX = w / 2;
        int centerY = h / 2 - 30;
        AffineTransform old = g2.getTransform();
        g2.translate(centerX, centerY);
        g2.rotate(Math.toRadians(angle));
        g2.drawImage(logo, -logoSize / 2, -logoSize / 2, logoSize, logoSize, this);
        g2.setTransform(old);

        // Draw text under icon
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String tagline = "Developed by Muhammad Mubashir Shafique & Team";
        int taglineWidth = g2.getFontMetrics().stringWidth(tagline);
        g2.drawString(tagline, (w - taglineWidth) / 2, h - 60);
    }
}
