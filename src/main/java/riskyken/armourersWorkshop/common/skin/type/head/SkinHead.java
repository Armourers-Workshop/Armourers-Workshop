package riskyken.armourersWorkshop.common.skin.type.head;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

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

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_HEAD);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_HEAD);
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
}
