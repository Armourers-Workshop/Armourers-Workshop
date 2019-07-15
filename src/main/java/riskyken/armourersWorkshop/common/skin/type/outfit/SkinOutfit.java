package riskyken.armourersWorkshop.common.skin.type.outfit;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinOutfit extends AbstractSkinTypeBase {

    private final ISkinType[] skinTypes;
    private ArrayList<ISkinPartType> skinParts;

    public SkinOutfit(ISkinType... skinTypes) {
        this.skinTypes = skinTypes;
        this.skinParts = new ArrayList<ISkinPartType>();
        for (int i = 0; i < skinTypes.length; i++) {
            skinParts.addAll(skinTypes[i].getSkinParts());
        }
    }

    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:outfit";
    }

    @Override
    public String getName() {
        return "Outfit";
    }

    @Override
    public boolean isHidden() {
        return false;
    }
    
    @SideOnly(Side.CLIENT) 
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_OUTFIT);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_OUTFIT);
    }
}
