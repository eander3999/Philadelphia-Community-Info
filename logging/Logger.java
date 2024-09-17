package philadelphia_info_calculator.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static Logger instance;
    private PrintWriter writer;
    private SimpleDateFormat dateFormat;

    private Logger() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void setLogFile(String filename) throws IOException {
        if (writer != null) {
            writer.close();
        }
        writer = new PrintWriter(new FileWriter(filename, true), true);
    }

    public void log(String message) {
        String timeStamp = dateFormat.format(new Date());
        String logMessage = timeStamp + " " + message;

        if (writer != null) {
            writer.println(logMessage);
        } else {
            System.err.println(logMessage);
        }
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}