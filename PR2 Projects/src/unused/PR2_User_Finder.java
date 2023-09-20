package unused;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class PR2_User_Finder {
    private static final String link = "https://pr2hub.com/get_player_info_2.php";
    private static final String username = "naruto";
    private static final List<String> alphabet = Arrays.asList("a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "w", "x", "y", "z"
    );
    private static final List<String> all = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "a", "b",
            "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "w", "x",
            "y", "z", "!", "?", "@", ".", ":", ";", "~", "-", "=", " "
    );
    private static int count = 0;
    private static int percent = 0;

    public static void main(String[] args) throws Exception {
        for (int a = 0; a < all.size();) {
            for (int b = 0; b < all.size();) {
                String aa = all.get(a);
                String bb = all.get(b);
                URL url = new URL(link + "?name=" + URLEncoder.encode(aa + username + bb, "UTF-8")); // fullNum("8"+i+""+j, 3)
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                boolean cont = false;
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.contains("Slow down")) cont = true;
                    if (!line.contains("error")) {
                        System.out.println(aa + username + bb); //fullNum("8"+i+""+j, 3)
                    }
                }
                if (cont) continue;
                count++;
                float temp = count * 100.0f / (all.size() * all.size());
                if ((int) temp > percent) {
                    percent = (int) temp;
                    System.out.println(percent + "%");
                }
                b++;
            }
            a++;
        }
    }

    private static String fullNum(int num, int len) {
        StringBuilder zeros = new StringBuilder();
        for (int i = Integer.toString(num).length(); i < len; i++) zeros.append("0");
        return zeros.toString() + num;
    }

    private static String fullNum(String num, int len) {
        return fullNum(Integer.parseInt(num), len);
    }
}
