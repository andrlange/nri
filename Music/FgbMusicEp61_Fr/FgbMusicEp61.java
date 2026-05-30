package Music.FgbMusicEp61_Fr; // Imported the package my dear friend 

import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;

public class FgbMusicEp61 extends JFrame {

    private Player player;
    private Thread playThread;

    public FgbMusicEp61() {
        setTitle("MP3 Player");
        setSize(300, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");

        add(playButton);
        add(stopButton);

        playButton.addActionListener(e -> playMp3("FgbMusicEp61.mp3"));
        stopButton.addActionListener(e -> stopMp3());
    }

    private void playMp3(String filePath) {
        stopMp3();

        playThread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                player = new Player(fis);
                player.play();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        playThread.start();
    }

    private void stopMp3() {
        if (player != null) {
            player.close();
        }

        if (playThread != null) {
            playThread.interrupt();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FgbMusicEp61 gui = new FgbMusicEp61();
            gui.setVisible(true);
        });
    }
}