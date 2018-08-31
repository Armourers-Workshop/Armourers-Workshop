package riskyken.armourers_workshop.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.lib.LibGuiIds;
import riskyken.armourers_workshop.common.lib.LibItemNames;

public class ItemGuideBook extends AbstractModItem {

    public ItemGuideBook() {
        super(LibItemNames.GUIDE_BOOK);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.isRemote) {
            playerIn.openGui(ArmourersWorkshop.instance, LibGuiIds.GUIDE_BOOK, worldIn, 0, 0, 0);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
