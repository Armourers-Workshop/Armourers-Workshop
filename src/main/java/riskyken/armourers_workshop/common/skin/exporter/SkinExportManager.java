package riskyken.armourers_workshop.common.skin.exporter;

import java.io.File;

import org.apache.logging.log4j.Level;

import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.utils.ModLogger;

public final class SkinExportManager {
    
    private SkinExportManager() {}
    
    
    public static ISkinExporter getSkinExporter(String fileExtension) {
        if (fileExtension == null) {
            return null;
        }
        if (fileExtension.equalsIgnoreCase("obj")) {
            //return new SkinExporterWavefrontObj(), file);
        }
        if (fileExtension.equalsIgnoreCase("ply")) {
            return new SkinExporterPolygon();
        }
        return null;
    }
    
    public static void exportSkin(Skin skin, String fileExtension, File file, float scale) {
        ISkinExporter skinExporter = getSkinExporter(fileExtension);
        if (skinExporter != null) {
            exportSkin(skin, skinExporter, file, scale);
        } else {
            ModLogger.log(Level.ERROR, String.format("Could not export to %s format.", skinExporter));
        }
    }
    
    public static void exportSkin(Skin skin, ISkinExporter skinExporter, File file, float scale) {
        skinExporter.exportSkin(skin, file, scale);
    }
}
