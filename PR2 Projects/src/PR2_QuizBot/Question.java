package PR2_QuizBot;

/**
 * Question.
 */
class Question {

    /**
     * Question.
     */
    private String Q;

    /**
     * Answer A.
     */
    private String A;

    /**
     * Answer B.
     */
    private String B;

    /**
     * Answer C.
     */
    private String C;

    /**
     * Answer D.
     */
    private String D;

    /**
     * Correct answer.
     */
    private String correct;

    /**
     * Constructor for a question.
     * @param q question
     * @param a answer A
     * @param b answer B
     * @param c answer C
     * @param d answer D
     * @param correct correct answer
     */
    Question(String q, String a, String b, String c, String d, String correct) {
        Q = q;
        A = a;
        B = b;
        C = c;
        D = d;
        this.correct = correct;
        if (!correct.equals(a) && !correct.equals(b) && !correct.equals(c) && !correct.equals(d)) {
            System.out.println(" - - - No right answer: " + q);
        }
    }

    /**
     * Get question.
     * @return question
     */
    String getQ() {
        return Q;
    }

    /**
     * Get answer A.
     * @return answer A
     */
    String getA() {
        return A;
    }

    /**
     * Get answer B.
     * @return answer B
     */
    String getB() {
        return B;
    }

    /**
     * Get answer C.
     * @return answer C
     */
    String getC() {
        return C;
    }

    /**
     * Get answer D.
     * @return answer D
     */
    String getD() {
        return D;
    }

    /**
     * Get correct answer.
     * @return correct answer
     */
    String getCorrect() {
        return correct;
    }
}
