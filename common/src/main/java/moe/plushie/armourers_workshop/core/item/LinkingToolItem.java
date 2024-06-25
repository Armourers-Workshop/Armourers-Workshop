package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class LinkingToolItem extends FlavouredItem implements IItemHandler, IItemPropertiesProvider {

    public LinkingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createModelProperties(BiConsumer<IResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> {
            if (itemStack.has(ModDataComponents.LINKED_POS.get())) {
                return 0;
            }
            return 1;
        });
    }

    @Override
    public InteractionResult useOnFirst(ItemStack itemStack, UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        var linkedBlockPos = itemStack.get(ModDataComponents.LINKED_POS.get());
        var blockEntity = getTitleEntity(level, context.getClickedPos());
        if (blockEntity != null && player.isSecondaryUseActive()) {
            blockEntity.setLinkedBlockPos(null);
            player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.clear"));
            return InteractionResult.SUCCESS;
        }
        if (linkedBlockPos != null) {
            itemStack.remove(ModDataComponents.LINKED_POS.get());
            if (blockEntity != null) {
                blockEntity.setLinkedBlockPos(linkedBlockPos);
                player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.finish"));
                return InteractionResult.SUCCESS;
            }
            player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.fail"));
            return InteractionResult.SUCCESS;
        }
        if (blockEntity != null) {
            player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.linkedToSkinnable"));
            return InteractionResult.FAIL;
        }
        itemStack.set(ModDataComponents.LINKED_POS.get(), context.getClickedPos());
        player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.start"));
        return InteractionResult.SUCCESS;
    }

    private SkinnableBlockEntity getTitleEntity(Level level, BlockPos blockPos) {
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof SkinnableBlockEntity) {
            return (SkinnableBlockEntity) blockEntity;
        }
        return null;
    }
}
