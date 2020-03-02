package moe.plushie.armourers_workshop.common.skin.type.head;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinHead extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinHead() {
        this.skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinHeadPartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:head";
    }
    
    @Override
    public String getName() {
        return "Head";
    }

    @Override
    public boolean showSkinOverlayCheckbox() {
        return true;
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 0;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_MODEL_OVERRIDE_HEAD);
        properties.add(SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD);
        return properties;
    }
    
    @Override
    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
        return SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skinPropsOld) != SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skinPropsNew);
    }
}
