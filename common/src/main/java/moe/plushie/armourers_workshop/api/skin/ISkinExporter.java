package moe.plushie.armourers_workshop.api.skin;

import java.io.File;
import java.util.Collection;

public interface ISkinExporter {

    Collection<String> getExtensions();

    void exportSkin(ISkin skin, File filePath, String filename, float scale) throws Exception;
}
