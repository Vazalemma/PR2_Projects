package PR2_Level_DB_Downloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

class PR2_Level_Database {
    private static final List<String> levels = new ArrayList<>();
    private static Integer index;
    private static final String user = "";

    PR2_Level_Database(int index) {
        PR2_Level_Database.index = index;
    }

    int getNextIndex() {
        synchronized (index) {
            return index++;
        }
    }

    int checkIndex() {
        synchronized (index) {
            return index;
        }
    }

    void addLevel(String level) {
        synchronized (levels) {
            levels.add(level);
            levels.notifyAll();
        }
    }

    void save() {
        synchronized (levels) {
            try {
                System.out.println("\nSaving...\n");
                BufferedWriter writer = new BufferedWriter(new FileWriter("textfiles/fulllevellist.txt", true));
                for (String level : levels) {
                    writer.write(level);
                    writer.newLine();
                }
                writer.close();
                levels.clear();
                levels.notifyAll();
            } catch (Exception e) {
                System.out.println("\nFailed to save level...\n");
            }
        }
    }

    void saveuser(int userID, String username) {
        synchronized (user) {
            try {
                String user = userID + " " + username;
                BufferedWriter writer = new BufferedWriter(new FileWriter("textfiles/userdatabase.txt", true));
                writer.write(user);
                writer.newLine();
                writer.close();
            } catch (Exception e) {
                System.out.println("\nFailed to save user...\n");
            }
        }
    }

    String getUserName(int userID) {
        synchronized (user) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("textfiles/userdatabase.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    String ID = line.split(" ", 2)[0];
                    String name = line.split(" ", 2)[1];
                    if (Integer.parseInt(ID) == userID) return name;
                }
                return "";
            } catch (Exception e) {
                return null;
            }
        }
    }
}
