package PR2_QuizBot;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.io.BufferedReader;
import org.json.JSONException;
import java.io.OutputStream;
import java.net.URLEncoder;
import org.json.JSONObject;
import java.net.URL;
import java.util.*;

/**
 * QuizBot's inner system.
 */
public class QuizBotPMReader extends TimerTask {

    /**
     * QuizBot player token.
     */
    private static final String token = "5641707-166b7f2c5088d583997e44bf52a12946433773c7";

    /**
     * Prize: Artifact location.
     * Change this to the newest location.
     */
    private static final String location = "zdgnmrhjyjgmykh by sp898";

    /**
     * Last PM ID.
     * This is to assure every PM gets read exactly once.
     */
    private static String lastMessageID = "";

    /**
     * List of all questions.
     */
    private static List<Question> questions = new ArrayList<>();

    /**
     * List of all contestants.
     */
    private static List<Contestant> contestants = new ArrayList<>();

    /**
     * List of all new PMs that VazaBot got.
     */
    private static Map<String, String> gotPMs = new HashMap<>();

    /**
     * List of all PMs VazaBot has to send.
     */
    private static Map<String, String> sendPMs = new HashMap<>();

    /**
     * List of all QuizBot Responder tokens.
     * There are currently 25 responders that answer to PMs.
     */
    private static List<String> tokens = new ArrayList<>();

    /**
     * This method gets executed once every second.
     * It makes sure all necessary procedures get executed in the right order.
     */
    @Override
    public void run() {
        if (questions.isEmpty()) createQuestions();
        if (tokens.isEmpty()) createTokens();
        getPMs();
        manageQuiz();
        sendPMs();
    }

    /**
     * Gets all new PMs,
     * Creates them a response,
     * And adds them to the sendPMs list to be sent.
     */
    private static void manageQuiz() {
        sendPMs.clear();
        for (Map.Entry<String, String> entry : gotPMs.entrySet()) {
            String response = createResponse(entry.getKey(), entry.getValue());
            sendPMs.put(entry.getKey(), response);
            System.out.println("SEND: " + response + ", TO: " + entry.getKey());
        }
    }

