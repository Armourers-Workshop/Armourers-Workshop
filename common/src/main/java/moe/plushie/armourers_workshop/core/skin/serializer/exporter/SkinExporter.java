package moe.plushie.armourers_workshop.core.skin.serializer.exporter;

import moe.plushie.armourers_workshop.core.skin.Skin;

import java.io.File;
import java.util.Collection;

public interface SkinExporter {

    Collection<String> extensions();

    void exportSkin(Skin skin, File filePath, String filename, float scale) throws Exception;
}
