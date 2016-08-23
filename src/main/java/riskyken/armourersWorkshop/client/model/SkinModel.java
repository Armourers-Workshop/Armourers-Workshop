package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.MathHelper;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.common.config.ConfigHandler;

@SideOnly(Side.CLIENT)
public class SkinModel {
    
    public boolean hasList[];
    public boolean[] displayListCompiled;
    public int[] displayList;
    public long loadedTime;
    
    public SkinModel(ArrayList<ColouredFace>[] vertexLists) {
        hasList = new boolean[vertexLists.length];
        displayListCompiled = new boolean[vertexLists.length];
        displayList = new int[vertexLists.length];
        for (int i = 0; i < vertexLists.length; i++) {
            hasList[i] = vertexLists[i].size() > 0;
        }
    }
    
    public void setLoaded() {
        loadedTime = System.currentTimeMillis();
    }
    
    public int getLoadingLod() {
        long time = System.currentTimeMillis();
        if (time < loadedTime + 500) {
            long timePassed = time - loadedTime;
            return MathHelper.clamp_int((ConfigHandler.maxLodLevels + 1) - ((int) (timePassed / 125F) + 1), 0, ConfigHandler.maxLodLevels + 1);
        }
        return 0;
    }
    
    public void cleanUpDisplayLists() {
        if (hasList != null) {
            for (int i = 0; i < displayList.length; i++) {
                if (hasList[i]) {
                    if (displayListCompiled[i]) {
                        GLAllocation.deleteDisplayLists(displayList[i]);
                    }
                }
            }
        }
    }
}
