package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class LinkingToolItem extends FlavouredItem {

    public LinkingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    protected OpenInteractionResult abi$useOnFirst(ItemStack itemStack, IUseOnContext context) {
        var level = context.level();
        var player = context.player();
        if (level.isClientSide() || player == null) {
            return OpenInteractionResult.SUCCESS;
        }
        var linkedPos = itemStack.get(ModDataComponents.LINKED_POS.get());
        var blockEntity = getBlockEntity(level, context.clickedPos());
        if (blockEntity != null && player.isSecondaryUseActive()) {
            blockEntity.setLinkedPos(null);
            player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.clear"));
            return OpenInteractionResult.SUCCESS;
        }
        if (linkedPos != null) {
            // check the target block dimension and distance.
            if (Objects.equals(level.dimension(), linkedPos.dimension())) {
                // the user allow link max distance is beyond?
                var maxDistance = ModConfig.Common.maxLinkDistance;
                if (maxDistance > 0 && !context.clickedPos().closerThan(linkedPos.pos(), maxDistance + 0.5)) {
                    player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.targetTooFar"));
                    return OpenInteractionResult.FAIL;
                }
            } else {
                // the user allow link a different dimensions block?
                if (!ModConfig.Common.enableLinkDimensional) {
                    player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.targetWrongDimensions"));
                    return OpenInteractionResult.FAIL;
                }
            }
            itemStack.remove(ModDataComponents.LINKED_POS.get());
            if (blockEntity != null) {
                blockEntity.setLinkedPos(linkedPos);
                player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.finish"));
                return OpenInteractionResult.SUCCESS;
            }
            player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.fail"));
            return OpenInteractionResult.SUCCESS;
        }
        if (blockEntity != null) {
            player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.linkedToSkinnable"));
            return OpenInteractionResult.FAIL;
        }
        itemStack.set(ModDataComponents.LINKED_POS.get(), GlobalPos.of(level.dimension(), context.clickedPos()));
        player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.linking-tool.start"));
        return OpenInteractionResult.SUCCESS;
    }

    @Override
    public void abi$appendModelProperties(BiConsumer<OpenResourceKey, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> {
            if (itemStack.has(ModDataComponents.LINKED_POS.get())) {
                return 0;
            }
            return 1;
        });
    }

    private SkinnableBlockEntity getBlockEntity(Level level, BlockPos blockPos) {
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof SkinnableBlockEntity skinnableBlockEntity) {
            return skinnableBlockEntity;
        }
        return null;
    }
}
