package moe.plushie.armourers_workshop.common.skin.type.json;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinJson extends AbstractSkinTypeBase {

    private final ArrayList<ISkinPartType> skinParts = new ArrayList<ISkinPartType>();
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
