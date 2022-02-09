package moe.plushie.armourers_workshop.client;

import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class SkinWardrobeContainer extends Container {

    public SkinWardrobeContainer(int containerId, PlayerInventory inventory, PlayerEntity player) {
        super(null, containerId);
        addPlayerSlots(inventory, 51, 168);
    }

    protected void addPlayerSlots(IInventory inventory, int posX, int posY) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, posX + 8 + col * 18, posY + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, posX + 8 + col * 18, posY + 58));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

}
