package riskyken.armourersWorkshop.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;

public class ItemGuideBook extends AbstractModItem {

    public ItemGuideBook() {
        super(LibItemNames.GUIDE_BOOK);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (world.isRemote) {
            entityPlayer.openGui(ArmourersWorkshop.instance, LibGuiIds.GUIDE_BOOK, world, 0, 0, 0);
        }
        return itemStack;
    }
}
