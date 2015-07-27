package riskyken.armourersWorkshop.common.skin.type;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;

public class SkinArrow extends AbstractSkinTypeBase {
    
    public final ISkinPartType partBase;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinArrow() {
        this.skinParts = new ArrayList<ISkinPartType>();
        this.partBase = new SkinArrowPartBase(this);
        this.skinParts.add(this.partBase);
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:arrow";
    }

    @Override
    public String getName() {
        return "arrow";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_ARROW);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_ARROW);
    }
}
