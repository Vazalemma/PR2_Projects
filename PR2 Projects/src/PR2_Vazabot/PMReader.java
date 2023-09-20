package PR2_Vazabot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimerTask;

/**
 * PM Reader.
 */
public class PMReader extends TimerTask {


    /**
     * VazaBot player token.
     */
    private static final String token = "5641162-b5af33f3d0a3ceddc252e624f3d232a201ab72ba";

    /**
     * Last PM ID.
     * This is to assure every PM gets read exactly once.
     */
    private static String message_id = "";

    /**
     * List of new PMs that have been received.
     */
    private Map<String, String> gotPMs = new HashMap<>();

    /**
     * List of PMs that will be sent.
     */
    private Map<String, String> sendPMs = new HashMap<>();

    /**
     * This method gets executed every 5 seconds.
     * It makes sure all necessary procedures get executed in the right order.
     */
    public void run() {
        getPMs();
        try {
            makeResponse();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendPMs();
    }

    /**
     * This method sends all PMs from the send PM list.
     */
    private void sendPMs() {
        try {
            for (Map.Entry<String, String> entry : sendPMs.entrySet()) {
                URL url = new URL("https://pr2hub.com/message_send.php");
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST"); http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                StringJoiner sj = new StringJoiner("&");
                Map<String, String> arguments = setArguments(entry.getKey(), entry.getValue());
                for (Map.Entry<String, String> ent : arguments.entrySet()) {
                    sj.add(URLEncoder.encode(ent.getKey(), "UTF-8")+"="+URLEncoder.encode(ent.getValue(), "UTF-8"));
                }
                byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                http.setFixedLengthStreamingMode(length);
                http.connect();
                try (OutputStream os = http.getOutputStream()) {
                    os.write(out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets all the arguments for VazaBot to send a PM.
     * @param key player to send message to
     * @param value message to send to player
     * @return map of all arguments
     */
    private Map<String, String> setArguments(String key, String value) {
        System.out.println("Send:    " + key + " - " + value);
        Map<String, String> map = new HashMap<>();
        map.put("token" ,token);
        map.put("to_name", key);
        map.put("message", value);
        return map;
    }

    /**
     * Create response to the received message.
     */
    private void makeResponse() throws InterruptedException {
        sendPMs.clear();
        for (Map.Entry<String, String> entry : gotPMs.entrySet()) {
            String response = ResponseCreator.createResponse(entry.getKey(), entry.getValue());
            sendPMs.put(entry.getKey(), response);
        }
    }

    /**
     * Reads in all PMs from the first page.
     * Splits all the new PMs to be dealt with later.
     */
    private void getPMs() {
        gotPMs.clear();
        try {
            URL url=new URL("https://pr2hub.com/messages_get.php?token="+token);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) decodePMs(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decodes the PMs and finds all the new PMs.
     * @param line json text
     * @throws Exception when nothing works
     */
    private void decodePMs(String line) throws Exception {
        JSONObject json = new JSONObject(line);
        int len = json.getJSONArray("messages").length();
        String id = json.getJSONArray("messages").getJSONObject(0).getString("message_id");
        if (message_id.equals("")) message_id = id;
        for (int i = 0; i < len; i++) {
            JSONObject message = json.getJSONArray("messages").getJSONObject(i);
            if (message_id.equals(message.getString("message_id"))) break;
            gotPMs.put(message.getString("name"), message.getString("message"));
            System.out.println("Receive: " + message.getString("name") + " - " + message.getString("message"));
        }
        message_id = id;
    }
}
