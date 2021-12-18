package moe.plushie.armourers_workshop.core.model;

import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.render.other.DisplayList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
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
            return MathHelper.clamp((SkinConfig.maxLodLevels + 1) - ((int) (timePassed / 125F) + 1), 0, SkinConfig.maxLodLevels + 1);
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
