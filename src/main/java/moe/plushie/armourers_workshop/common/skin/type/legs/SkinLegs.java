package moe.plushie.armourers_workshop.common.skin.type.legs;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinLegs extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinLegs() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinLegsPartLeftLeg(this));
        skinParts.add(new SkinLegsPartRightLeg(this));
        skinParts.add(new SkinLegsPartSkirt(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:legs";
    }
    
    @Override
    public String getName() {
        return "Legs";
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT);
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT);
        properties.add(SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS);
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
