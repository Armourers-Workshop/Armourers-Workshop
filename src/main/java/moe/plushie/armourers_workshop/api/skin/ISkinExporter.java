package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.core.skin.Skin;

import java.io.File;

public interface ISkinExporter {
    
    public String[] getFileExtensions();
    
    public void exportSkin(Skin skin, File filePath, String filename, float scale);
    
}
