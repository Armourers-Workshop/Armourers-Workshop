package moe.plushie.armourers_workshop.core.skin.exporter;

import moe.plushie.armourers_workshop.api.skin.ISkinExporter;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public final class SkinExportManager {

    // we must reduce the floating point zero padding to save space.
    public static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#.#####");
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.############");

    private static final ArrayList<ISkinExporter> SKIN_EXPORTERS;

    static {
        SKIN_EXPORTERS = new ArrayList<>();
        SKIN_EXPORTERS.add(new SkinExporterWavefrontObj());
        SKIN_EXPORTERS.add(new SkinExporterPolygon());
    }

    public static ISkinExporter getSkinExporter(String fileExtension) {
        if (fileExtension.isEmpty()) {
            return null;
        }
        for (var skinExporter : SKIN_EXPORTERS) {
            for (var ext : skinExporter.getExtensions()) {
                if (ext.equalsIgnoreCase(fileExtension)) {
                    return skinExporter;
                }
            }
        }
        return null;
    }

    public static void exportSkin(Skin skin, String fileExtension, String filename, float scale) throws Exception {
        var skinExporter = getSkinExporter(fileExtension);
        if (skinExporter != null) {
            exportSkin(skin, skinExporter, filename, scale);
        } else {
            ModLog.error("Could not export to {} format.", fileExtension);
        }
    }

    public static void exportSkin(Skin skin, ISkinExporter skinExporter, String filename, float scale) throws Exception {
        var filePath = new File(EnvironmentManager.getRootDirectory(), "model-exports");
        SkinFileUtils.forceMkdir(filePath);
        skinExporter.exportSkin(skin, filePath, filename, scale);
    }

    public static Collection<String> getExporters() {
        var exporters = new ArrayList<String>();
        for (var skinExporter : SKIN_EXPORTERS) {
            exporters.addAll(skinExporter.getExtensions());
        }
        return exporters;
    }
}
