package moe.plushie.armourers_workshop.builder.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.core.menu.AbstractBlockEntityMenu;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AdvancedBuilderMenu extends AbstractBlockEntityMenu<AdvancedBuilderBlockEntity> implements SkinDocumentProvider {

    public AdvancedBuilderMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    @Override
    public SkinDocument getDocument() {
        return blockEntity.getDocument();
    }
}
