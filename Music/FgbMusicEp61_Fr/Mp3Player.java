package Music.FgbMusicEp61_Fr; // Same package as the player window.

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * A small, Swing-agnostic MP3 playback engine that adds <b>pause</b> and
 * <b>seek</b> on top of the vendored JLayer library.
 *
 * <p>JLayer's plain {@code Player} can only play a stream start-to-finish — it
 * has no pause and no random-access seek. The trick used here is to decode the
 * file <b>one MPEG frame at a time</b> and keep a {@code currentFrame} playhead:
 * <ul>
 *   <li><b>Pause</b> simply stops feeding frames to the audio device while
 *       keeping the same stream open, so resuming is instant and gap-free.</li>
 *   <li><b>Seek</b> reopens the file and skips frames up to the target frame.
 *       Because MP3 frames have a (near-)constant duration, frame number maps
 *       directly to a time position. Reopening is the only operation that
 *       causes a tiny (sub-second) audio gap — this is inherent to JLayer.</li>
 * </ul>
 *
 * <p>All playback happens on a single background thread; the public methods are
 * meant to be called from the Swing Event Dispatch Thread. State shared across
 * the two threads is {@code volatile}.
 */
public class Mp3Player {

    /**
     * Minimal subclass that exposes JLayer's per-frame primitives.
     * {@code decodeFrame()} and {@code skipFrame()} are {@code protected} in
     * {@link AdvancedPlayer}, so a subclass can call them on itself even though
     * we live in a different package.
     */
    private static final class FramePlayer extends AdvancedPlayer {
        FramePlayer(InputStream in) throws Exception {
            super(in);
        }

        /** Decode + play exactly one frame. @return false when the stream is exhausted. */
        boolean decodeOne() throws Exception {
            return decodeFrame();
        }

        /** Skip exactly one frame without playing it (used to fast-forward to a seek target). */
        boolean skipOne() throws Exception {
            return skipFrame();
        }
    }

    private final String filePath;

    // Track metadata, filled in by analyze().
    private int totalFrames;     // total number of MPEG frames in the file
    private double msPerFrame;   // duration of a single frame, in milliseconds

    // Shared playback state (touched by both the UI thread and the play thread).
    private volatile int currentFrame;       // the playhead, in frames
    private volatile boolean paused;          // true while paused (stream stays open)
    private volatile boolean stopRequested;   // asks the play loop to exit
    private volatile int seekTarget = -1;     // pending seek target frame, or -1 if none
    private volatile FramePlayer player;      // the live JLayer player (null when idle)
    private Thread thread;                     // the background playback thread

    public Mp3Player(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Pre-scans the file once to learn its length: counts every frame and reads
     * the per-frame duration from the first header. Cheap for a small file, and
     * it gives the UI the total time and a frame-count to map the timeline onto.
     */
    public void analyze() throws Exception {
        try (InputStream in = new BufferedInputStream(new FileInputStream(filePath))) {
            Bitstream bitstream = new Bitstream(in);
            int frames = 0;
            double ms = 0;
            Header h;
            while ((h = bitstream.readFrame()) != null) {
                if (frames == 0) {
                    ms = h.ms_per_frame(); // frame duration is constant for CBR
                }
                frames++;
                bitstream.closeFrame();
            }
            bitstream.close();
            totalFrames = frames;
            msPerFrame = ms;
        }
    }

    /** Starts (or restarts) playback from the current playhead on a background thread. */
    public synchronized void start() {
        if (thread != null && thread.isAlive()) {
            return; // already playing — ignore
        }
        stopRequested = false;
        paused = false;
        thread = new Thread(this::run, "Mp3Player");
        thread.setDaemon(true); // never block JVM shutdown
        thread.start();
    }

    /** The playback loop: decode one frame per iteration, honouring pause/seek/stop. */
    private void run() {
        boolean completed = false;
        try {
            openAt(currentFrame);
            while (!stopRequested) {
                // Consume a pending seek by reopening the stream at the new frame.
                int target = seekTarget;
                if (target >= 0) {
                    seekTarget = -1;
                    currentFrame = target;
                    openAt(currentFrame);
                }
                // While paused, idle without decoding (keeps the stream open).
                if (paused) {
                    Thread.sleep(40);
                    continue;
                }
                if (!player.decodeOne()) {
                    completed = true; // reached the end of the track
                    break;
                }
                currentFrame++;
            }
        } catch (InterruptedException ignored) {
            // stop() interrupted us — fall through and clean up.
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closePlayer();
            if (completed) {
                currentFrame = 0; // rewind so the next Play starts from the beginning
            }
        }
    }

    /** Closes any current player and opens a fresh one positioned at {@code frame}. */
    private void openAt(int frame) throws Exception {
        closePlayer();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        FramePlayer p = new FramePlayer(in);
        for (int i = 0; i < frame; i++) {
            if (!p.skipOne()) {
                break; // ran past the end — nothing left to skip
            }
        }
        player = p;
    }

    /** Closes the live player, if any. JLayer's close() is safe to call from any thread. */
    private void closePlayer() {
        FramePlayer p = player;
        if (p != null) {
            player = null;
            p.close();
        }
    }

    /** Pauses playback (no-op if not playing). The stream stays open for instant resume. */
    public void pause() {
        paused = true;
    }

    /** Resumes from a paused state. */
    public void resume() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    /** True while the background playback thread is alive (playing or paused). */
    public boolean isPlaying() {
        return thread != null && thread.isAlive();
    }

    /**
     * Seeks to the given frame. While playing this is queued for the play loop
     * (which reopens the stream); while stopped it just moves the playhead so
     * the next Play starts there.
     */
    public void seek(int frame) {
        int clamped = clampFrame(frame);
        if (isPlaying()) {
            seekTarget = clamped;
        } else {
            currentFrame = clamped;
        }
    }

    /** Stops playback and rewinds to the start. */
    public synchronized void stop() {
        stopRequested = true;
        paused = false;
        closePlayer();       // closing the audio device unblocks any in-flight decode
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        currentFrame = 0;
    }

    private int clampFrame(int frame) {
        if (frame < 0) {
            return 0;
        }
        if (totalFrames > 0 && frame > totalFrames) {
            return totalFrames;
        }
        return frame;
    }

    // --- Read-only accessors used by the UI to render the timeline ---

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public double getMsPerFrame() {
        return msPerFrame;
    }

    public long getCurrentMillis() {
        return (long) (currentFrame * msPerFrame);
    }

    public long getTotalMillis() {
        return (long) (totalFrames * msPerFrame);
    }
}