    /**
     * This method sends all PMs from the sendPMs list.
     */
    private static void sendPMs() {
        try {
            for (Map.Entry<String, String> entry : sendPMs.entrySet()) {
                URL url = new URL("https://pr2hub.com/message_send.php"); URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                StringJoiner sj = new StringJoiner("&");
                String tempToken = tokens.get(0);
                tokens.remove(0);
                tokens.add(tempToken);
                Map<String, String> arguments = setArguments(entry.getKey(), entry.getValue(), tempToken);
                for (Map.Entry<String, String> ent : arguments.entrySet()) {
                    sj.add(URLEncoder.encode(ent.getKey(), "UTF-8") + "=" + URLEncoder.encode(ent.getValue(), "UTF-8"));
                }
                byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                http.setFixedLengthStreamingMode(length);
                http.connect();
                try (OutputStream os = http.getOutputStream()) { os.write(out); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets all the arguments for VazaBot responders to send a PM.
     * @param key player to send message to
     * @param value message to send to player
     * @return map of all arguments
     */
    private static Map<String, String> setArguments(String key, String value, String tempToken) {
        Map<String, String> map = new HashMap<>();
        map.put("token" ,tempToken);
        map.put("to_name", key);
        map.put("message", value);
        return map;
    }

    /**
     * This method decodes player's messages.
     * If player sends "/help", player gets sent the instruction manual.
     * If player sends "/quiz", player's quiz starts.
     * If player sends "A", "B", "C" or "D", it checks the answer, if a question s set.
     * Otherwise the message will tell the player to seek help.
     * @param user player's username
     * @param message player's message
     * @return response to player
     */
    private static String createResponse(String user, String message) {
        if (message.toLowerCase().equals("/help")) return "Send \"/quiz\" to start your quiz. Send \"A\", \"B\", \"C\" or \"D\" to "+
                "answer your current question. Send \"/re\" to get the last question in case you didn't receive it. Get 20 questions "+
                "right in a row to win. Get 1 question wrong, and you lose all progress.";
        Contestant player = null;
        for (Contestant contestant : contestants) {
            if (contestant.getUsername().equals(user)) {
                player = contestant;
                break;
            }
        }
        if (message.toLowerCase().equals("/quiz")) {
            return startQuiz(player, user);
        }
        else if (message.toLowerCase().equals("/re")) {
            if (player != null) return player.getLastMessage();
        }
        else if (message.toUpperCase().matches("[ABCD]")) {
            return checkAnswer(player, message.toUpperCase());
        }
        return "Your message didn't match any formats. Try again, or send \"/help\" for more info.";
    }

    /**
     * Checks the answer a player gave.
     * If the player hasn't started a quiz, they will be told to do so.
     * If the answer is incorrect, they get sent their results.
     * If the answer is correct, then
     *   1) They get sent a new question they haven't answered in the current quiz yet
     *   2) If they answered all questions correctly, they win the prize
     * @param player player's username
     * @param answer player's answer
     * @return message to send to player
     */
    private static String checkAnswer(Contestant player, String answer) {
        if (player == null) return "You haven't started a quiz yet!";
        Question question = player.getCurrentQuestion();
        if (question == null) {
            return "You haven't started your quiz yet!";
        }
        if (player.getCorrectAnswer().equals(answer)) {
            if (player.getAnsweredQuestions().size() == 20) {
                player.reset();
                String response = "Congratulations! You won! Here's your prize: " + location;
                player.setLastMessage(response);
                return response;
            }
            Question q;
            while (true) {
                q = questions.get(new Random().nextInt(questions.size()));
                if (player.getAnsweredQuestions().contains(q)) continue;
                player.setCurrentQuestion(q);
                break;
            }
            if (player.getAnsweredQuestions().size() == 20) {
                String response = askQuestion("Correct! Final question: ", q, player);
                player.setLastMessage(response);
                return response;
            }
            String response = askQuestion("Correct! Question " + player.getAnsweredQuestions().size() + ": ", q, player);
            player.setLastMessage(response);
            return response;
        } else {
            int score = player.getAnsweredQuestions().size() - 1;
            player.reset();
            String response = "Wrong answer! Your score: " + score + ". Send \"/quiz\" to try again.";
            player.setLastMessage(response);
            return response;
        }
    }

    /**
     * Starts the quiz for a player and sends the first question.
     * If player already has a quiz, nothing happens.
     * @param player player
     * @param user player's username
     * @return question to send
     */
    private static String startQuiz(Contestant player, String user) {
        if (player == null) {
            player = new Contestant(user);
            contestants.add(player);
        }
        if (player.isPlaying()) {
            return "You've already started your quiz!";
        }
        Question q = questions.get(new Random().nextInt(questions.size()));
        player.setCurrentQuestion(q);
        player.setPlaying();
        String response = askQuestion("Question 1: ", q, player);
        player.setLastMessage(response);
        return response;
    }

    /**
     * Generates the proper question format to ask a question.
     * All answers are set in random order.
     * @param add additional info
     * @param q question
     * @param contestant contestant
     * @return formatted question
     */
    private static String askQuestion(String add, Question q, Contestant contestant) {
        List<SimpleEntry<String, Integer>> answers = new ArrayList<>();
        Random r = new Random();
        answers.add(new AbstractMap.SimpleEntry<>(q.getA(), r.nextInt(100)));
        answers.add(new AbstractMap.SimpleEntry<>(q.getB(), r.nextInt(100)));
        answers.add(new AbstractMap.SimpleEntry<>(q.getC(), r.nextInt(100)));
        answers.add(new AbstractMap.SimpleEntry<>(q.getD(), r.nextInt(100)));
        answers.sort(Comparator.comparingInt(SimpleEntry::getValue));
        String A = answers.get(0).getKey();
        String B = answers.get(1).getKey();
        String C = answers.get(2).getKey();
        String D = answers.get(3).getKey();
        if (q.getCorrect().equals(A)) contestant.setCorrectAnswer("A");
        else if (q.getCorrect().equals(B)) contestant.setCorrectAnswer("B");
        else if (q.getCorrect().equals(C)) contestant.setCorrectAnswer("C");
        else if (q.getCorrect().equals(D)) contestant.setCorrectAnswer("D");
        return add + q.getQ() + "\nA: " + A + "\nB: " + B + "\nC: " + C + "\nD: " + D;
    }

    /**
     * Reads in all PMs from the first page.
     */
    private static void getPMs() {
        try {
            URL url = new URL("https://pr2hub.com/messages_get.php?start=0&count=20&token=" + token);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) decodePMs(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decodes all the PMs.
     * Sorts all the new unread PMs and adds them to gotPMs list.
     * @param line json text
     * @throws JSONException when nothing works
     */
    private static void decodePMs(String line) throws JSONException {
        gotPMs.clear();
        JSONObject json = new JSONObject(line);
        int len = json.getJSONArray("messages").length();
        String id = json.getJSONArray("messages").getJSONObject(0).getString("message_id");
        if (lastMessageID.equals("")) lastMessageID = id;
        for (int i = 0; i < len; i++) {
            JSONObject message = json.getJSONArray("messages").getJSONObject(i);
            if (lastMessageID.equals(message.getString("message_id"))) break;
            gotPMs.put(message.getString("name"), message.getString("message"));
            System.out.println("GET: " + message.getString("message") + ", FROM: " + message.getString("name"));
        }
        lastMessageID = id;
    }

    /**
     * Creates all the questions for the quiz.
     * New questions get set in this order:
     * Question, A, B, C, D, Correct answer
     */
    private static void createQuestions() {
        questions.add(new Question("Who is my creator?", "Juhla", "Vaza", "Bls1999", "Antonhs", "Vaza"));
        questions.add(new Question("Who created this game?", "Vaza", "Red Jackdaw", "Bls199", "Jiggmin", "Jiggmin"));
        questions.add(new Question("What is vasalemma's most popular level?", "Hurry Up!", "Prop Hunt", "PR2 Questions", "PR2 in 3D", "Hurry Up!"));
        questions.add(new Question("How many varieties of blocks does PR2 have?", "31", "29", "25", "27", "27"));
        questions.add(new Question("What's the name of the music that plays when you get the artifact?", "We Are Loud", "Under Fire", "Prismatic", "Quickly Now", "We Are Loud"));
        questions.add(new Question("What's the default chance of Cowboy Time?", "2%", "10%", "1%", "5%", "5%"));
        questions.add(new Question("What's the maximum length you can set to a username?", "25 characters", "24 characters", "20 characters", "30 characters", "20 characters"));
        questions.add(new Question("What's the maximum length that a level name can show?", "infinite", "50 characters", "100 characters", "40 characters", "50 characters"));
        questions.add(new Question("How many unique placeable images does PR2 have?", "10", "12", "11", "13", "10"));
        questions.add(new Question("What is the maximum amount of characters a text box can have in PR2?", "100", "250", "500", "1000", "500"));
        questions.add(new Question("Which one of these has NOT been an artifact location?", "The Level by player z", "Hurry Up! by vasalemma", "Apocalypse by Divinity", "idk lol by wait I can't xD", "abandon ship by golioth hog"));
        questions.add(new Question("What is PR2's server address?", "26.92.56.214", "72.2.192.230", "58.132.45.194", "45.76.24.255", "45.76.24.255"));
        questions.add(new Question("How many non-private servers does PR2 have?", "8", "10", "12", "14", "10"));
        questions.add(new Question("Which one of these is not a server on PR2?", "Isabel", "Andres", "Loki", "Marina", "Marina"));
        questions.add(new Question("What are all of your stats set to during Tournament Mode?", "50/50/50", "60/60/60", "65/65/65", "70/70/70", "65/65/65"));
        questions.add(new Question("How many PMs are you allowed to send per minute?", "4", "5", "8", "10", "4"));
        questions.add(new Question("What is the maximum amount of players a server is able to hold?", "100", "150", "200", "255", "200"));
        questions.add(new Question("Which one of these has never been a mod?", "XPA!", "PooZy", "Red Jackdaw", "b@by*G", "PooZy"));
        questions.add(new Question("What is the top rated level of All Time Best?", "R vs B, Showdown", "Pr3's Randomness", "-Deliverance-", "Art Gallery", "Art Gallery"));
        questions.add(new Question("Which one of these has never been an admin?", "XPA!", "1python64", "Red Jackdaw", "b@by*G", "XPA!"));
        questions.add(new Question("Which one of these was not a server in the original Platform Racing?", "Derron", "Carina", "Grayan", "Fitz", "Carina"));
        questions.add(new Question("Which one of these is not a level in the original Platform Racing?", "Slip", "Buto", "Robocity", "Eternal Hop", "Eternal Hop"));
        questions.add(new Question("What rank does Assembly require in the original Platform Racing?", "5", "10", "20", "50", "20"));
        questions.add(new Question("Which one of these was not an item in the original Platform Racing?", "Sword", "Laser Gun", "Mine", "Teleport", "Sword"));
        questions.add(new Question("Jiggmin once made a contest to add a new item to PR2. There were two choices, where Ice Wave won. What was the name of the item that lost?", "Triple Bow", "Throw Bomb", "Rocket Launcher", "Flame Spread", "Triple Bow"));
        questions.add(new Question("Jiggmin once made a contest to add a new design element to PR2. There were two choices, where text tool won. What was the name of the design element that lost?", "Block Skinning", "Custom Stickers", "Custom Music", "Sound Changer", "Block Skinning"));
        questions.add(new Question("What is Jiggmin's real name?", "Jig Mings", "Jim Fales", "Jacob Grahn", "Jake Thorson", "Jacob Grahn"));
        questions.add(new Question("What's the pixel size of a block in PR2?", "30x30", "32x32", "40x40", "36x36", "30x30"));
        questions.add(new Question("What's the pixel size of a block in PR3?", "30x30", "32x32", "40x40", "36x36", "40x40"));
        questions.add(new Question("Which one of these is not a hat in PR3?", "Nurse Hat", "Bouncy Hat", "Santa Hat", "Propeller Hat", "Propeller Hat"));
        questions.add(new Question("Which one of these hats cannot be earned through any other way than random chance?", "Exp Hat", "Party Hat", "Santa Hat", "Top Hat", "Exp Hat"));
        questions.add(new Question("Who won the first ArtiContest?", "Vipa", "Peregrine", "SoyaS", "Funruna", "Peregrine"));
        questions.add(new Question("Who won the third ArtiContest?", "#-Pro-#", "Vipa", "Zodiac", "Yaw 5019", "#-Pro-#"));
        questions.add(new Question("Which one of these is not a block category in PR3?", "Jungle", "Space", "Underwater", "Arctic", "Arctic"));
        questions.add(new Question("Which one of these is not an item in the current PR3 reboot?", "Napalm", "Snowball", "Magnet", "Shield", "Magnet"));
        questions.add(new Question("If there's a single Move Block in a level in PR2, what is it's first three moves?", "Down Down Up", "Down Left Right", "Up Right Down", "It's completely random", "Down Down Up"));
        questions.add(new Question("Which one of these is not a name of a song in PR3?", "Galactic Dive", "Mercury Drop", "Stylization", "Dark Samhain", "Galactic Dive"));
        questions.add(new Question("Which one of these games is not an inspiration of any of Vaza's levels?", "Super Mario Galaxy", "A Hat In Time", "Touhou", "Garry's Mod", "A Hat In Time"));
        questions.add(new Question("Which one of these fruit/vegetables is characterized as PooZy in PR2?", "Potato", "Pear", "Pumpkin", "Pickle", "Pickle"));
        questions.add(new Question("What type of contest has never been chosen as an ArtiContest?", "Level making contest", "Racing contest", "Deathmatch contest", "Trapping contest", "Trapping contest"));
        questions.add(new Question("Which one of these items is not available in Vaza's level Prop Hunt 2?", "Mine", "Laser Gun", "Sword", "Ice Wave", "Ice Wave"));
        questions.add(new Question("Which one of these games is not an inspiration of any of Vaza's levels?", "VVVVVV", "Castlevania", "Super Meat Boy", "I Wanna Be The Guy", "Castlevania"));
        questions.add(new Question("Which one of these games is not an inspiration of any of Vaza's levels?", "Terraria", "Paper Mario", "Minecraft", "Karoshi", "Minecraft"));
        questions.add(new Question("Which hat was the latest hat that Jiggmin added to PR2?", "Artifact Hat", "Moon Hat", "Thief Hat", "Jump Start Hat", "Artifact Hat"));
        questions.add(new Question("What is the F@H code for Team Jiggmin?", "149015", "143016", "146014", "142013", "143016"));
        questions.add(new Question("Who is the owner of PR2's Discord server?", "TRUC", "Dangevin", "Bls1999", "Jiggmin", "Dangevin"));
        questions.add(new Question("How many levels does Vaza have? (On all alts in total, search wolfie4321 for more data)", "50-100", "100-150", "150-200", "200+", "150-200"));
        questions.add(new Question("Which one of these is not an alt account of Vaza? (Check Vaza's guild)", "Black Thunder", "wolfie4321", "Ice Castle", "TheNovaStar", "TheNovaStar"));
        questions.add(new Question("Which one of these levels is not a part of the original campaign of PR2?", "Backlight", "Volcanic Inferno", "Soul Temple", "razor blade", "Volcanic Inferno"));
        questions.add(new Question("Which one of these sites doesn't belong to Jiggmin?", "jiggmin2.com", "grahn.io", "freegoo.se", "pr2hub.com", "jiggmin2.com"));
        questions.add(new Question("How many rank tokens can you earn in total on PR2?", "2", "3", "5", "7", "5"));
    }

    /**
     * Adds all responsive QuizBot responder tokens to list.
     */
    private void createTokens() {
        tokens.add("5647261-0bd780160e54a6c74bf97ac8594d1deacdea6384");
        tokens.add("5647267-0ed498bec61bd2418660076e1a9cfce02cce16d5");
        tokens.add("5647269-0e7f2dc1ed86f8a28ef83c47659953a6b3b21b1a");
        tokens.add("5647270-78f582476812381f02c7cb2dbb496509768ab6cd");
        tokens.add("5647271-a8aff28373f37f129003d76f1fb3e7a73c4132e5"); // 5
        tokens.add("5647274-34eb8f71994286ba67b7b39fd5bea9eabec7a9cc");
        tokens.add("5647275-919727c81e038b5c49e7152c37af62be560f4867");
        tokens.add("5647276-c253b39bef17a917e7a1ef33819f47e0ac66c29a");
        tokens.add("5647277-54268d65a6c40582a7cf4d95b60532aecc874038");
        tokens.add("5647278-2d5d693a7496eed42666fbfb18973f163e4a65e6"); // 10
        tokens.add("5647279-b43c53cffc87f89ef2abda22a26a86e47f276a31");
        tokens.add("5647280-00776a63fc1508a62c6cccc0f40f4637c2153234");
        tokens.add("5647281-70b98fa70d35c0f91f01456a36e9838c7d302b64");
        tokens.add("5647282-76c2d91120edaee305bee39ff9f74af8eb55ac25");
        tokens.add("5647283-43b555b31c3319794299839358f1693d6785653f"); // 15
        tokens.add("5647284-bd18237d42413535e3d509123e68ead4ccb389c9");
        tokens.add("5647285-f857b501108a2e8904a2178c6a23d490fc82b357");
        tokens.add("5647286-a000a11973f4f42feaf48071d3f9ba89984c06ae");
        tokens.add("5647287-3106bbb8a803b4aba396c4de86cb98abc49a018d");
        tokens.add("5647288-f68d4ca1bc84eed97f62d18e75c77551bfc3b554"); // 20
        tokens.add("5647289-1542d2d5404cff367111a49db3376933b9c95a40");
        tokens.add("5647291-227d1beaf74de7dcf29a1b79a09264e4e0640097");
        tokens.add("5647292-b4613e0503558e5294c173ecddd3565e82ddc204");
        tokens.add("5647293-2b0960dc65ef9d57c20c13f2d0da042e072c1b9d");
        tokens.add("5647294-d03f8f96e9791eb65526d4c7df275abf94f285e1"); // 25
    }
}
