package riskyken.armourersWorkshop.client.skin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.model.SkinModel;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.client.render.SkinPartRenderData;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;

@SideOnly(Side.CLIENT)
public class ClientSkinPartData {

    /** Blank dye that is used if no dye is applied. */
    public static final SkinDye blankDye = new SkinDye();
    public ArrayList<ColouredFace>[] vertexLists;
    public HashMap<ModelKey, SkinModel> dyeModels;
    public int[] totalCubesInPart;

    private int[] averageR = new int[10];
    private int[] averageG = new int[10];
    private int[] averageB = new int[10];

    public ClientSkinPartData() {
        dyeModels = new HashMap<ModelKey, SkinModel>();
    }

    public SkinModel getModelForDye(SkinPartRenderData renderData) {
        ModelKey modelKey = new ModelKey(renderData.getSkinDye(), renderData.getExtraColours());
        SkinModel skinModel = dyeModels.get(modelKey);
        if (skinModel == null) {
            skinModel = new SkinModel(vertexLists);
            dyeModels.put(modelKey, skinModel);
        }
        return skinModel;
    }

    public void cleanUpDisplayLists() {
        Set keys = dyeModels.keySet();
        Iterator<ModelKey> i = dyeModels.keySet().iterator();
        while (i.hasNext()) {
            ModelKey modelKey = i.next();
            dyeModels.get(modelKey).cleanUpDisplayLists();
        }
    }

    public int getModelCount() {
        return dyeModels.size();
    }

    public void setVertexLists(ArrayList<ColouredFace>[] vertexLists) {
        this.vertexLists = vertexLists;
    }

    public void setAverageDyeValues(int[] r, int[] g, int[] b) {
        this.averageR = r;
        this.averageG = g;
        this.averageB = b;
    }

    public int[] getAverageDyeColour(int dyeNumber) {
        return new int[] { averageR[dyeNumber], averageG[dyeNumber], averageB[dyeNumber] };
    }

    private class ModelKey {

        private ISkinDye skinDye;
        byte[] extraColours;

        public ModelKey(ISkinDye skinDye, byte[] extraColours) {
            if (skinDye == null) {
                this.skinDye = blankDye;
            } else {
                this.skinDye = skinDye;
            }
            this.extraColours = extraColours;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(extraColours);
            result = prime * result + ((skinDye == null) ? 0 : skinDye.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ModelKey other = (ModelKey) obj;
            if (!Arrays.equals(extraColours, other.extraColours))
                return false;
            if (skinDye == null) {
                if (other.skinDye != null)
                    return false;
            } else if (!skinDye.equals(other.skinDye))
                return false;
            return true;
        }
    }
}
