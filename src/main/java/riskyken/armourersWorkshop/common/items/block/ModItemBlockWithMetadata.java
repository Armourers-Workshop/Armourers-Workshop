package riskyken.armourersWorkshop.common.items.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ModItemBlockWithMetadata extends ItemBlockWithMetadata {

    public ModItemBlockWithMetadata(Block block) {
        super(block, block);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return field_150939_a.getUnlocalizedName() + itemstack.getItemDamage();
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String unlocalized;
        String localized;

        unlocalized = itemStack.getUnlocalizedName() + ".flavour";
        localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            list.add(localized);
        }
        
        super.addInformation(itemStack, player, list, par4);
    }
}
