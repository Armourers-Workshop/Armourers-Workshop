package moe.plushie.armourers_workshop.client.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.SkinModel;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSkinPartData {

    /** Blank dye that is used if no dye is applied. */
    public static final SkinDye blankDye = new SkinDye();
    public ArrayList<ColouredFace>[] vertexLists;
    public HashMap<ModelKey, SkinModel> dyeModels;
    public int[] totalCubesInPart;
    
    private int[] averageR = new int[12];
    private int[] averageG = new int[12];
    private int[] averageB = new int[12];
    
    public ClientSkinPartData() {
        dyeModels = new HashMap<ModelKey, SkinModel>();
    }
    
    public SkinModel getModelForDye(ISkinDye skinDye, ExtraColours extraColours) {
        if (skinDye == null) {
            skinDye = blankDye;
        }
        ModelKey modelKey = new ModelKey(skinDye, extraColours);
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
        private ExtraColours extraColours;
        
        public ModelKey(ISkinDye skinDye, ExtraColours extraColours) {
            this.skinDye = skinDye;
            this.extraColours = extraColours;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((extraColours == null) ? 0 : extraColours.hashCode());
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
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (extraColours == null) {
                if (other.extraColours != null)
                    return false;
            } else if (!extraColours.equals(other.extraColours))
                return false;
            if (skinDye == null) {
                if (other.skinDye != null)
                    return false;
            } else if (!skinDye.equals(other.skinDye))
                return false;
            return true;
        }

        private ClientSkinPartData getOuterType() {
            return ClientSkinPartData.this;
        }
        
        
    }
}
