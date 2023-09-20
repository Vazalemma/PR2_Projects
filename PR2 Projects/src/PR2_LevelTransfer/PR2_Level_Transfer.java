package PR2_LevelTransfer;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Level transferer.
 */
public class PR2_Level_Transfer {


    /**
     * Your player token. (Network Tab)
     * Updates every time you log out or go to level editor.
     */
    private static final String token = "1333985-b50055d51974150e4e2b3e04c68caa";

    /**
     * Your username where you want the level to be published.
     */
    private static final String userName = "vasalemma";

    /**
     * The level ID of the level you want to copy.
     */
    private static final String levelCode = "6511383";

    /**
     * Cowboy chance. A number between 0 and 100,
     * Where 0 means never and 100 means always.
     */
    private static final String cowboyChance = "0";

    /**
     * This determines whether the level is published or not.
     * 0 means unpublished, 1 means published.
     */
    private static final String live = "0";

    /**
     * The description of the level.
     * Leave empty if you want to take it directly from the level.
     */
    private static String note = "Normal";

    /**
     * Choose which items you want to be allowed in the level.
     * Change the number at the end.
     * 1 means the item is allowed,
     * 0 means it's not allowed.
     */
    private static void setItems() {
        itemcheck.put("7 Speed Burst", 1);
        itemcheck.put("5  Super Jump", 1);
        itemcheck.put("3   Lightning", 1);
        itemcheck.put("1   Laser Gun", 1);
        itemcheck.put("9    Ice Wave", 1);
        itemcheck.put("6    Jet Pack", 1);
        itemcheck.put("4    Teleport", 1);
        itemcheck.put("8       Sword", 1);
        itemcheck.put("2        Mine", 1);
    }



    /**
     * Map that determines whether an item is allowed or not.
     */
    private static Map<String, Integer> itemcheck = new HashMap<>();

    /**
     * Level data. Automatic. Can be custom.
     */
    private static String data = ""; // getData();

    private static String getData() {
        try {
            StringBuilder level = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader("textfiles/level.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                level.append(line);
            }
            return level.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Level hash code. Automatic.
     */
    private static String hash = "";

    /**
     * Level items. Automatic.
     */
    private static String items = "";

    /**
     * Maximum time limit for the level. Automatic.
     */
    private static String max_time = "";

    /**
     * Level gravity. Automatic.
     */
    private static String gravity = "";

    /**
     * Level title. Automatic.
     */
    private static String title = "";

    /**
     * Mai method used to start the process.
     * @param args input
     */
    public static void main(String[] args) {
        doLevelTransferFromAlreadyExistingFile();
    }

    /**
     * This method decodes all the items you want used in the level.
     * It gets formed into a string that looks something like "1´4´5´8".
     */
    private static void decodeItems() {
        StringBuilder itemBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : itemcheck.entrySet()) {
            if (entry.getValue() == 1) {
                itemBuilder.append(entry.getKey().substring(0, 1)).append("`");
            }
        }
        if (items.length() > 0) {
            items = itemBuilder.toString().substring(0, items.length() - 1);
        }
    }

    /**
     * This method calculates the MD5 hash for the level.
     * This is required, without this a level is not allowed to be published.
     * @param input string to be hashed
     */
    private static void encodeMD5(String input) {
        StringBuilder hashText = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(input.getBytes());
            byte[] digest = md5.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hashText = new StringBuilder(bigInt.toString(16));
            while(hashText.length() < 32 ) {
                hashText.insert(0, "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hash = hashText.toString();
    }

    /**
     * This method gets all the automatic level variables described above.
     * @param code level ID
     */
    private static void getLevelVars(String code) {
        try {
            URL url = new URL("https://pr2hub.com/levels/" + code + ".txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder data = new StringBuilder();
            while ((line = in.readLine()) != null) data.append(line).append("\n");
            decodeLevelVars(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method decodes the level variables from the URL query
     * And sets all the variables in place.
     * @param query level data query
     */
    private static void decodeLevelVars(String query) {
        String[] params = query.split("&");
        for (String param : params) {
            String key = param.split("=")[0];
            if (key.equals("max_time") && max_time.equals("")) max_time = param.split("=")[1];
            if (key.equals("gravity")) gravity = param.split("=")[1];
            if (key.equals("title")) title = param.split("=")[1];
            if (key.equals("data") && data.equals("")) data = param.split("=", 2)[1];
            if (key.equals("note") && note.equals("")) {
                note = param.split("=")[1];
            }
        }
    }

    /**
     * This method uploads the level through POST-request.
     */
    private static void sendRequest() {
        try {
            URL url = new URL("https://pr2hub.com/upload_level.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            Map<String, String> arguments = setArguments();
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
            System.out.println(http.getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes all the now properly set arguments above
     * And converts it into a map to be inserted into the POST-request.
     * Some variables are already set in stone. They can be changed,
     * But I decided they should be left like this, for now.
     * @return map of all arguments
     */
    private static Map<String, String> setArguments() {
        Map<String, String> map = new HashMap<>();
        map.put("gameMode", "race");
        map.put("rand", "6235436");
        map.put("min_level", "0");
        map.put("passHash", "");
        map.put("hasPass", "0");
        map.put("credits", "");
        map.put("song", "");
        map.put("cowboyChance", cowboyChance);
        map.put("token", token);
        map.put("items", items);
        map.put("note", note);
        map.put("live", live);
        map.put("max_time", max_time);
        map.put("gravity", gravity);
        map.put("title", title);
        map.put("data", data);
        map.put("hash", hash);
        return map;
    }

    /**
     * This method executes all necessary procedures in the right order.
     */
    private static void doLevelTransferFromAlreadyExistingFile() {
        getLevelVars(levelCode);
        setItems();
        decodeItems();
        encodeMD5(title + userName.toLowerCase() + data + "84ge5tnr");
        sendRequest();
    }
}
