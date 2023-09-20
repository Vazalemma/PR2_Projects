package PR2_Vazabot;

import PR2_ArtiFinder_Old.PR2_Hint_Decoder;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * VazaBot response creator.
 */
class ResponseCreator {


    /**
     * List of hints of the current artifact location.
     * Will be replaced with artiDunno if the answer is unknown.
     */
    private static final List<String> hints = Arrays.asList(
            "It's kind of like a tutorial level, like Newbieland, but better. ",
            "The level itself is very space-related. It's in spaaaace! ",
            "We're recruiting new people to PR2 and making them play this level first. ");

    /**
     * Artifact location. Update this as necessary.
     * Update to:
     *   1) "", if artifact is unknown.
     *   3) The artifact location itself, if it's known.
     */
    private static final String artiState = "Space Recruit by hyperacticpro ";

    /**
     * List of responses if someone asks for the artifact location
     * while the location is unknown.
     */
    private static final List<String> artiDunno = Arrays.asList(
            "Do you wanna know where the artifact is? Yeah, me too... ",
            "I don't know where the current artifact is. ",
            "I'm currently still searching for the artifact. ",
            "Nope, no better hint is coming from here! ",
            "Artifact? Never seen one of those before. ",
            "It's in a level somewhere! ...That's all I know though. ");

    /**
     * List of responses if someone asks where the artifact is
     * and the location is known.
     */
    private static final List<String> artiFound = Arrays.asList(
            "Artifact location? Yeah, I know where is it. ",
            "I know where it is. I won't tell you though! ",
            "It's a secret to everybody... Except Vaza! ",
            "You want the artifact? Tell Vaza about it. ",
            "Check the chat room or Discord, there might be an ArtiContest going on! ");

    /**
     * List of greetings.
     */
    private static final List<String> greetings = Arrays.asList(
            "Hello", "Hi", "Howdy", "Sup", "Hey");

    /**
     * List of responses for answering to questions about how I'm doing.
     */
    private static final List<String> whatsups = Arrays.asList("I'm good. ", "I'm doing fine, wbu? ",
            "Everything's good. ", "I'm okay. ", "All's fine. ", "I'm doing fine. ", "All good in the hood! ",
            "I'm doing fantastic! ", "I'm fine, I guess. ", "I'm fine, you? ", "I'm okay, wbu? ");

    /**
     * List of responses for cases when a message cannot be decoded.
     */
    private static final List<String> garble = Arrays.asList(
            "I'm sorry, I didn't seem to understand anything you just said. ",
            "I'm sorry, my AI isn't smart enough to understand your message. ",
            "I'm sorry, it looks like your message was too difficult to decode. ",
            "I'm sorry, your sentence was too complex to understand. ",
            "I'm sorry, could you please explain that a little bit easier? ",
            "I'm sorry, my mechanical hamsters were unable to decode your message. ");

    /**
     * List of all available commands.
     */
    private static final List<String> commands = Arrays.asList(
            "/help", "/hint", "/arti", "/random", "/find", "/joke");

    /**
     * List of random responses.
     */
    private static final List<String> random = Arrays.asList(
            "According to all known laws of aviation, there is no way that a bee should be able to fly. Its wings are"+
                    " too small to get its fat little body off the ground. The bee, of course, flies anyways. Because"+
                    "bees don't care what humans think is impossible. ",
            "You spin me right round baby right round like a record baby-- ",
            "Suggest more random stuff to say! Plz. ");

    /**
     * List of responses for people that use bad language.
     */
    private static final List<String> badlanguage = Arrays.asList(
            "Now that's not a very nice thing to say. Please apologise. ",
            "Don't go saying things like that, that's rude. ",
            "Watch your language there buddy, be a bit more polite. ",
            "That wasn't very polite of you to say. Please say sorry. ",
            "It would be preferred if you used more polite phrases, thank you. ",
            "Don't say such horrible things. I may be a bot, but I also have feelings D:  ",
            "Do you eat food or kiss people with that filthy mouth of yours? ");

