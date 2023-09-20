package PR2_ArtiFinder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PR2_ArtiFinder_Controller {


    /**
     * Map of revealed letters in the hint.
     */
    private static final Map<Integer, Character> HINT_LETTERS = new HashMap<>();

    /**
     * The hint you're trying to decode.
     */
    private static final String hint = "__g__n____________________p_3";

    /**
     * Manual shortening decision. Mandatory for true artifact hints.
     */
    private static final boolean shorten = false;






    /**
     * Main method used for activating the code.
     * @param args are useless here
     */
    public static void main(String[] args) throws Exception {
        decodeLetters();
        printMatchingLevels();
        /*BufferedReader br = new BufferedReader(new FileReader("textfiles/fulllevellist.txt"));
        String s;
        while ((s = br.readLine()) != null) {
            String u = s.contains(" by ") ? s.split(" by ")[1] : "";
            int i = u.length();
            if (u.length() >= 1 && u.charAt(i - 1) == 'a') System.out.println(u);
        }*/
    }

    /**
     * Decodes lever letters and saves them in a map.
     */
    private static void decodeLetters() {
        for (int i = 0, n = hint.length(); i < n; i++) {
            char c = hint.charAt(i);
            if (c != '_') HINT_LETTERS.put(i, c);
        }
    }

    /**
     * Prints matching levels.
     */
    private static void printMatchingLevels() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("textfiles/fulllevellist.txt"));
            String level;
            int count = 0;
            while ((level = br.readLine()) != null) {
                if (decideToShorten(level)) level = shortenTo50Chars(level);
                if (level == null) continue;
                if (!lengthMatches(level)) continue;
                if (!lettersMatch(level)) continue;
                System.out.println(level);
                count++;
            }
            System.out.println("\nLevel count: " + count);
        } catch (Exception e) {
            System.out.println("- Could not read fulllevellist.txt");
        }
    }

    /**
     * Decision to shorten a level title length.
     * @param level current level
     * @return true/false
     */
    private static boolean decideToShorten(String level) {
        return shorten && hint.length() >= 56 && level.length() >= 56;
    }

    /**
     * PR2 only shows the first 50 characters of a level title.
     * If the decision to shorten is met here, the level title length might get decreased here as well.
     * @param level current level
     * @return potentially shortened version of the current level
     */
    private static String shortenTo50Chars(String level) {
        if (level.split(" by ").length < 2) {
            System.out.println("- Broken level: " + level);
            return null;
        }
        Map<String, String> splits = getSplits(level);
        if (splits.size() > 1) return shortenTo50CharsSpecialCase(level, splits);
        String title = level.split(" by ")[0];
        String user = level.split(" by ")[1];
        if (title.length() >= 50) title = title.substring(0, 50);
        return title + " by " + user;
    }

    /**
     * Splits the level by every instance of " by " an puts it into a map.
     * @param level current level
     * @return map of all splits
     */
    private static Map<String, String> getSplits(String level) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < level.length() - 4; i++) {
            if (level.substring(i, i + 4).equals(" by ")) {
                map.put(level.substring(0, i), level.substring(i + 4));
                i += 2;
            }
        }
        return map;
    }

    /**
     * Special case for level shortening.
     *  - Splits by every instance of " by "
     *  - Sees if any instance has a username that exists
     *  - If it finds one suitable username, then we're good
     *  - Otherwise decoding this would become a bit too complicated,
     *      so we'll leave you to decode it manually instead
     * @param level level to shorten
     * @param splits all spits of " by "
     * @return Shortened level
     */
    private static String shortenTo50CharsSpecialCase(String level, Map<String, String> splits) {
        try {
            List<String> users = new ArrayList<>();
            for (String username : splits.values()) {
                long t = System.currentTimeMillis();
                while (System.currentTimeMillis() - t > 4000) {
                    URL url = new URL("https://pr2hub.com/get_player_info_2.php?name=" + username);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    boolean cont = true;
                    while ((line = in.readLine()) != null) {
                        JSONObject json = new JSONObject(line);
                        if (!json.has("error")) {
                            users.add(username);
                            cont = false;
                        }
                    }
                    if (!cont) break;
                }
            }
            if (users.size() == 1) {
                for (Map.Entry<String, String> entry : splits.entrySet()) {
                    if (entry.getValue().equals(users.get(0))) return entry.getKey().substring(0, 50) + " by " + users.get(0);
                }
            }
            System.out.println("Level 50-Char Shortening Special Case Over Complication Error:\n -> " + level);
            return level;
        } catch (Exception e) {
            return level;
        }
    }

    /**
     * Checks if current level length matches hint length.
     * @param level current level
     * @return true/false
     */
    private static boolean lengthMatches(String level) {
        return level.length() == hint.length();
    }

    /**
     * Checks if hint characters match with the ones of the current level.
     * @param level current level
     * @return true/false
     */
    private static boolean lettersMatch(String level) {
        for (Map.Entry<Integer, Character> entry : HINT_LETTERS.entrySet()) {
            if (level.charAt(entry.getKey()) != entry.getValue()) return false;
        }
        return true;
    }
}
