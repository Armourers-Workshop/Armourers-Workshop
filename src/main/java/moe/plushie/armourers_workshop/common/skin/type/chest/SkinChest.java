package moe.plushie.armourers_workshop.common.skin.type.chest;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinChest extends AbstractSkinTypeBase {
    
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinChest() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinChestPartBase(this));
        skinParts.add(new SkinChestPartLeftArm(this));
        skinParts.add(new SkinChestPartRightArm(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:chest";
    }
    
    @Override
    public String getName() {
        return "Chest";
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 1;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_CHEST);
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT);
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT);
        return properties;
    }
    
    @Override
    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
        if (SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skinPropsOld) != SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skinPropsNew)) {
            return true;
        }
        if (SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.getValue(skinPropsOld) != SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.getValue(skinPropsNew)) {
            return true;
        }
        if (SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.getValue(skinPropsOld) != SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.getValue(skinPropsNew)) {
            return true;
        }
        return false;
    }
}
