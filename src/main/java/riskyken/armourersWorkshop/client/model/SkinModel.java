package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.MathHelper;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.client.render.DisplayList;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;

@SideOnly(Side.CLIENT)
public class SkinModel {
    
    public DisplayList[] displayList;
    public boolean[] haveList;
    public long loadedTime;
    
    public SkinModel(ArrayList<ColouredFace>[] vertexLists) {
        displayList = new DisplayList[vertexLists.length];
        haveList = new boolean[vertexLists.length];
        for (int i = 0; i < displayList.length; i++) {
            if (vertexLists[i].size() > 0) {
                displayList[i] = new DisplayList();
                haveList[i] = true;
            } else {
                haveList[i] = false;
            }
        }
    }
    
    public void setLoaded() {
        loadedTime = System.currentTimeMillis();
    }
    
    public int getLoadingLod() {
        long time = System.currentTimeMillis();
        if (time < loadedTime + 500) {
            long timePassed = time - loadedTime;
            return MathHelper.clamp_int((ConfigHandlerClient.maxLodLevels + 1) - ((int) (timePassed / 125F) + 1), 0, ConfigHandlerClient.maxLodLevels + 1);
        }
        return 0;
    }
    
    public void cleanUpDisplayLists() {
        for (int i = 0; i < displayList.length; i++) {
            if (haveList[i]) {
                displayList[i].cleanup();
            }
        }
    }
}
