package PR2_QuizBot;

import java.util.Timer;

/**
 * QuizBot - A bot that hosts quizzes.
 */
public class QuizBot {

    /**
     * Main method, used to activate the bot.
     * Reads PMs once a second.
     * @param args input
     */
    public static void main(String[] args) {
        new Timer().schedule(new QuizBotPMReader(), 0, 2000);
    }
}
