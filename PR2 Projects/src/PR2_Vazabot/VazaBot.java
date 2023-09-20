package PR2_Vazabot;

import java.util.*;

/**
 * VazaBot. Answers incoming messages and commands.
 */
public class VazaBot {

    /**
     * Activation code. Checks PMs every 5 seconds.
     * @param args input
     */
    public static void main(String[] args) {
        new Timer().schedule(new PMReader(), 0, 5000);
    }
}