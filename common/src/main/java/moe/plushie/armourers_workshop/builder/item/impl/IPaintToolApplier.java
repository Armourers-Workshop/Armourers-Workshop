package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.builder.network.UpdateBlockColorPacket;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IPaintToolApplier {

    default OpenInteractionResult usePaintTool(IUseOnContext context) {
        if (!shouldUseTool(context)) {
            return OpenInteractionResult.PASS;
        }
        var blockEntity = context.level().getBlockEntity(context.clickedPos());
        if (blockEntity == null) {
            return OpenInteractionResult.PASS;
        }
        var selector = createPaintToolSelector(blockEntity, context);
        IPaintToolAction action = null;
        if (selector != null) {
            action = createPaintToolAction(context);
        }
        if (selector == null || action == null) {
            return OpenInteractionResult.PASS;
        }
        var collector = new CubeChangesCollector(context.level());
        var event = new CubePaintingEvent(selector, action);
        if (event.prepare(collector, context)) {
            event.apply(collector, context);
            var packet = new UpdateBlockColorPacket(context, event);
            NetworkManager.sendToServer(packet);
            return OpenInteractionResult.SUCCESS;
        }
        return OpenInteractionResult.PASS;
    }

    @Nullable
    IPaintToolAction createPaintToolAction(IUseOnContext context);

    @Nullable
    IPaintToolSelector createPaintToolSelector(IUseOnContext context);

    @Nullable
    default IPaintToolSelector createPaintToolSelector(BlockEntity blockEntity, IUseOnContext context) {
        if (blockEntity instanceof IPaintToolSelector.Provider provider) {
            return provider.createPaintToolSelector(context);
        }
        if (blockEntity instanceof IBlockPaintable) {
            return createPaintToolSelector(context);
        }
        return null;
    }

    boolean shouldUseTool(IUseOnContext context);
}
