package com.mps.ui;

import com.mps.audio.AudioPlayer;
import com.mps.core.Playlist;
import com.mps.core.Song;
import com.mps.core.SearchManager;
import com.mps.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class MusicPlayerFrame extends JFrame {
    private final Playlist playlist = new Playlist();
    private final AudioPlayer audio = new AudioPlayer();
    private final DefaultListModel<Song> listModel = new DefaultListModel<>();
    private final JList<Song> songJList = new JList<>(listModel);
    private final JLabel nowLabel = new JLabel("Now: -");
    private final JTextField searchField = new JTextField();
    private final SearchManager searchManager = new SearchManager();
    private final File songsFolder;
    private final File dbFile;

    public MusicPlayerFrame() {
        super("🎵 Music Player System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        // 🧠 Set custom window icon (removes Java logo)
        Image icon = new ImageIcon(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "27648.png").getImage();
        setIconImage(icon);

        songsFolder = new File(System.getProperty("user.dir"), "Songs");
        dbFile = new File(System.getProperty("user.dir"), "musicdb.accdb");

        buildUI();
        loadSongs();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        // === TOP BAR WITH INFO BUTTON ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.DARK_GRAY);
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Icon beside title
        JLabel title = new JLabel("  🎵 Music Player System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Add small logo image near title
        ImageIcon logoIcon = new ImageIcon(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "27648.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(logoLabel);
        titlePanel.add(title);

        JButton infoBtn = new JButton("ℹ️ Info");
        infoBtn.setFocusPainted(false);
        infoBtn.setBackground(Color.BLACK);
        infoBtn.setForeground(Color.WHITE);
        infoBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        infoBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        infoBtn.addActionListener(e -> showInfoDialog());

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(infoBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // === LEFT PANEL ===
        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.setPreferredSize(new Dimension(340, 0));
        left.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topSearch = new JPanel(new BorderLayout(6, 6));
        searchField.setToolTipText("Search songs by name...");
        topSearch.add(searchField, BorderLayout.CENTER);
        JButton searchBtn = new JButton("Search");
        topSearch.add(searchBtn, BorderLayout.EAST);
        left.add(topSearch, BorderLayout.NORTH);

        songJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Song s) l.setText(s.getTitle());
                return l;
            }
        });
        JScrollPane listScroll = new JScrollPane(songJList);
        left.add(listScroll, BorderLayout.CENTER);

        JPanel leftButtons = new JPanel(new GridLayout(2, 2, 6, 6));
        JButton btnLoadFolder = new JButton("Load Songs");
        JButton btnLoadDB = new JButton("Load DB");
        JButton btnImport = new JButton("Import Song");
        JButton btnRemove = new JButton("Remove Song");

        leftButtons.add(btnLoadFolder);
        leftButtons.add(btnLoadDB);
        leftButtons.add(btnImport);
        leftButtons.add(btnRemove);
        left.add(leftButtons, BorderLayout.SOUTH);
        add(left, BorderLayout.WEST);

        // === RIGHT PANEL ===
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        nowLabel.setFont(nowLabel.getFont().deriveFont(Font.BOLD, 16f));
        right.add(nowLabel, BorderLayout.NORTH);

        JLabel cover = new JLabel("🎧", SwingConstants.CENTER);
        cover.setFont(new Font("SansSerif", Font.PLAIN, 120));
        right.add(cover, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        JButton prev = new JButton("Prev");
        JButton play = new JButton("Play");
        JButton stop = new JButton("Stop");
        JButton next = new JButton("Next");

        controls.add(prev);
        controls.add(play);
        controls.add(stop);
        controls.add(next);
        right.add(controls, BorderLayout.SOUTH);
        add(right, BorderLayout.CENTER);

        // === EVENT HANDLERS ===
        songJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Song s = songJList.getSelectedValue();
                if (s != null) {
                    playlist.setIndex(songJList.getSelectedIndex());
                    playSong(s);
                }
            }
        });

        play.addActionListener(a -> {
            Song cur = playlist.getCurrent();
            if (cur != null) playSong(cur);
        });

        stop.addActionListener(a -> {
            audio.stop();
            nowLabel.setText("Now: -");
        });

        next.addActionListener(a -> {
            Song nextSong = playlist.next();
            if (nextSong != null) {
                songJList.setSelectedIndex(playlist.getCurrentIndex());
                playSong(nextSong);
            }
        });

        prev.addActionListener(a -> {
            Song prevSong = playlist.previous();
            if (prevSong != null) {
                songJList.setSelectedIndex(playlist.getCurrentIndex());
                playSong(prevSong);
            }
        });

        searchBtn.addActionListener(a -> doSearch());
        searchField.addActionListener(a -> doSearch());
        btnLoadFolder.addActionListener(a -> loadSongsFromFolder());
        btnLoadDB.addActionListener(a -> loadSongsFromDatabase());
        btnImport.addActionListener(a -> importSong());
        btnRemove.addActionListener(a -> removeSelectedSong());
    }

    // 🧾 INFO DIALOG
    private void showInfoDialog() {
        String infoText = """
                ⚙️ MUSIC PLAYER SYSTEM

                👨‍💻 Developers: 
                    Muhammad Usman Khan   
                    Meraj Ali
                    Muhammad Mubashir Shafique (Mubrix Technology)

                🏫 Sukkur IBA University   
                📚 BS Computer Science | Section G | Semester 3   
                📆 Batch: 2024 - 2028

                🚀 Innovation through Code!
                        
                © 2025 Mubrix Technology. All Rights Reserved.

                """;

        JTextArea textArea = new JTextArea(infoText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JOptionPane.showMessageDialog(this, textArea,
                "About Project", JOptionPane.INFORMATION_MESSAGE);
    }

    // 🟢 IMPORT SONG (same as before)
    private void importSong() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select MP3 Song to Import");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Audio Files", "mp3", "wav"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected != null && selected.exists()) {
                try {
                    if (!songsFolder.exists()) songsFolder.mkdirs();

                    File dest = new File(songsFolder, selected.getName());
                    Files.copy(selected.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    String relativePath = "\\Songs\\" + dest.getName();
                    Song newSong = new Song(-1, selected.getName(), relativePath);
                    playlist.add(newSong);
                    listModel.addElement(newSong);

                    DatabaseManager dm = new DatabaseManager();
                    if (dm.connectToAccess(dbFile)) {
                        dm.insertSong(newSong);
                        dm.close();
                    }

                    JOptionPane.showMessageDialog(this, "✅ Song imported successfully!");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error importing song: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // 🔴 REMOVE SONG
    private void removeSelectedSong() {
        Song selected = songJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No song selected.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure to delete \"" + selected.getTitle() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        DatabaseManager dm = new DatabaseManager();
        if (dm.connectToAccess(dbFile)) {
            dm.deleteSong(selected);
            dm.close();
        }

        File f = selected.getFile();
        if (f != null && f.exists()) f.delete();

        listModel.removeElement(selected);
        playlist.getSongs().remove(selected);

        JOptionPane.showMessageDialog(this, "🗑️ Song deleted successfully!");
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        listModel.clear();
        if (q.isEmpty()) for (Song s : playlist.getSongs()) listModel.addElement(s);
        else for (Song s : searchManager.searchByTitle(playlist.getSongs(), q)) listModel.addElement(s);
    }

    private void loadSongs() {
        File db = new File(System.getProperty("user.dir"), "musicdb.accdb");
        if (db.exists()) {
            DatabaseManager dm = new DatabaseManager();
            if (dm.connectToAccess(db)) {
                var songs = dm.loadSongsFromDb();
                dm.close();
                if (!songs.isEmpty()) {
                    playlist.clear();
                    playlist.addAll(songs);
                    refreshListModel();
                    return;
                }
            }
        }
        loadSongsFromFolder();
    }

    private void loadSongsFromFolder() {
        playlist.clear();
        listModel.clear();
        File[] files = songsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (files != null) {
            var list = new ArrayList<Song>();
            for (File f : files) list.add(new Song(f));
            playlist.addAll(list);
            refreshListModel();
        }
    }

    private void loadSongsFromDatabase() {
        DatabaseManager dm = new DatabaseManager();
        if (!dm.connectToAccess(dbFile)) return;
        var songs = dm.loadSongsFromDb();
        dm.close();
        playlist.clear();
        playlist.addAll(songs);
        refreshListModel();
    }

    private void refreshListModel() {
        listModel.clear();
        for (Song s : playlist.getSongs()) listModel.addElement(s);
    }

    private void playSong(Song s) {
        if (s == null) return;
        try {
            audio.playFile(s.getFile());
            nowLabel.setText("Now: " + s.getTitle());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot play file: " + ex.getMessage(),
                    "Playback Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
