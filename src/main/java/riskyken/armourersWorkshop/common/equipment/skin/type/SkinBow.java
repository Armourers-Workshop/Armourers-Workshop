package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinTypeBase;

public class SkinBow extends EquipmentSkinTypeBase {

    private ArrayList<IEquipmentSkinPart> skinParts;
    
    public SkinBow() {
        this.skinParts = new ArrayList<IEquipmentSkinPart>();
        skinParts.add(new SkinBowPartBase(this));
        skinParts.add(new SkinBowPartFrame1(this));
        skinParts.add(new SkinBowPartFrame2(this));
        skinParts.add(new SkinBowPartArrow(this));
    }
    
    @Override
    public ArrayList<IEquipmentSkinPart> getSkinParts() {
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
    }
}
