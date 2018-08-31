package riskyken.armourers_workshop.common.skin.type.item;

import java.util.ArrayList;

import riskyken.armourers_workshop.api.common.skin.type.ISkinPartType;
import riskyken.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinItem extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinItem() {
        this.skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinItemPartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:sword";
    }
    
    @Override
    public String getName() {
        return "Sword";
    }
}
