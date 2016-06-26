package riskyken.armourersWorkshop.common.items;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.plushieWrapper.common.entity.PlushieEntityPlayer;
import riskyken.plushieWrapper.common.item.PlushieItemStack;

public class ItemGuideBook extends AbstractModItemNew {

    public ItemGuideBook() {
        super(LibItemNames.GUIDE_BOOK);
    }
    
    @Override
    public PlushieItemStack onItemRightClick(PlushieItemStack stack,
            WorldPointer world, PlushieEntityPlayer player) {
        if (world.isRemote()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.GUIDE_BOOK, world, new BlockLocation(0, 0, 0));
        }
        return stack;
    }
}
