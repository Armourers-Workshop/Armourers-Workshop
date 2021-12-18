package moe.plushie.armourers_workshop.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkinLog {

    private static final Logger LOGGER = LogManager.getLogger(SkinCore.class);

    public static void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    public static void error(String message, Object... params) {
        LOGGER.error(message, params);
    }

    public static void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }
}
