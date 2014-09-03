package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractModItemArmor extends ItemArmor {

    public AbstractModItemArmor(String name, ArmorMaterial armorMaterial, int armorType) {
        super(armorMaterial, 0, armorType);
        //setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        setUnlocalizedName(name);
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_) {
        return 16777215;
    }
    
    @Override
    public int getColor(ItemStack p_82814_1_) {
        return -1;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return false;
    }
    
    @Override
    public Item setUnlocalizedName(String name) {
        GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return getModdedUnlocalizedName(super.getUnlocalizedName(itemStack));
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        return "item." + LibModInfo.ID.toLowerCase() + ":" + name;
    }
}
