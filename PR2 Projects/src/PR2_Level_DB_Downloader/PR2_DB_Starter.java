package PR2_Level_DB_Downloader;

import java.util.ArrayList;
import java.util.List;

public class PR2_DB_Starter {
    private static final int FROM_INDEX = 6493586; // 6496502
    private static final int THREAD_COUNT = 1;
    private static final int SAVE_EVERY_X_SEC = 10;

    public static void main(String[] args) throws InterruptedException {
        PR2_Level_Database database = new PR2_Level_Database(FROM_INDEX);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads.add(new Thread(new PR2_Level_List_Updater(database)));
            threads.get(i).start();
        }

        while (database.checkIndex() < 6500000) {
            Thread.sleep(SAVE_EVERY_X_SEC * 1000);
            database.save();
        }

        for (int i = 0; i < THREAD_COUNT; i++) threads.get(i).interrupt();
    }
}
