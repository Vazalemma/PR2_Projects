package PR2_ArtiFinder_Old;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class UserGenerator {


    private static final List<Character> chars = Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n',
            'o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9');
    private static final List<Character> letters = Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z');
    private static final List<Character> consonants = Arrays.asList('b','c','d','f','g','h','j','k','l','m','n','p',
            'q','r','s','t','v','w','x','y','z');
    private static final List<Character> special = Arrays.asList('b','c','d','h','l','n','r','s','t');
    private static final List<Character> commonConsonants = Arrays.asList('b','c','d','h','k','l','m','n','p','r','s','t');
    private static final List<Character> vowels = Arrays.asList('a','e','i','o','u','y');
    private static final List<Character> numbers = Arrays.asList('0','1','2','3','4','5','6','7','8','9');
    private static final List<Character> empty = Collections.singletonList('?');

    private static final List<String> results = new ArrayList<>();

    private static final String base = "https://pr2hub.com/get_player_info_2.php?name=";

    public static void main(String[] args) throws Exception {
        for (char a : special) {
            for (char b : chars) {
                for (char c : chars) {
                    for (char d : empty) {
                        String user = "" + a + "obby" + b + c;
                        URL url = new URL(base + user);
                        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                        String response;
                        while ((response = br.readLine()) != null) {
                            if (!response.contains("error")) {
                                System.out.println(user);
                                results.add(response);
                            } else {
                                System.out.println("Error: " + user);
                            }
                        }
                    }
                }
            }
        }
        for (String user : results) {
            PR2_Hint_Decoder.decode("_______" + " by " + user);
        }
    }
}
