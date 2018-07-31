package riskyken.armourersWorkshop.common.skin.exporter;

import java.io.File;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class SkinExportManager {
    
    private SkinExportManager() {}
    
    public static void exportSkin(Skin skin, String skinExporter, File file) {
        if (skinExporter.equalsIgnoreCase("obj")) {
            //exportSkin(skin, new SkinExporterWavefrontObj(), file);
            return;
        }
        if (skinExporter.equalsIgnoreCase("ply")) {
            exportSkin(skin, new SkinExporterPolygon(), file);
            return;
        }
        ModLogger.log(Level.ERROR, String.format("Could not export to %s format.", skinExporter));
    }
    
    public static void exportSkin(Skin skin, ISkinExporter skinExporter, File file) {
        skinExporter.exportSkin(skin, file);
    }
}
