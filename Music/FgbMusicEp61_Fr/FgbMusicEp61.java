package Music.FgbMusicEp61_Fr; // Imported the package my dear friend

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The MP3 player window. The audio/pause/seek logic lives in {@link Mp3Player};
 * this class only builds the Swing UI and forwards user actions to the engine.
 *
 * Layout:
 * <pre>
 *   0:12 ───────●──────────── 3:45     ← draggable timeline + elapsed / total
 *           [ ▶ ]  [ ❚❚ ]  [ ■ ]       ← Java2D-drawn Play / Pause / Stop icons
 * </pre>
 */
public class FgbMusicEp61 extends JFrame {

    // Resolved from the repository root (always launch the app from there).
    private static final String MP3_PATH = "Music/FgbMusicEp61_Fr/FgbMusicEp61.mp3";

    // The timeline slider uses a fixed 0..1000 scale (per-mille of the track);
    // this decouples the UI from the exact frame count.
    private static final int SLIDER_MAX = 1000;

    private final Mp3Player engine = new Mp3Player(MP3_PATH);

    private final JSlider timeline = new JSlider(0, SLIDER_MAX, 0);
    private final JLabel elapsedLabel = new JLabel("0:00");
    private final JLabel totalLabel = new JLabel("0:00");

    // Periodically advances the timeline from the engine's playhead.
    private final Timer uiTimer;

    // True while the user is dragging the slider, so the timer doesn't fight them.
    private boolean userSeeking = false;

    public FgbMusicEp61() {
        setTitle("MP3 Player");
        setSize(360, 150);
        // DISPOSE_ON_CLOSE: closing this player must not terminate the
        // whole launcher (EXIT_ON_CLOSE would kill the entire JVM).
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Closing the window must stop playback too. DISPOSE_ON_CLOSE only
        // disposes the frame; the MP3 plays on a separate background thread,
        // so without this the sound keeps running after the window is gone.
        // Intercept the close event and stop the sound, just like Stop.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });

        // Pre-scan the track so the timeline knows its total length up front.
        try {
            engine.analyze();
            totalLabel.setText(formatTime(engine.getTotalMillis()));
        } catch (Exception ex) {
            ex.printStackTrace(); // e.g. file missing — UI still loads, Play is a no-op
        }

        buildUi();

        // Refresh the timeline ~5×/second from the engine's playhead.
        uiTimer = new Timer(200, e -> refreshTimeline());
        uiTimer.start();
    }

    /** Assembles the timeline row (top) and the icon-button row (centre). */
    private void buildUi() {
        setLayout(new BorderLayout(8, 8));

        // --- Timeline row: elapsed | slider | total ---
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 14));
        top.add(elapsedLabel, BorderLayout.WEST);
        top.add(timeline, BorderLayout.CENTER);
        top.add(totalLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Dragging the slider seeks. ChangeListener fires continuously while
        // dragging (getValueIsAdjusting() == true); we preview the time live and
        // commit the actual seek once the user releases the knob.
        timeline.addChangeListener(e -> {
            if (timeline.getValueIsAdjusting()) {
                userSeeking = true;
                elapsedLabel.setText(formatTime(sliderToMillis(timeline.getValue())));
            } else if (userSeeking) {
                userSeeking = false;
                engine.seek(sliderToFrame(timeline.getValue()));
            }
        });

        // --- Button row: Play / Pause / Stop, drawn as icons ---
        JButton playButton = iconButton(MediaIcon.Kind.PLAY, "Play");
        JButton pauseButton = iconButton(MediaIcon.Kind.PAUSE, "Pause");
        JButton stopButton = iconButton(MediaIcon.Kind.STOP, "Stop");

        playButton.addActionListener(e -> onPlay());
        pauseButton.addActionListener(e -> onPause());
        stopButton.addActionListener(e -> onStop());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        buttons.add(playButton);
        buttons.add(pauseButton);
        buttons.add(stopButton);
        add(buttons, BorderLayout.CENTER);
    }

    /** Builds a square icon button carrying one of the drawn media glyphs. */
    private JButton iconButton(MediaIcon.Kind kind, String tooltip) {
        JButton b = new JButton(new MediaIcon(kind, 16, new Color(0x202020)));
        b.setToolTipText(tooltip);
        b.setFocusable(false);
        b.setPreferredSize(new Dimension(56, 40));
        return b;
    }

    // --- Button actions -----------------------------------------------------

    /** Play = start from the current position, or resume if paused. */
    private void onPlay() {
        if (engine.isPaused()) {
            engine.resume();
        } else if (!engine.isPlaying()) {
            engine.start();
        }
    }

    /** Pause = hold the current position (resume with Play). */
    private void onPause() {
        if (engine.isPlaying() && !engine.isPaused()) {
            engine.pause();
        }
    }

    /** Stop = halt and rewind to the start. */
    private void onStop() {
        engine.stop();
        refreshTimeline();
    }

    /** Stops playback and the UI timer (used when the window closes). */
    private void shutdown() {
        uiTimer.stop();
        engine.stop();
    }

    // --- Timeline syncing ---------------------------------------------------

    /** Pushes the engine's current playhead into the slider + elapsed label. */
    private void refreshTimeline() {
        if (userSeeking) {
            return; // the user is dragging — don't move the knob under them
        }
        int total = engine.getTotalFrames();
        if (total <= 0) {
            return;
        }
        int value = (int) Math.round((double) SLIDER_MAX * engine.getCurrentFrame() / total);
        timeline.setValue(value);
        elapsedLabel.setText(formatTime(engine.getCurrentMillis()));
    }

    /** Converts a slider value (0..SLIDER_MAX) to a frame index. */
    private int sliderToFrame(int sliderValue) {
        return (int) Math.round((double) engine.getTotalFrames() * sliderValue / SLIDER_MAX);
    }

    /** Converts a slider value to a time position in milliseconds (for the live drag preview). */
    private long sliderToMillis(int sliderValue) {
        return (long) (sliderToFrame(sliderValue) * engine.getMsPerFrame());
    }

    /** Formats milliseconds as m:ss (e.g. 72000 → "1:12"). */
    private static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    /**
     * A tiny {@link Icon} that paints the Play / Pause / Stop glyphs with
     * Graphics2D — no image assets, crisp at any size.
     */
    private static final class MediaIcon implements Icon {
        enum Kind { PLAY, PAUSE, STOP }

        private final Kind kind;
        private final int size;
        private final Color color;

        MediaIcon(Kind kind, int size, Color color) {
            this.kind = kind;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            switch (kind) {
                case PLAY: // right-pointing triangle ▶
                    int[] xs = { x, x, x + size };
                    int[] ys = { y, y + size, y + size / 2 };
                    g2.fillPolygon(xs, ys, 3);
                    break;
                case PAUSE: // two vertical bars ❚❚
                    int barWidth = size / 3;
                    g2.fillRect(x, y, barWidth, size);
                    g2.fillRect(x + size - barWidth, y, barWidth, size);
                    break;
                case STOP: // filled square ■
                    g2.fillRect(x, y, size, size);
                    break;
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FgbMusicEp61 gui = new FgbMusicEp61();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}
