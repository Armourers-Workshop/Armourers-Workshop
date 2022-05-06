package moe.plushie.armourers_workshop.core.skin.exporter;

import moe.plushie.armourers_workshop.api.skin.ISkinExporter;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.common.ModLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class SkinExportManager {

    private static final ArrayList<ISkinExporter> SKIN_EXPORTERS;

    private SkinExportManager() {}

    static {
        SKIN_EXPORTERS = new ArrayList<>();
        SKIN_EXPORTERS.add(new SkinExporterWavefrontObj());
//        SKIN_EXPORTERS.add(new SkinExporterPolygon());
    }


    public static ISkinExporter getSkinExporter(String fileExtension) {
        if (StringUtils.isEmpty(fileExtension)) {
            return null;
        }
        for (ISkinExporter skinExporter : SKIN_EXPORTERS) {
            for (String ext : skinExporter.getFileExtensions()) {
                if (ext.equalsIgnoreCase(fileExtension)) {
                    return skinExporter;
                }
            }
        }
        return null;
    }

    public static void exportSkin(Skin skin, String fileExtension, File filePath, String filename, float scale) {
        ISkinExporter skinExporter = getSkinExporter(fileExtension);
        if (skinExporter != null) {
            exportSkin(skin, skinExporter, filePath, filename, scale);
        } else {
            ModLog.error("Could not export to {} format.", fileExtension);
        }
    }

    public static void exportSkin(Skin skin, ISkinExporter skinExporter, File filePath, String filename, float scale) {
        try {
            FileUtils.forceMkdir(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        skinExporter.exportSkin(skin, filePath, filename, scale);
    }

    public static Collection<String> getExporters() {
        ArrayList<String> exporters = new ArrayList<>();
        for (ISkinExporter skinExporter : SKIN_EXPORTERS) {
            exporters.addAll(Arrays.asList(skinExporter.getFileExtensions()));
        }
        return exporters;
    }
}
