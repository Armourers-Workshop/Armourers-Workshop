package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class SkinWardrobeOpMenu extends SkinWardrobeMenu {

    public SkinWardrobeOpMenu(IMenuType<?> menuType, int containerId, Inventory inventory, SkinWardrobe wardrobe) {
        super(menuType, containerId, inventory, wardrobe);
    }

    @Override
    protected boolean abi$stillValid(Player player) {
        // in op mode, we have access wardrobe anytime anywhere.
        var entity = entity();
        return entity != null && entity.isAlive();
    }
}
