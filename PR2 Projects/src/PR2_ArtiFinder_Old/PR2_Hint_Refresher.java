package PR2_ArtiFinder_Old;

import java.util.Timer;

/**
 * Artifact location hint update checker.
 */
class PR2_Hint_Refresher {

    /**
     * Checks for artifact hint updates every 10 seconds.
     * Location: https://pr2hub.com/files/artifact_hint.txt
     * @param args input
     */
    public static void main(String[] args) {
        new Timer().schedule(new CheckArtiState(), 0, 10000);
    }
}
