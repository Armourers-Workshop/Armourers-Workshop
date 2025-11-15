package moe.plushie.armourers_workshop.builder.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.core.menu.BlockEntityContainerMenu;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AdvancedBuilderMenu extends BlockEntityContainerMenu<AdvancedBuilderBlockEntity> implements SkinDocumentProvider {

    public AdvancedBuilderMenu(IMenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
    }

    @Override
    public SkinDocument document() {
        return blockEntity.document();
    }

    @Override
    protected ItemStack abi$quickMoveStack(Player player, int index) {
        return abi$quickMoveStack(player, index, slots.size() - 1);
    }
}
