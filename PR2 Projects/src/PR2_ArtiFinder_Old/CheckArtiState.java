package PR2_ArtiFinder_Old;

import javax.sound.sampled.AudioSystem;
import java.text.SimpleDateFormat;
import javax.sound.sampled.Clip;
import org.json.JSONObject;
import java.util.TimerTask;
import java.util.Date;
import java.net.URL;
import java.io.*;

/**
 * Artifact state update announcer.
 */
class CheckArtiState extends TimerTask {


    /**
     * Choose whether or not music gets played upon a trigger.
     * Triggers are: (1) New letter, (2) Someone found it.
     * Choose:
     *   a) true  -> Plays music
     *   b) false -> Doesn't play music
     */
    private static final boolean playMusic = true;

    /**
     * Choose whether or not to search for artifact the moment a new letter appears.
     * Choose:
     *   a) true  -> Search for artifact ASAP
     *   b) false -> Just notify new letter, don't search for it ASAP
     */
    private static final boolean decode = false;



    /**
     * Global variable that checks if artifact has been found.
     */
    private static boolean found = false;

    /**
     * Stored global comparable hint.
     */
    private static String main_hint = "";

    /**
     * This function runs every 10 seconds.
     * A variable named "main_hint" gets stored for comparison.
     * Upon every activation, if:
     *   1) Artifact is found           -> Function no longer does anything
     *   2) Hint is same as "main_hint" -> Nothing happens
     *   3) New letter in hint          -> Searches for artifact (PR2_Hint_Decoder)
     */
    public void run() {
        if (found) System.exit(0);
        try {
            URL url = new URL("https://pr2hub.com/files/artifact_hint.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                if (!json.getString("finder_name").equals("")) {
                    Clip clip = AudioSystem.getClip();
                    System.out.println("ERROR: ARTIFACT FOUND AT "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    if (playMusic) {
                        clip.open(AudioSystem.getAudioInputStream(new File("audio/Stolen.wav")));
                        clip.start();
                    }
                    found = true;
                }
                String hint = json.getString("hint");
                if (main_hint.equals("")) {
                    main_hint = hint;
                    System.out.println(hint);
                }
                if (!main_hint.equals(hint)) {
                    Clip clip = AudioSystem.getClip();
                    main_hint = hint;
                    System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date())+"\n"+hint);
                    if (playMusic) {
                        clip.open(AudioSystem.getAudioInputStream(new File("audio/Letter.wav")));
                        clip.start();
                    }
                    if (decode) PR2_Hint_Decoder.decode(hint);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
