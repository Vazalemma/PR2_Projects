package unused;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;

public class All_User_Finder {
    private static int i = 3000000; // 5654674

    public static void main(String[] args) {
        BufferedWriter writer;
        try { writer = new BufferedWriter(new FileWriter("users1.txt", true)); } catch (IOException e) { return; }
        while (i < 3000000) {
            try {
                if (i % 10 == 1) {
                    writer.close();
                    writer = new BufferedWriter(new FileWriter("users1.txt", true));
                }
                URL url = new URL("https://pr2hub.com/get_player_info_2.php?user_id=" + i);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                boolean again = false;
                while ((line = in.readLine()) != null) {
                    JSONObject json = new JSONObject(line);
                    if (json.has("error")) {
                        if (json.getString("error").contains("Slow")) again = true;
                    } else {
                        System.out.println(json.getString("name"));
                        writer.write(json.getString("name"));
                        writer.newLine();
                    }
                }
                if (again) {
                    Thread.sleep(100);
                    continue;
                }
                System.out.println(i++);
                Thread.sleep(100);
            } catch (Exception e) {
                try { Thread.sleep(3000); } catch (InterruptedException ie) { return; }
            }
        }
    }
}
