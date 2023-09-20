package PR2_ArtiFinder_Old;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Decode level and username.
 */
public class PR2_Level_Decoder {



    /**
     * Insert the level name hint you want to decode.
     * For example: _P_B____o_ _R_
     * RESTRICTION:
     *   Case 1: Level name is up to 29 characters long -> Level name must contain at least 2 non-underscore characters
     *   Case 2: Level name is 30 or more characters long -> Level name must contain at least 1 non-underscore character
     */
    private static String hint = "__e_____U_";

    /**
     * Insert the username hint you want to decode.
     * For example: _a_a_e__a
     * RESTRICTION:
     *   Username must contain at least 2 non-underscore characters
     */
    private static String user = "_______w";

    /**
     * Insert the method of choice for level searching.
     * Choose:
     *   0 -> Max results        (18 pages, 4000 max)
     *   1 -> Restrictions mode  (Read more on restrictions in Level Decoder)
     *   2 -> Skimming mode      (1 page, 4000 max)
     *   3 -> Deep skim mode     (5 pages, 800 max)
     *   4 -> Contains mode      (Max results, searches for levels containing the string below)
     *   5 -> Username mode      (Skips all level names and only checks for usernames with max results)
     *   6 -> Custom
     */
    private static final int method = 0;



    /**
     * Choose whether or not you want to apply extra restrictions.
     * The goal of restrictions is to exclude some info for a faster performance.
     * Restrictions include:
     *   (1) Level name non-underscore character count restrictor:
     *       Read more from "hint" variable doc comments above
     *   (2) Automatic match limit and page limit restrictor:
     *       Maximum results per both level name and username hint is 240
     *       Page limit gets chosen automatically dependant on the amount of results
     * Choose:
     *   a) true  -> Apply restrictions
     *   b) false -> Don't apply restrictions
     */
    private static boolean restrictions = false;

    /**
     * Choose whether or not you want extra text to be printed out.
     * Text that gets printed out:
     *   (1) All matching level names
     *   (2) All matching user names
     *   (3) Match counts and time taken
     * Choose:
     *   a) true  -> Print extra info
     *   b) false -> Don't print extra info
     */
    private static final boolean print = true;

    /**
     * Choose whether or not you want a more detailed information on the level count.
     * Normally if a level name comes up multiple times, it will only be added once.
     * Activating this allows you to see how many total results each level name actually has.
     * For example:
     *   Searching "1P Bane of PR2" will give you 1 result either way, but...
     *   Searching "traps" will give you 1 result normally, but show it got 6800+ results in total.
     * This can help you figure out if you missed a (lot of) level(s), perhaps unsearchable ones.
     * Choose:
     *   a) true  -> Prints out detailed results
     *   b) false -> Doesn't print out detailed results
     */
    private static final boolean counters = true;



    /**
     * !WARNING! This has no effect if restrictions are turned on.
     * Choose the amount of pages to search per result (both level name and username).
     * Choose:
     *   A number between 1 and 18 (edges included).
     */
    private static int pagelimit = 18;

    /**
     * !WARNING! This has no effect if restrictions are turned on.
     * Choose the maximum amount of matches allowed (both level name and username).
     * PS! If the amount of results exceeds this number, NONE of the results will be searched.
     * Choose:
     *   A number between 1 and 4000 (edges included).
     */
    private static int matchlimit = 4000;



    /**
     * Checker for if the searcher is in Contains mode.
     * If this is True, then the searcher will only check if level name contains a string.
     */
    private static boolean containMode;

    /**
     * If the search is in Contains mode, this is the string that will be checked.
     */
    private static String containWord;

    /**
     * Checker for if the searcher is in Username mode.
     * If this is True, all level names will be skipped, and only usernames will be considered.
     */
    private static boolean usernameMode;



    /**
     * In worst case scenarios, level name can have underscores in it (username fortunately can't).
     * Due to the way hints work, unknown characters are replaced by underscores.
     * That means you don't know which underscores have been "revealed".
     * If you're confident the level name has underscores in it, specify the amount you suspect.
     * Leave it at 0 if you're uncertain.
     */
    private static final int underscoreCount = 0;



    /**
     * A map to store all detailed information on each matching result.
     * Used to store data if "counters" is set to 'true'.
     */
    private static HashMap<String, Integer> levelmap = new HashMap<>();

