package io.github.thugborean.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingManager {
    private static FileHandler fileHandler;
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "log.log";

    static {
        try {
            // If the directory doesn't exist then make it
            File logDir = new File(LOG_DIR);
            if(!logDir.exists()) logDir.mkdirs();
            fileHandler = new FileHandler(LOG_DIR + "/" + LOG_FILE, false);
            fileHandler.setFormatter(new CustomFormatter());
            fileHandler.setLevel(Level.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
    public static Logger getLogger(Class<?> cls) {
        Logger logger = Logger.getLogger(cls.getName());
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        if(logger.getHandlers().length == 0) {
            logger.addHandler(fileHandler);
        }
        return logger;
    }
}