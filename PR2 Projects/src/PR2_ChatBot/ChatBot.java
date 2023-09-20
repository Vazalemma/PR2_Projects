package PR2_ChatBot;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * ChatBot (UNFINISHED)
 */
public class ChatBot {

    /**
     * Server IP.
     */
    private static final String SERVER = "45.76.24.255";

    /**
     * Server port (currently Derron).
     */
    private static final int PORT = 9160;

    /**
     * Main method
     * @param args input
     * @throws Exception when socket refuses connection
     */
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(SERVER, PORT);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        PrintStream out = new PrintStream(socket.getOutputStream());

        String s = "request_login_id`";

        out.flush();
        out.write(s.getBytes());
        out.flush();

        in.close();
        out.close();
        socket.close();
    }
}
