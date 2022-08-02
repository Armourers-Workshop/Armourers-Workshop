package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.network.UpdateBlockColorPacket;
import moe.plushie.armourers_workshop.builder.other.CubeApplier;
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
        BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (tileEntity == null) {
            return InteractionResult.PASS;
        }
        IPaintToolSelector selector = createPaintToolSelector(tileEntity, context);
        IPaintToolAction action = null;
        if (selector != null) {
            action = createPaintToolAction(context);
        }
        if (selector == null || action == null) {
            return InteractionResult.PASS;
        }
        CubeApplier applier = new CubeApplier(context.getLevel());
        CubePaintingEvent event = new CubePaintingEvent(selector, action);
        if (event.prepare(applier, context)) {
            event.apply(applier, context);
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
    default IPaintToolSelector createPaintToolSelector(BlockEntity tileEntity, UseOnContext context) {
        if (tileEntity instanceof IPaintToolSelector.Provider) {
            return ((IPaintToolSelector.Provider) tileEntity).createPaintToolSelector(context);
        }
        if (tileEntity instanceof IPaintable) {
            return createPaintToolSelector(context);
        }
        return null;
    }

    boolean shouldUseTool(UseOnContext context);
}
