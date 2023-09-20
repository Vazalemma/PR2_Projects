package PR2_Level_DB_Downloader;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;

public class PR2_Level_List_Updater implements Runnable {
    private PR2_Level_Database db;

    PR2_Level_List_Updater(PR2_Level_Database db) {
        this.db = db;
    }

    @Override
    public void run() {
        try {
            doYourThing();
        } catch (Exception e) {
            System.out.println("Thread was interrupted");
        }
    }

    private void doYourThing() {
        int i;
        while ((i = db.getNextIndex()) < 6500000 && !Thread.interrupted()) {
            System.out.println(i);
            boolean saveUser = false;
            String content = getContentFromLevelFile(i);
            if (content.equals("")) continue;
            int userID = getUserID(content);
            if (userID < 0) continue;
            String user = db.getUserName(userID);
            if (user == null) continue;
            if (user.equals("")) {
                saveUser = true;
                user = findUserByID(userID);
            }
            if (user.equals("")) continue;
            if (saveUser) {
                db.saveuser(userID, user);
            }
            String levelName = getLevel(content);
            if (levelName.equals("")) continue;
            db.addLevel(levelName + " by " + user);
            System.out.println(levelName + " by " + user);
        }
    }

    private static String findUserByID(int userID) {
        try {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < 10000) {
                URL url = new URL("https://pr2hub.com/get_player_info_2.php?user_id=" + userID);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject json = new JSONObject(line);
                    if (!json.has("error")) return json.getString("name");
                }
                Thread.sleep(100);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private static String getLevel(String content) {
        try {
            String name = content.split("title=")[1].split("&")[0];
            return URLDecoder.decode(name, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private static int getUserID(String content) {
        try {
            String number = content.split("user_id=")[1].split("&")[0];
            return Integer.parseInt(number);
        } catch (Exception e) {
            return -1;
        }
    }

    private static String getContentFromLevelFile(int i) {
        try {
            URL url = new URL("https://pr2hub.com/levels/" + i + ".txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) content.append(line);
            return content.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