    /**
     * The body of the link where all the level searching is done.
     */
    private static final String link = "https://pr2hub.com/search_levels.php";

    /**
     * A map of all the non-underscore characters found in the level name hint.
     */
    private static Map<Integer, Character> levelchars = new HashMap<>();

    /**
     * A map of all the non-underscore characters found in the username hint.
     */
    private static Map<Integer, Character> userchars = new HashMap<>();

    /**
     * A list of all the matching "[level name] by [username]" results.
     */
    private static List<String> matchingLevels = new ArrayList<>();

    /**
     * A list of all the matching level names.
     */
    private static List<String> levels = new ArrayList<>();

    /**
     * A list of all the matching usernames.
     */
    private static List<String> users = new ArrayList<>();

    /**
     * Total count of all matching "[level name] by [username]" results.
     */
    private static int count = 0;

    /**
     * Total count of all matching results (both level name and username, separately).
     */
    private static int tempcount = 0;

    /**
     * Main function, used to activate the program manually.
     * @param args input
     */
    public static void main(String[] args) {
        setMethod(method, containWord);
        analyse(hint, levelchars);
        analyse(user, userchars);
        long startTime = System.currentTimeMillis();
        processLevels();
        searchMatchingUser();
        long endTime = System.currentTimeMillis();
        if (print) printResults((endTime - startTime) / 1000);
        startTime = System.currentTimeMillis();
        processUsers();
        searchMatchingLevel();
        endTime = System.currentTimeMillis();
        if (print) printResults((endTime - startTime) / 1000);
        if (counters) {
            for(Map.Entry<String, Integer> ent : levelmap.entrySet()) {
                if (ent.getValue() > 2) {
                    System.out.println(ent.getKey() + " --- " + ent.getValue());
                }
            }
        }
    }

    /**
     * Prints out the total amount of "[level name] by [username]" results in a nice format.
     * @param time The amount of time it took to find all results
     */
    private static void printResults(long time) {
        System.out.println("\nPerfectly matching levels: "+count+"\nTime taken: "+time+" seconds.\n");
    }

