package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.network.UpdateBlockColorPacket;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IPaintToolApplier {

    default InteractionResult usePaintTool(UseOnContext context) {
        if (!shouldUseTool(context)) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (blockEntity == null) {
            return InteractionResult.PASS;
        }
        IPaintToolSelector selector = createPaintToolSelector(blockEntity, context);
        IPaintToolAction action = null;
        if (selector != null) {
            action = createPaintToolAction(context);
        }
        if (selector == null || action == null) {
            return InteractionResult.PASS;
        }
        CubeChangesCollector collector = new CubeChangesCollector(context.getLevel());
        CubePaintingEvent event = new CubePaintingEvent(selector, action);
        if (event.prepare(collector, context)) {
            event.apply(collector, context);
            UpdateBlockColorPacket packet = new UpdateBlockColorPacket(context, event);
            NetworkManager.sendToServer(packet);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    IPaintToolAction createPaintToolAction(UseOnContext context);

    @Nullable
    IPaintToolSelector createPaintToolSelector(UseOnContext context);

    @Nullable
    default IPaintToolSelector createPaintToolSelector(BlockEntity blockEntity, UseOnContext context) {
        if (blockEntity instanceof IPaintToolSelector.Provider) {
            return ((IPaintToolSelector.Provider) blockEntity).createPaintToolSelector(context);
        }
        if (blockEntity instanceof IPaintable) {
            return createPaintToolSelector(context);
        }
        return null;
    }

    boolean shouldUseTool(UseOnContext context);
}
