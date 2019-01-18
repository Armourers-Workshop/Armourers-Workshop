package moe.plushie.armourers_workshop.common.skin.type.outfit;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinOutfit extends AbstractSkinTypeBase {
    
    private final ISkinType[]  skinTypes;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinOutfit(ISkinType...  skinTypes) {
        this.skinTypes = skinTypes;
        this.skinParts = new ArrayList<ISkinPartType>();
        for (int i = 0; i < skinTypes.length; i++) {
            skinParts.addAll(skinTypes[i].getSkinParts());
        }
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:outfit";
    }
    
    @Override
    public String getName() {
        return "Outfit";
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
}
