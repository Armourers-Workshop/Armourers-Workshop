package riskyken.armourersWorkshop.common.skin.type.block;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinBlock extends AbstractSkinTypeBase {

    public final ISkinPartType partBase;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinBlock() {
        this.skinParts = new ArrayList<ISkinPartType>();
        this.partBase = new SkinBlockPartBase(this);
        this.skinParts.add(this.partBase);
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:block";
    }

    @Override
    public String getName() {
        return "block";
    }
}
