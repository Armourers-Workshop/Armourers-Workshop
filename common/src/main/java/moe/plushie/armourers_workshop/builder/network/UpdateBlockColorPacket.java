package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class UpdateBlockColorPacket extends CustomPacket {

    final InteractionHand hand;
    final GlobalPos clickedPos;
    final BlockHitResult traceResult;
    final CubePaintingEvent paintingEvent;

    public UpdateBlockColorPacket(FriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(InteractionHand.class);
        this.clickedPos = buffer.readGlobalPos();
        this.traceResult = buffer.readBlockHitResult();
        this.paintingEvent = new CubePaintingEvent(buffer);
    }

    public UpdateBlockColorPacket(UseOnContext context, CubePaintingEvent paintingEvent) {
        this.hand = context.getHand();
        this.clickedPos = GlobalPos.of(context.getLevel().dimension(), context.getClickedPos());
        this.traceResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
        this.paintingEvent = paintingEvent;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeGlobalPos(clickedPos);
        buffer.writeBlockHitResult(traceResult);
        paintingEvent.encode(buffer);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        // we don't support modify blocks in multiple dimensions at the same time.
        Level level = player.server.getLevel(clickedPos.dimension());
        if (level == null) {
            return;
        }
        try {
            ItemStack itemStack = player.getItemInHand(hand);
            UseOnContext context = new UseOnContext(level, player, hand, itemStack, traceResult);
            CubeChangesCollector collector = new CubeChangesCollector(level);
            paintingEvent.apply(collector, context);
            collector.submit(itemStack.getHoverName(), player);
            applyUseEffects(itemStack, context);
        } catch (Exception exception) {
            // ignore any exception.
            exception.printStackTrace();
        }
    }

    public BlockPos by(IPaintable target) {
        if (target instanceof BlockEntity) {
            return ((BlockEntity) target).getBlockPos();
        }
        return null;
    }

    private void applyUseEffects(ItemStack itemStack, UseOnContext context) {
        Item item = itemStack.getItem();
        if (item instanceof IItemSoundProvider) {
            ((IItemSoundProvider) item).playSound(context);
        }
        if (item instanceof IItemParticleProvider) {
            ((IItemParticleProvider) item).playParticle(context);
        }
    }
}
