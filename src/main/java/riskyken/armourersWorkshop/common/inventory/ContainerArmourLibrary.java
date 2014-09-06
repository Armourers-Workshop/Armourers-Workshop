package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;

public class ContainerArmourLibrary extends Container {

    private TileEntityArmourLibrary armourLibrary;
    
    public ContainerArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary armourLibrary) {
        this.armourLibrary = armourLibrary;

        addSlotToContainer(new Slot(armourLibrary, 0, 24, 33));
        addSlotToContainer(new Slot(armourLibrary, 1, 24, 77));

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 173));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 115 + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        return null;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return armourLibrary.isUseableByPlayer(player);
    }
    
    public TileEntityArmourLibrary getTileEntity() {
        return armourLibrary;
    }
}
