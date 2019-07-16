package riskyken.armourersWorkshop.common.skin.type.item;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinItem extends AbstractSkinTypeBase {

    private final String name;
    private ArrayList<ISkinPartType> skinParts;

    public SkinItem(String name) {
        this.name = name;
        this.skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinItemPartBase(this));
    }

    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:" + name.toLowerCase();
    }

    @Override
    public String getName() {
        return name;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_ITEM + name);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_ITEM + name);
    }
}
