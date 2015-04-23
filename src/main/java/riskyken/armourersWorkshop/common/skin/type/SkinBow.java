package riskyken.armourersWorkshop.common.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;

public class SkinBow extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinBow() {
        this.skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinBowPartBase(this));
        skinParts.add(new SkinBowPartFrame1(this));
        skinParts.add(new SkinBowPartFrame2(this));
        skinParts.add(new SkinBowPartArrow(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:bow";
    }

    @Override
    public String getName() {
        return "bow";
    }

    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_BOW);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_BOW);
    }
}
