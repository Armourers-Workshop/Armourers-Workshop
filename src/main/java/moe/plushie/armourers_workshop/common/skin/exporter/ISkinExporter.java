package moe.plushie.armourers_workshop.common.skin.exporter;

import java.io.File;

import moe.plushie.armourers_workshop.common.skin.data.Skin;

public interface ISkinExporter {
    
    public void exportSkin(Skin skin, File file, float scale);
}
