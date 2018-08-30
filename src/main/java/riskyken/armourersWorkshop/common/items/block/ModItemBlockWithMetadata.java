package riskyken.armourersWorkshop.common.items.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.creativetab.ISortOrder;

public class ModItemBlockWithMetadata extends ItemBlock implements ISortOrder {

    public ModItemBlockWithMetadata(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return block.getUnlocalizedName() + itemstack.getItemDamage();
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String unlocalized;
        String localized;

        unlocalized = itemStack.getUnlocalizedName() + ".flavour";
        localized = I18n.format(unlocalized);
        if (!unlocalized.equals(localized)) {
            if (localized.contains("%n")) {
                String[] split = localized.split("%n");
                for (int i = 0; i < split.length; i++) {
                    list.add(split[i]);
                }
            } else {
                list.add(localized);
            }
        }
        
        super.addInformation(itemStack, player, list, par4);
    }

    @Override
    public int getSortPriority() {
        if (block instanceof ISortOrder) {
            return ((ISortOrder)block).getSortPriority();
        }
        return 100;
    }
}
