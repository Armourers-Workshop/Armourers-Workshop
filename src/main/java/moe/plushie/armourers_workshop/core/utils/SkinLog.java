package moe.plushie.armourers_workshop.core.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkinLog {

    private static Logger LOGGER = LogManager.getLogger("SkinCore");
	
    public static void info(Object object) {
        LOGGER.info(String.valueOf(object));
    }
    public static void error(Object object) {
        LOGGER.error(String.valueOf(object));
    }
    public static void warn(Object object) {
        LOGGER.warn(String.valueOf(object));
    }


}