    /**
     * List of responses for people who thank me for something.
     */
    private static final List<String> thanks = Arrays.asList("No problem! ", "No worries! ", "My pleasure! ",
            "You're welcome! ", "Glad to be of service! ", "I'm glad I could help! ", "Any time :)  ");

    /**
     * List of responses for people that ask whether I'm real or not.
     */
    private static final List<String> imreal = Arrays.asList(
            "I am a bot. Not a robot, just a simple program bot. ",
            "I'm real, I exist, but only in the form of a program. ",
            "I'm not a human, but I'm also not a walking robot. I'm merely a bot. A program. ");

    /**
     * List of jokes to tell.
     */
    private static final List<String> jokes = Arrays.asList(
            "Q: Can a Kangaroo jump higher than a house? A: Of course! A house doesn't jump at all! ",
            "My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away. ",
            "Q: What is the difference between a snowman and a snowwoman? A: Snowballs. ",
            "In a boomerang shop: \"Hi, I'd like to buy a new boomerang, but first, how do I throw the old one away?\" ",
            "Patient: \"Oh Doctor, I'm so nervous. This is my first operation.\" Doctor: \"Don't worry, mine too.\" ",
            "A naked woman robbed the bank. Nobody could remember her face. ",
            "I can't believe I forgot to go to the gym today. That's, like, 7 years in a row now. ",
            "I was making Russian tea. Unfortunately I couldn't fish the tea bag out of the vodka bottle. ",
            "I thought I'd tell you a good time travel joke -- but you didn't like it. ",
            "I'm selling my parrot. Why? Because yesterday that bastard tried to sell me. ",
            "Q: What goes up and down but never moves? A: The stairs! ",
            "A wife is like a hand grenade. Take off the ring and you can say goodbye to your house. ",
            "Q: Why don't cannibals eat divorced women? A: Because they're bitter! ",
            "Q: Why did the physics teacher break up with the biology teacher? A: There was no chemistry. ",
            "Don't be sad when a bird craps on your head. Be happy that cows can't fly! ");

    /**
     * List of ways to say good bye.
     */
    private static final List<String> seeya = Arrays.asList("Take care! ", "Cyaz!~~ ", "Goodbye. ", "See ya! ");

