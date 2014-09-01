package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.common.registry.GameRegistry;

public abstract class AbstractModItem extends Item {

    public AbstractModItem(String name) {
        setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        setUnlocalizedName(name);
        setHasSubtypes(false);
        setMaxStackSize(1);
        setNoRepair();
    }
    
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String unlocalized;
        String localized;

        unlocalized = itemStack.getUnlocalizedName() + ".flavor";
        localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            list.add(localized);
        }
        
        super.addInformation(itemStack, player, list, par4);
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
