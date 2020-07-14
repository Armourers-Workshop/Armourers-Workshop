package riskyken.armourersWorkshop.common.skin.type.feet;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

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

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_FEET);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_FEET);
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
        properties.add(SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS);
        return properties;
    }
}
