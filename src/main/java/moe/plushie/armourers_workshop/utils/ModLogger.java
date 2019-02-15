package moe.plushie.armourers_workshop.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.fml.common.FMLLog;

public class ModLogger {
	
    public static void log(Object object) {
    	ArmourersWorkshop.getLogger().log(Level.INFO, String.valueOf(object));
    }

    public static void log(Level logLevel, Object object) {
    	ArmourersWorkshop.getLogger().log(logLevel, String.valueOf(object));
    }
}
