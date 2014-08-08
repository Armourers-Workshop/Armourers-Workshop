package riskyken.armourersWorkshop.common.items.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ModItemBlock extends ItemBlock {

    public ModItemBlock(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List par3List, boolean par4) {
        String unlocalized;
        String localized;

        unlocalized = itemStack.getUnlocalizedName() + ".lore";
        localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            par3List.add(localized);
        }

        for (int i = 0; i < 10; i++) {
            unlocalized = itemStack.getUnlocalizedName() + ".lore" + i;
            localized = StatCollector.translateToLocal(unlocalized);
            if (!unlocalized.equals(localized)) {
                par3List.add(localized);
            } else {
                break;
            }
        }

        super.addInformation(itemStack, player, par3List, par4);
    }
}
