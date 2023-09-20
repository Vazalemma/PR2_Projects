package PR2_ArtiFinder_Old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Artifact hint decoder.
 */
public class PR2_Hint_Decoder {



    /**
     * Insert the entire hint you want to decode.
     * The hint structure is as follows:
     *   [level name] by [username]
     * For example:
     *   Insert -> P____H__t__y__as_____a
     *   Result -> Prop Hunt by vasalemma
     */
    private static final String hint = "I__anna__e__he__uy________by The (______)____";

    /**
     * Insert the method of choice for level searching.
     * Choose:
     *   0 -> Max results        (18 pages, 4000 max)
     *   1 -> Restrictions mode  (Read more on restrictions in Level Decoder)
     *   2 -> Skimming mode      (1 page, 4000 max)
     *   3 -> Deep skim mode     (5 pages, 800 max)
     *   4 -> Contains mode      (Max results, searches for levels containing the string below)
     *   5 -> Username mode      (Skips all level names and only checks for usernames with max results)
     */
    private static final int method = 1;

    /**
     * If search method is 4 (Contains mode), this string will be searched in all level names.
     * This string will be converted to lower case.
     */
    private static final String contains = "Estonia";



    /**
     * Combination mapped list of all possible hints.
     * Basically, looks for places where " by " could be, to separate username and level name.
     */
    private static Map<String, String> hints = new HashMap<>();

    /**
     * List of all levels that fit the given hint.
     * All levels are given in the "[level name] by [username]" structure.
     */
    private static List<String> levels = new ArrayList<>();

    /**
     * This is used to activate the decoding manually.
     * @param args input
     */
    public static void main(String[] args) throws InterruptedException {
        decode(hint);
    }

    /**
     * Decodes the hint by finding all possible and likely hints.
     * It tries to separate the username and level name by locating " by " in the hint.
     * To do that, it looks for letters 'b', 'y', ' ', and 4-letter long underscore substrings "____".
     * NOTE! These are my given rules to a legit hint:
     *   1) Username must be 4 or more characters long
     *   2) Level name must be 4 or more characters long
     *   3) It must contain a legit location to place " by "
     * @param hint The hint that you want to decode
     * @return A long string of possible levels that were found, separated by a semicolon.
     *         Possible result: "[level name 1] by [username1]; [level name 2] by [username2]; "
     */
    public static String decode(String hint) throws InterruptedException {
        hints.clear();
        levels.clear();
        long startTime = System.currentTimeMillis();
        int len = hint.length();
        if (len < 12) return "";
        for (int i = 0; i < len; i++) {
            if (hint.charAt(i) == 'b') {
                if (i > 4 && len - 23 <= i && len - i >= 7) {
                    if (checkb(i)) {
                        String add = hint.substring(0, i - 1);
                        if (!hints.containsKey(add)) {
                            hints.put(add, hint.substring(i + 3));
                        }
                    }
                }
            }
            if (hint.charAt(i) == 'y') {
                if (i > 5 && len - 22 <= i && len - i >= 6) {
                    if (checky(i)) {
                        String add = hint.substring(0, i - 2);
                        if (!hints.containsKey(add)) {
                            hints.put(add, hint.substring(i + 2));
                        }
                    }
                }
            }
            if (hint.charAt(i) == ' ') {
                if (i > 3 && len - 24 <= i && len - i >= 8) {
                    if (checkl(i)) {
                        String add = hint.substring(0, i);
                        if (!hints.containsKey(add)) {
                            hints.put(add, hint.substring(i + 4));
                        }
                    }
                }
                if (i > 6 && len - 21 <= i && len - i >= 5) {
                    if (checkr(i)) {
                        String add = hint.substring(0, i - 3);
                        if (!hints.containsKey(add)) {
                            hints.put(add, hint.substring(i + 1));
                        }
                    }
                }
            }
            if (i <= len - 4) {
                if (hint.substring(i, i + 4).equals("____")) {
                    if (i > 3 && len - 24 <= i && len - i >= 8) {
                        String add = hint.substring(0, i);
                        if (!hints.containsKey(add)) {
                            hints.put(add, hint.substring(i + 4));
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, String> entry : hints.entrySet()) {
            levels.addAll(PR2_Level_Decoder.decode(entry.getKey(), entry.getValue(), method, contains));
        }
        for (Map.Entry<String, String> entry : hints.entrySet()) {
            System.out.println(entry.getKey() + " by " + entry.getValue());
        }
        long endTime = System.currentTimeMillis();
        StringBuilder results = new StringBuilder();
        System.out.println("\nTotal tile elapsed: "+(endTime - startTime) / 1000+" seconds.\nAll matches:\n");
        for (String level : levels) {
            System.out.println(level);
            results.append(level).append("; ");
        }
        return results.toString();
    }

    /**
     * Checks if the right ' ' can be a part of " by ".
     * @param i string index
     * @return true, if " by " can be placed there, otherwise false.
     */
    private static boolean checkr(int i) {
        return !(hint.charAt(i - 1) != '_' && hint.charAt(i - 1) != 'y') && !(hint.charAt(i - 2) != '_' &&
                hint.charAt(i - 2) != 'b') && !(hint.charAt(i - 3) != '_' && hint.charAt(i - 3) != ' ');
    }

    /**
     * Checks if the left ' ' can be a part of " by ".
     * @param i string index
     * @return true, if " by " can be placed there, otherwise false.
     */
    private static boolean checkl(int i) {
        return !(hint.charAt(i + 1) != '_' && hint.charAt(i + 1) != 'b') && !(hint.charAt(i + 2) != '_' &&
                hint.charAt(i + 2) != 'y') && !(hint.charAt(i + 3) != '_' && hint.charAt(i + 3) != ' ');
    }

    /**
     * Checks if 'y' can be a part of " by ".
     * @param i string index
     * @return true, if " by " can be placed there, otherwise false.
     */
    private static boolean checky(int i) {
        return !(hint.charAt(i - 1) != '_' && hint.charAt(i - 1) != 'b') && !(hint.charAt(i - 2) != '_' &&
                hint.charAt(i - 2) != ' ') && !(hint.charAt(i + 1) != '_' && hint.charAt(i + 1) != ' ');
    }

    /**
     * Checks if 'b' can be a part of " by ".
     * @param i string index
     * @return true, if " by " can be placed there, otherwise false.
     */
    private static boolean checkb(int i) {
        return !(hint.charAt(i - 1) != '_' && hint.charAt(i - 1) != ' ') && !(hint.charAt(i + 1) != '_' &&
                hint.charAt(i + 1) != 'y') && !(hint.charAt(i + 2) != '_' && hint.charAt(i + 2) != ' ');
    }
}
