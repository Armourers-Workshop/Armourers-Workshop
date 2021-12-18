package moe.plushie.armourers_workshop.core.skin.type;

import moe.plushie.armourers_workshop.core.api.common.skin.*;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public abstract class AbstractSkinType implements ISkinType {


    protected String registryName;

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation("LibModInfo.ID", "textures/items/skin/template-" + getRegistryName() + ".png");
    }

    @Override
    public ResourceLocation getSlotIcon() {
        return new ResourceLocation("LibModInfo.ID", "textures/items/slot/skin-" + getRegistryName() + ".png");
    }

    @Override
    public boolean showSkinOverlayCheckbox() {
        return false;
    }

    @Override
    public boolean showHelperCheckbox() {
        return false;
    }

    @Override
    public int getVanillaArmourSlotId() {
        return -1;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = new ArrayList<ISkinProperty<?>>();
        properties.add(SkinProperty.ALL_FLAVOUR_TEXT);
        return properties;
    }

    @Override
    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
        return true;
    }
}
