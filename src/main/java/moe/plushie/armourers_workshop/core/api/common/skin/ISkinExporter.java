package moe.plushie.armourers_workshop.core.api.common.skin;

import java.io.File;

import moe.plushie.armourers_workshop.core.skin.Skin;

public interface ISkinExporter {
    
    public String[] getFileExtensions();
    
    public void exportSkin(Skin skin, File filePath, String filename, float scale);
    
}
