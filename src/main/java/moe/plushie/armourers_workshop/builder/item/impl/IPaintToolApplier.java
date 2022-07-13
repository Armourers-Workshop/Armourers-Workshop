package moe.plushie.armourers_workshop.builder.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.world.SkinCubeApplier;
import moe.plushie.armourers_workshop.builder.world.SkinCubePaintingEvent;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateBlockColorPacket;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nullable;

public interface IPaintToolApplier {

    default ActionResultType usePaintTool(ItemUseContext context) {
        if (!shouldUseTool(context)) {
            return ActionResultType.PASS;
        }
        TileEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (tileEntity == null) {
            return ActionResultType.PASS;
        }
        IPaintToolSelector selector = createPaintToolSelector(tileEntity, context);
        IPaintToolAction action = null;
        if (selector != null) {
            action = createPaintToolAction(context);
        }
        if (selector == null || action == null) {
            return ActionResultType.PASS;
        }
        SkinCubeApplier applier = new SkinCubeApplier(context.getLevel());
        SkinCubePaintingEvent event = new SkinCubePaintingEvent(selector, action);
        if (event.prepare(applier, context)) {
            event.apply(applier, context);
            UpdateBlockColorPacket packet = new UpdateBlockColorPacket(context, event);
            NetworkHandler.getInstance().sendToServer(packet);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Nullable
    IPaintToolAction createPaintToolAction(ItemUseContext context);

    @Nullable
    IPaintToolSelector createPaintToolSelector(ItemUseContext context);

    @Nullable
    default IPaintToolSelector createPaintToolSelector(TileEntity tileEntity, ItemUseContext context) {
        if (tileEntity instanceof IPaintToolSelector.Provider) {
            return ((IPaintToolSelector.Provider) tileEntity).createPaintToolSelector(context);
        }
        if (tileEntity instanceof IPaintable) {
            return createPaintToolSelector(context);
        }
        return null;
    }

    boolean shouldUseTool(ItemUseContext context);
}
