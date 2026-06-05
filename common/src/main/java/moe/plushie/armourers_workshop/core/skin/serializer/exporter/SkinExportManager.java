package moe.plushie.armourers_workshop.core.skin.serializer.exporter;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SkinExportManager {

    private static final List<SkinExporter> INSTANCES = Collections.immutableList(builder -> {
        builder.add(new SkinExporterWavefrontObj());
        builder.add(new SkinExporterPolygon());
        builder.add(new SkinExporterBlockBench());
    });

    public static SkinExporter getSkinExporter(String fileExtension) {
        if (fileExtension.isEmpty()) {
            return null;
        }
        for (var exporter : INSTANCES) {
            for (var extension : exporter.extensions()) {
                if (extension.equalsIgnoreCase(fileExtension)) {
                    return exporter;
                }
            }
        }
        return null;
    }

    public static void exportSkin(Skin skin, String fileExtension, String filename, float scale) throws Exception {
        var exporter = getSkinExporter(fileExtension);
        if (exporter != null) {
            exportSkin(skin, exporter, filename, scale);
        } else {
            ModLog.error("Could not export to {} format.", fileExtension);
        }
    }

    public static void exportSkin(Skin skin, SkinExporter exporter, String filename, float scale) throws Exception {
        var filePath = new File(EnvironmentManager.getRootDirectory(), "model-exports");
        FileUtils.forceMkdir(filePath);
        exporter.exportSkin(skin, filePath, filename, scale);
    }

    public static Collection<String> allExporters() {
        var exporters = new ArrayList<String>();
        for (var exporter : INSTANCES) {
            exporters.addAll(exporter.extensions());
        }
        return exporters;
    }
}
