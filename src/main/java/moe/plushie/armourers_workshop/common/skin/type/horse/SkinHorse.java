package moe.plushie.armourers_workshop.common.skin.type.horse;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinHorse extends AbstractSkinTypeBase {
    
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinHorse() {
        this.skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinHorsePartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:horse";
    }
    
    @Override
    public String getName() {
        return "horse";
    }
    
    @Override
    public boolean enabled() {
        return false;
    }
}
