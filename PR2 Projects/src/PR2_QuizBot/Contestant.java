package PR2_QuizBot;

import java.util.ArrayList;
import java.util.List;

/**
 * Contestant.
 */
class Contestant {

    /**
     * Contestant username.
     */
    private String username = "";

    /**
     * Contestant status.
     * "True" if playing, "False" if not.
     */
    private boolean playing = false;

    /**
     * Current question the contestant is on.
     * If the contestant status is "False", this value is "null".
     */
    private Question currentQuestion;

    /**
     * List of questions the contestant has answered.
     * This is used to make sure the contestant doesn't answer
     * The same question twice in the same quiz.
     */
    private List<Question> answeredQuestions = new ArrayList<>();

    /**
     * The correct answer for the current question.
     * Can be either "A", "B", "C" or "D".
     */
    private String correctAnswer = "";

    /**
     * Last message a player received.
     * Only stores data related to questions.
     */
    private String lastMessage = "";

    /**
     * Contestant constructor.
     * @param username contestant username
     */
    Contestant(String username) {
        this.username = username;
        playing = false;
        lastMessage = "You haven't started a quiz yet!";
    }

    /**
     * Get the username of the contestant.
     * @return contestant's username
     */
    String getUsername() {
        return username;
    }

    /**
     * Get the current question the contestant is on.
     * @return contestant's current question
     */
    Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Set the current question for the contestant.
     * @param currentQuestion question
     */
    void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
        answeredQuestions.add(currentQuestion);
    }

    /**
     * Get the list of all questions the contestant has already answered.
     * @return list of answered questions
     */
    List<Question> getAnsweredQuestions() {
        return answeredQuestions;
    }

    /**
     * Check whether the contestant is playing right now.
     * @return "True" if playing, "False" if not
     */
    boolean isPlaying() {
        return playing;
    }

    /**
     * Set contestant playing status to "True".
     */
    void setPlaying() {
        this.playing = true;
    }

    /**
     * Get the correct answer for the current question.
     * @return "A", "B", "C" or "D"
     */
    String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Get the last message a player received.
     * @return last message
     */
    String getLastMessage() {
        return lastMessage;
    }

    /**
     * Set the last message that a player received.
     * @param lastMessage last message
     */
    void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * Set correct answer for the current question.
     * @param correctAnswer "A", "B", "C" or "D"
     */
    void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    /**
     * Reset contestant's quiz status and progress.
     */
    void reset() {
        playing = false;
        currentQuestion = null;
        answeredQuestions.clear();
        correctAnswer = null;
    }
}
