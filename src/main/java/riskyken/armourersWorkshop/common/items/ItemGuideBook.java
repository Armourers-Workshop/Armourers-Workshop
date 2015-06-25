package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.minecraftWrapper.common.entity.EntityPlayerPointer;
import riskyken.minecraftWrapper.common.item.ItemStackPointer;
import riskyken.minecraftWrapper.common.world.BlockLocation;
import riskyken.minecraftWrapper.common.world.WorldPointer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGuideBook extends AbstractModItemNew {

    public ItemGuideBook() {
        super(LibItemNames.GUIDE_BOOK);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
        iconList.add(LibItemResources.GUIDE_BOOK);
    }
    
    @Override
    public ItemStackPointer onItemRightClick(ItemStackPointer stack,
            WorldPointer world, EntityPlayerPointer player) {
        if (world.isRemote()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.GUIDE_BOOK, world, new BlockLocation(0, 0, 0));
        }
        return stack;
    }
}
