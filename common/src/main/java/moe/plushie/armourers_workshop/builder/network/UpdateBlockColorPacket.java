package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.compat.core.item.AbstractUseOnContext;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class UpdateBlockColorPacket extends CustomPacket {

    final OpenInteractionHand hand;
    final GlobalPos clickedPos;
    final BlockHitResult traceResult;
    final CubePaintingEvent paintingEvent;

    public UpdateBlockColorPacket(IFriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(OpenInteractionHand.class);
        this.clickedPos = buffer.readGlobalPos();
        this.traceResult = buffer.readBlockHitResult();
        this.paintingEvent = new CubePaintingEvent(buffer);
    }

    public UpdateBlockColorPacket(IUseOnContext context, CubePaintingEvent paintingEvent) {
        this.hand = context.hand();
        this.clickedPos = GlobalPos.of(context.level().dimension(), context.clickedPos());
        this.traceResult = context.hitResult();
        this.paintingEvent = paintingEvent;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeGlobalPos(clickedPos);
        buffer.writeBlockHitResult(traceResult);
        paintingEvent.encode(buffer);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        // we don't support modify blocks in multiple dimensions at the same time.
        var level = player.server().getLevel(clickedPos.dimension());
        if (level == null) {
            return;
        }
        try {
            var itemStack = player.getItemInHand(hand);
            var context = AbstractUseOnContext.create(level, player, hand, itemStack, traceResult);
            var collector = new CubeChangesCollector(level);
            paintingEvent.apply(collector, context);
            collector.submit(itemStack.getHoverName(), player);
            applyUseEffects(itemStack, context);
        } catch (Exception exception) {
            // ignore any exception.
            exception.printStackTrace();
        }
    }

    public BlockPos by(IBlockPaintable target) {
        if (target instanceof BlockEntity blockEntity) {
            return blockEntity.getBlockPos();
        }
        return null;
    }

    private void applyUseEffects(ItemStack itemStack, IUseOnContext context) {
        var item = itemStack.getItem();
        if (item instanceof IItemSoundProvider provider) {
            provider.playSound(context);
        }
        if (item instanceof IItemParticleProvider provider) {
            provider.playParticle(context);
        }
    }
}
