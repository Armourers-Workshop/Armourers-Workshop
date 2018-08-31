package riskyken.armourers_workshop.utils;

import org.apache.logging.log4j.Level;

import riskyken.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.fml.common.FMLLog;

public class ModLogger {
    public static void log(Object object) {
        FMLLog.log(LibModInfo.NAME, Level.INFO, String.valueOf(object));
    }

    public static void log(Level logLevel, Object object) {
        FMLLog.log(LibModInfo.NAME, logLevel, String.valueOf(object));
    }
}