    /**
     * Response creator. A complex method. Creates a response for the received message.
     * Commands:
     *   1) /help -> returns a tutorial
     *   2) /arti -> returns artifact location
     *   3) /hint -> returns an artifact hitnt
     *   4) /joke -> returns a joke
     *   5) /random -> returns something random
     *   6) /find [hint] -> tries to find the level with the given hint
     * Text recognition:
     *   0) Help         -> Sends a tutorial
     *   1) Greetings    (Hi, Hey, Hiya, Hello, Howdy)
     *   2) What's up    (Sup, What's up, Hows it going, etc)
     *   3) Bad language (Fuck, Nigger, Bitch, kys, etc)
     *   4) Sorry        (Sorry, I Apologise, sorries, etc)
     *   5) Thanks       (Thx, Thanks, Thank you)
     *   6) Am I real?   (Are you bot, r u real, are you human, etc)
     *   7) My name      (ur name, your name)
     *   8) What am I    (Are you boy, r u female, etc)
     *   9) Make jokes   (Tell me a joke, say jokes, etc)
     *   10) I like you  (I luv u, I like you, etc)
     *   11) Goodbyes    (Cya, Bye, Good night, bai, etc)
     * The method can recognize many command by splitting them with a space
     * And many messages by splitting them by punctuation (. , ; ! ?),
     * But the commands must come first, as they will no longer be recognized as commands later on.
     * Example: "/arti /joke /random Hi! What's up, I love you, fuck off, bye."
     * @param name message sender's name
     * @param message message sender's message
     * @return generated response.
     */
    static String createResponse(String name, String message) throws InterruptedException {
        StringBuilder response = new StringBuilder();
        if (message.contains("/help")) return tutorial();
        if (message.toLowerCase().startsWith("/find")) return findArti(message);
        while (true) {
            boolean stop = true;
            if (message.toLowerCase().startsWith("/arti")) {
                stop = false;
                if (artiState.equals("")) response.append(artiDunno.get(new Random().nextInt(artiDunno.size())));
                else if (artiState.length() > 1 && artifactIsNotFound()) {
                    response.append(artiFound.get(new Random().nextInt(artiFound.size())));
                }
                else response.append(artiState);
            }
            else if (message.toLowerCase().startsWith("/hint")) {
                stop = false;
                if (artiState.equals("")) response.append(artiDunno.get(new Random().nextInt(artiDunno.size())));
                else response.append(hints.get(new Random().nextInt(hints.size())));
            }
            else if (message.toLowerCase().startsWith("/random")) {
                stop = false;
                response.append(random.get(new Random().nextInt(random.size())));
            }
            else if (message.toLowerCase().startsWith("/joke")) {
                stop = false;
                response.append(jokes.get(new Random().nextInt(jokes.size())));
            }
            else if (meaninglessCommand(message)) {
                response.append(message.split(" ", 2)[0]).append(" is not a command. Type /help for more info. ");
            }
            if (message.contains(" ")) {
                message = message.split(" ", 2)[1];
            } else {
                message = "";
            }
            if (stop) break;
        }
        String[] sentences = message.split("[.!?;,]+ ");
        for (String sentence : sentences) {
            String s = sentence.toLowerCase();
            if (contains(s, new String[] {"hiya", "howdy", "hello", "hey"}) || s.matches("( )?hi( (.)*)?")) {
                response.append(greetings.get(new Random().nextInt(greetings.size()))).append(" ").append(name).append("! ");
            }
            if (contains(s, new String[] {"what's up", "sup", "how's it goin", "hows it goin", "whats up", "how are you"})) {
                response.append(whatsups.get(new Random().nextInt(whatsups.size())));
            }
            else if (contains(s, new String[] {"fuck you", "fuck off", "kill your", "kys", "fucker",
                    "go die", "nigger", "nigga", "bitch"}))
                response.append(badlanguage.get(new Random().nextInt(badlanguage.size())));
            else if (contains(s, new String[] {"sorry", "i apologise", "sorries", "my apologies"}))
                response.append("Apology accepted. ");
            else if (s.matches("(.)*(thx|thanks|thank you)(.)*"))
                response.append(thanks.get(new Random().nextInt(thanks.size())));
            else if (s.startsWith("help")) response.append("Send me a PM named \"/help\" to learn the commands" +
                    " or just use regular everyday expressions to talk to me! ");
            else if (contains(s, new String[] {"are you real", "human or bot", "human or robot", "robot or human",
                    "are you a bot", "are you a robot", "bot or human", "are you a human", "are you human",
                    "are you bot", "are you robot"}))
                response.append(imreal.get(new Random().nextInt(imreal.size())));
            else if (s.contains("ur name")) response.append("My name is Vaza. I am a bot. You can call me VazaBot. ");
            else if (s.matches("(.)*are (u|you)( a)? (boy|girl|man|woman|guy|gal|male|female)( or )?(.)*"))
                response.append("Vaza is male, but I'm a bot, so I'm an \"it\". ");
            else if (s.matches("(.)*(tell|say|make)( me)?( a)? joke(s)?(.)*"))
                response.append(jokes.get(new Random().nextInt(jokes.size())));
            else if (s.matches("(.)*(i )?(love|luv|like|lik) (u|you)(.)*"))
                response.append("Aww, thanks! :)  ");
            else if (contains(s, new String[] {"bye", "cya", "bai", "goodnight", "good night", "see ya", "see you", "g'night"}))
                response.append(seeya.get(new Random().nextInt(seeya.size())));
            //else if (contains(s, new String[] {"", ""})) response += "";
        }
        if (response.toString().equals("")) response = new StringBuilder(garble.get(new Random().nextInt(garble.size())));
        return response.substring(0, response.length() - 1);
    }

