package app.utils;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtils {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static {
        try {
            // Set the root logger level
            logger.setLevel(Level.ALL);
    
            // File handler for logging to a file only
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
    
            // Remove the default console handler
            Logger rootLogger = Logger.getLogger("");
            if (rootLogger.getHandlers().length > 0) {
                rootLogger.removeHandler(rootLogger.getHandlers()[0]);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to set up logger handlers", e);
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }
}