    /**
     * When all matching level names have been found, it's time to search them to find all
     * the matching usernames. If there are more level name results than allowed,
     * it will be skipped, otherwise it will search from 1 to 25 pages.
     */
    private static void searchMatchingUser() {
        if (print) System.out.println("\nMatching level names: " + tempcount + "\n");
        if (restrictions) {
            if (tempcount > 240) return;
            if (tempcount > 60) pagelimit = 15;
            if (tempcount > 120) pagelimit = 10;
            if (tempcount > 180) pagelimit = 5;
        } else {
            if (tempcount > matchlimit) return;
        }
        try {
            for (String level : levels) {
                int i = 1;
                boolean repeat = true;
                while (repeat && i < pagelimit + 1) {
                    repeat = false;
                    HttpURLConnection http = (HttpURLConnection) new URL(link).openConnection();
                    sendPostRequest(http, "title", "\"" + level + "\"", i);
                    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        repeat = true;
                        Map<String, String> params = getLevels(line, true);
                        if (params == null) {
                            Thread.sleep(140);
                            continue;
                        }
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            String username = java.net.URLDecoder.decode(entry.getKey(), "UTF-8");
                            String levelname = java.net.URLDecoder.decode(entry.getValue(), "UTF-8");
                            if (analyseUser(username, user.length(), userchars) && levelname.equals(level)) {
                                count++;
                                String result = levelname + " by " + username;
                                if (!matchingLevels.contains(result)) {
                                    matchingLevels.add(result);
                                    System.out.println(result);
                                }
                            }
                        }
                    }
                    i++;
                    in.close();
                    if (i == 2 && !repeat) System.out.println(" --- ZERO RESULTS : " + level + " --- ");
                }
                // if (i == 26) System.out.println(" --- PAGE LIMIT : " + level + " --- ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When all matching usernames have been found, it's time to search them to find all
     * their matching level names. If there are more username results than allowed,
     * it will be skipped, otherwise it will search from 1 to 25 pages.
     */
    private static void searchMatchingLevel() {
        count = 0;
        if (print) System.out.println("\nMatching user names: " + tempcount + "\n");
        if (restrictions) {
            if (tempcount > 200) return;
            if (tempcount > 40) pagelimit = 15;
            if (tempcount > 80) pagelimit = 10;
            if (tempcount > 120) pagelimit = 5;
            if (tempcount > 160) pagelimit = 1;
        } else {
            if (tempcount > matchlimit) return;
        }
        try {
            for (String username : users) {
                int i = 1;
                boolean repeat = true;
                while (repeat && i < pagelimit + 1) {
                    repeat = false;
                    HttpURLConnection http = (HttpURLConnection) new URL(link).openConnection();
                    sendPostRequest(http, "user", username, i);
                    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        repeat = true;
                        Map<String, String> params = getLevels(line, false);
                        if (params == null) {
                            Thread.sleep(140);
                            continue;
                        }
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            String userName = java.net.URLDecoder.decode(entry.getValue(), "UTF-8");
                            String levelName = java.net.URLDecoder.decode(entry.getKey(), "UTF-8");
                            if (analyseLevelName(levelName, hint.length(), levelchars) && userName.equals(username)) {
                                count++;
                                String result = levelName + " by " + userName;
                                if (!matchingLevels.contains(result)) {
                                    matchingLevels.add(result);
                                    System.out.println(result);
                                }
                            }
                        }
                        i++;
                    }
                    in.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This this sends a post requests, because PR2 no longer accepts GET requests.
     * Hurray for more code!
     * @param http HTTP connection
     * @param mode search mode, either "title" or "user"
     * @param search search string
     * @param page page number
     * @throws Exception if anything goes wrong
     */
    private static void sendPostRequest(HttpURLConnection http, String mode, String search, int page) throws Exception {
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        HashMap<String,String> arguments = new HashMap<>();
        arguments.put("mode", mode);
        arguments.put("search_str", search);
        arguments.put("page", Integer.toString(page));
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String,String> entry : arguments.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }

    /**
     * Gets all the level name and username data of a single page in the search engine.
     * @param query the returned query data after searching for levels on a single page
     * @param isLevel checks whether the search type was by level name or username
     * @return a map of all the search results on a single page, including only the level name and username
     */
    private static Map<String, String> getLevels(String query, boolean isLevel) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        String level = "";
        String user;
        for (String param : params) {
            String key = param.split("=")[0];
            if (key.contains("error")) return null;
            if (key.contains("title")) {
                level = param.split("=")[1];
            }
            if (key.contains("userName")) {
                user = param.split("=")[1];
                if (isLevel) map.put(user, level);
                else map.put(level, user);
            }
        }
        return map;
    }

    /**
     * Creates a map of all non-underscore characters in the given hint.
     * @param string the hint of either username of level name
     * @param map the map to save all the data to
     */
    private static void analyse(String string, Map<Integer, Character> map) {
        for (int i = 0, n = string.length(); i < n; i++) {
            char c = string.charAt(i);
            if (c != '_') map.put(i, c);
        }
    }

    /**
     * Loops through the 30 files of level names to find
     * all the ones that match with the level name hint.
     */
    private static void processLevels() {
        pagelimit = Math.max(1, Math.min(18, pagelimit));
        if (method != 6) matchlimit = Math.max(1, Math.min(4000, matchlimit));
        if (restrictions) {
            if ((hint.length() < 30 && levelchars.size() < 2) || (hint.length() >= 30 && levelchars.size() < 1)) return;
        }
        for (int i = 0; i <= 30; i++) {
            try (BufferedReader br = new BufferedReader(new FileReader("C:/xampp/cgi-bin/artifact/levels"+i+".txt"))) {
                String level;
                while ((level = br.readLine()) != null) analyseLevel(level);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loops through the username file to find all
     * the ones that match with the username hint.
     */
    private static void processUsers() {
        if (!matchingLevels.isEmpty()) return;
        tempcount = 0;
        pagelimit = 18;
        if (userchars.size() < 1) return;
        if (userchars.size() == user.length()) {
            if (print) System.out.println(user);
            users.add(user);
            tempcount++;
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader("usernames.txt"))) {
            String username;
            while ((username = br.readLine()) != null) {
                if (analyseUser(username, user.length(), userchars)) {
                    if (print) System.out.println(username);
                    users.add(username);
                    tempcount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the given level name matches with the level name hint.
     * Upon a match, it gets saved, and can be added to the detailed level list.
     * @param level a level name from one of the level files
     */
    private static void analyseLevel(String level) {
        if (usernameMode) return;
        if (level.length() == hint.length()) {
            if ((!containMode && check(level, levelchars) && getNumberOfUnderscores(level) >= underscoreCount) ||
                    (containMode && level.contains(containWord))) {
                if (counters) {
                    String lowlvl = level.toLowerCase();
                    if (!levelmap.containsKey(lowlvl)) {
                        levelmap.put(lowlvl, 1);
                    } else {
                        int i = levelmap.get(lowlvl);
                        levelmap.remove(lowlvl);
                        levelmap.put(lowlvl, i+1);
                    }
                }
                if (!levels.contains(level)) {
                    if (print) {
                        System.out.println(level);
                    }
                    levels.add(level);
                    tempcount++;
                }
            }
        }
    }

    /**
     * Counts the amount of underscores in the level name.
     * @param level level name
     * @return number of underscores in the level
     */
    private static int getNumberOfUnderscores(String level) {
        int count = 0;
        for (int i = 0; i < level.length(); i++) if (level.charAt(i) == '_') count++;
        return count;
    }

    /**
     * Checks whether or not a username from the username file matches the username hint.
     * @param user username to check
     * @param length username hint length
     * @param map username hint character map
     * @return true if username matches the username hint, otherwise false
     */
    private static boolean analyseUser(String user, int length, Map<Integer, Character> map) {
        return user.length() == length && check(user, map);
    }

    /**
     * Checks whether or not a level name from one of the level name files matches the level name hint.
     * @param level level name to check
     * @param length level name hint length
     * @param map level name hint character map
     * @return true if level name matches the level name hint, otherwise false
     */
    private static boolean analyseLevelName(String level, int length, Map<Integer, Character> map) {
        return level.length() == length && check(level, map);
    }

    /**
     * Check whether or not the given string matches the given character map
     * @param string either a level name or a username
     * @param map either username or level name character map
     * @return true if all characters match, otherwise false
     */
    private static boolean check(String string, Map<Integer, Character> map) {
        for (Map.Entry<Integer, Character> entry : map.entrySet()) {
            if (string.charAt(entry.getKey()) != entry.getValue()) return false;
        }
        return true;
    }

    /**
     * Activation function for PR2_Hint_Decoder.
     *
     * @param title   level name hint
     * @param creator username hint
     * @return list of all matching "[level name] by [username]" results
     */
    static List<String> decode(String title, String creator, int method, String contains) throws InterruptedException {
        hint = title;
        user = creator;
        count = 0;
        tempcount = 0;
        levelchars.clear();
        userchars.clear();
        levels.clear();
        users.clear();
        matchingLevels.clear();
        setMethod(method, contains);
        analyse(hint, levelchars);
        analyse(user, userchars);
        long startTime = System.currentTimeMillis();
        processLevels();
        searchMatchingUser();
        long endTime = System.currentTimeMillis();
        if (print) printResults((endTime - startTime) / 1000);
        Thread.sleep(5000);
        startTime = System.currentTimeMillis();
        processUsers();
        searchMatchingLevel();
        endTime = System.currentTimeMillis();
        if (print) printResults((endTime - startTime) / 1000);
        return matchingLevels;
    }

    /**
     * Different searching methods for different ways to search for levels.
     * Each method has their own presets.
     * @param method method number
     * @param contains contains mode
     */
    private static void setMethod(int method, String contains) {
        switch (method) {
            case 0:
                restrictions = false;
                pagelimit = 18;
                matchlimit = 4000;
                containMode = false;
                usernameMode = false;
                return;
            case 1:
                restrictions = true;
                containMode = false;
                usernameMode = false;
                return;
            case 2:
                restrictions = false;
                pagelimit = 1;
                matchlimit = 4000;
                containMode = false;
                usernameMode = false;
                return;
            case 3:
                restrictions = false;
                pagelimit = 5;
                matchlimit = 800;
                containMode = false;
                usernameMode = false;
                return;
            case 4:
                restrictions = false;
                pagelimit = 18;
                matchlimit = 4000;
                containMode = true;
                containWord = contains;
                usernameMode = false;
                return;
            case 5:
                restrictions = false;
                pagelimit = 18;
                matchlimit = 4000;
                containMode = false;
                usernameMode = true;
            case 6:
                restrictions = false;
                pagelimit = 2;
                matchlimit = 6000;
                containMode = false;
                usernameMode = false;
        }
    }
}