    /**
     * Artifact (or any level in that sense) finder.
     * This method has many restrictions:
     *   1) If the artifact has not been found, searching for levels is disabled.
     *   2) If the command does not match the requirements, the method will not be executed.
     *   3) If you search for "/find help", you will be given a tutorial for finding levels.
     *   4) The searched hint must be between 12 and 74 characters long.
     *         -> Username must be between 4 and 20 characters long.
     *         -> Level name must be between 4 and 50 characters long.
     *         -> +4 characters for " by ".
     *   5) The hint mus contain at least 4 non-underscore ("_") letters.
     * Results:
     *   (1) No results were found,
     *   (2) One or more results were found (all results are separated by a semicolon ("; ")).
     * @param message message
     * @return respective response
     */
    private static String findArti(String message) throws InterruptedException {
        if (artifactIsNotFound()) return artiNotFoundException();
        if (!message.matches("/find (.)+")) return "You must add a hint! PM me \"/find help\" for more info.";
        if (message.startsWith("/find help")) return findArtiTutorial();
        if (!message.matches("/find (.){12,74}")) return "The hint must be between 12 and 74 characters long! Try again.";
        String hint = message.split(" ", 2)[1];
        int letters = 0;
        new PR2_Hint_Decoder();
        for (char c : hint.toCharArray()) if (c != '_') letters++;
        if (letters < 4) return "The hint should contain at least 4 non-underscore (\"_\") characters.";
        StringBuilder response = new StringBuilder(PR2_Hint_Decoder.decode(hint));
        if (response.toString().equals("")) return "I've found no matching results.";
        return response.substring(0, response.length() - 1);
    }

    /**
     * Returns a tutorial on how to use VazaBot's artifact finder.
     * @return tutorial
     */
    private static String findArtiTutorial() {
        return "This is a level finder. You can find levels and their " +
                "corresponding creators! To search, follow the pattern: /find <level> by <user>. Here is an " +
                "example: /find _P_B____o__P________s______. Try sending that PM to me! Also note that the hint " +
                "must be between 6 and 74 characters long and contain at least 4 non-underscore (\"_\") characters.";
    }

    /**
     * Checks if the artifact has been found by someone.
     * @return "True" if found, "False" is not
     */
    private static boolean artifactIsNotFound() {
        try {
            URL url = new URL("https://pr2hub.com/files/artifact_hint.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (new JSONObject(line).getString("finder_name").equals("")) return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * If the artifact isn't found yet, you're not allowed to search for levels.
     * @return error response
     */
    private static String artiNotFoundException() {
        return "Error: There is currently an artifact that hasn't been found. My level finding "+
                "functions have been temporarily deactivated. Try /arti to see if I have found it "+
                "myself and /hint to get hints on where the artifact is if I have actually found it.";
    }

    /**
     * Creates a tutorial for those that need help using VazaBot.
     * @return tutorial
     */
    private static String tutorial() {
        return "Here are the available commands: /help, /arti, /hint, /random, /joke, /find <hint>. "
                + "You can also talk to me normally and I can say things back! Try it out!";
    }

    /**
     * Checks if the message tries to be a command that doesn't actually exist.
     * @param message message
     * @return "True" if the command doesn't exist, otherwise "False"
     */
    private static boolean meaninglessCommand(String message) {
        return allStartWith(message, commands.toArray(new String[commands.size()]));
    }

    /**
     * Checks if the message starts with a forward slash and isn't a command.
     * @param input message
     * @param search list of commands
     * @return "True" if the message pretends to be a command that doesn't exist, otherwise "False"
     */
    private static boolean allStartWith(String input, String[] search) {
        for (String s : search) {
            if (input.startsWith(s)) return false;
        }
        return input.startsWith("/");
    }

    /**
     * Checks if a string array contains a string.
     * @param input string
     * @param search atring array
     * @return "True" if array contains the string, otherwise "False"
     */
    private static boolean contains(String input, String[] search) {
        for (String aSearch : search) {
            if (input.contains(aSearch)) return true;
        } return false;
    }

    /**
     * Main method, used to test the interface.
     * @param args input
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println(createResponse("vasalemma", "Hi VazaBot! sup? I wanna say thank you, I luv u so much! I also wanna ask, are you a boy or a girl, and what's ur name? Can you tell jokes? I g2g now, cya"));
    }
}
