package moe.plushie.armourers_workshop.common.skin.type.feet;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinFeet extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinFeet() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinFeetPartLeftFoot(this));
        skinParts.add(new SkinFeetPartRightFoot(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:feet";
    }
    
    @Override
    public String getName() {
        return "Feet";
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 3;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT);
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT);
        return properties;
    }
    
    @Override
    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
        if (SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.getValue(skinPropsOld) != SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.getValue(skinPropsNew)) {
            return true;
        }
        if (SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.getValue(skinPropsOld) != SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.getValue(skinPropsNew)) {
            return true;
        }
        return false;
    }
}
