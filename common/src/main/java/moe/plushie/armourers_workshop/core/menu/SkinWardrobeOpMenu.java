package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class SkinWardrobeOpMenu extends SkinWardrobeMenu {

    public SkinWardrobeOpMenu(MenuType<?> menuType, int containerId, Inventory inventory, SkinWardrobe wardrobe) {
        super(menuType, containerId, inventory, wardrobe);
    }

    @Override
    public boolean stillValid(Player player) {
        // in op mode, we have access wardrobe anytime anywhere.
        Entity entity = getEntity();
        return entity != null && entity.isAlive();
    }
}
