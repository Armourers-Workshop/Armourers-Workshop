package moe.plushie.armourers_workshop.common.skin.type.item;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinItem extends AbstractSkinTypeBase {

    private final String name;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinItem(String name) {
        this.skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinItemPartBase(this));
        this.name = name;
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:" + name.toLowerCase();
    }
    
    @Override
    public String getName() {
        return name;
    }
}
