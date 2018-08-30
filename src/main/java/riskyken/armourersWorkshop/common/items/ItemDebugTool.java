package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;

public class ItemDebugTool extends AbstractModItem {

    public ItemDebugTool() {
        super(LibItemNames.DEBUG_TOOL, true);
        setSortPriority(-1);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            playerIn.openGui(ArmourersWorkshop.instance, LibGuiIds.DEBUG_TOOL, worldIn, 0, 0, 0);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
    }
    
    public static interface IDebug {   
        public void getDebugHoverText(World world, int x, int y, int z, ArrayList<String> textLines);
    }
}
