package riskyken.armourersWorkshop.common.skin.exporter;

import java.io.File;

import riskyken.armourersWorkshop.common.skin.data.Skin;

public interface ISkinExporter {
    
    public void exportSkin(Skin skin, File file, float scale);
}
