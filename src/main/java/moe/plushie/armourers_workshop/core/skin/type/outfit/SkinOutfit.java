package moe.plushie.armourers_workshop.core.skin.type.outfit;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;

import java.util.ArrayList;
import java.util.List;

public class SkinOutfit extends AbstractSkinType {

    private final ISkinType[]  skinTypes;
    private ArrayList<ISkinPartType> skinParts;

    public SkinOutfit(ISkinType...  skinTypes) {
        this.skinTypes = skinTypes;
        this.skinParts = new ArrayList<>();
        for (int i = 0; i < skinTypes.length; i++) {
            skinParts.addAll(skinTypes[i].getParts());
        }
    }

    @Override
    public List<? extends ISkinPartType> getParts() {
        return this.skinParts;
    }


    @Override
    public boolean isHidden() {
        return false;
    }
}
