package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.world.SkinCubeApplier;
import moe.plushie.armourers_workshop.builder.world.SkinCubePaintingEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class UpdateBlockColorPacket extends CustomPacket {

    final Hand hand;
    final GlobalPos clickedPos;
    final BlockRayTraceResult traceResult;
    final SkinCubePaintingEvent paintingEvent;

    public UpdateBlockColorPacket(PacketBuffer buffer) {
        this.hand = buffer.readEnum(Hand.class);
        this.clickedPos = readGlobalPos(buffer);
        this.traceResult = buffer.readBlockHitResult();
        this.paintingEvent = new SkinCubePaintingEvent(buffer);
    }

    public UpdateBlockColorPacket(ItemUseContext context, SkinCubePaintingEvent paintingEvent) {
        this.hand = context.getHand();
        this.clickedPos = GlobalPos.of(context.getLevel().dimension(), context.getClickedPos());
        this.traceResult = new BlockRayTraceResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
        this.paintingEvent = paintingEvent;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        try {
            buffer.writeEnum(hand);
            buffer.writeWithCodec(GlobalPos.CODEC, clickedPos);
            buffer.writeBlockHitResult(traceResult);
            paintingEvent.encode(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check player
        // we don't support modify blocks in multiple dimensions at the same time.
        World world = player.server.getLevel(clickedPos.dimension());
        if (world == null) {
            return;
        }
        try {
            ItemStack itemStack = player.getItemInHand(hand);
            ItemUseContext context = new ItemUseContext(world, player, hand, itemStack, traceResult);
            SkinCubeApplier applier = new SkinCubeApplier(world);
            paintingEvent.apply(applier, context);
            applier.submit(itemStack.getHoverName(), player);
            applyUseEffects(itemStack, context);
        } catch (Exception exception) {
            // ignore any exception.
            exception.printStackTrace();
        }
    }

    public BlockPos by(IPaintable target) {
        if (target instanceof TileEntity) {
            return ((TileEntity) target).getBlockPos();
        }
        return null;
    }

    private void applyUseEffects(ItemStack itemStack, ItemUseContext context) {
        Item item = itemStack.getItem();
        if (item instanceof IItemSoundProvider) {
            ((IItemSoundProvider) item).playSound(context);
        }
        if (item instanceof IItemParticleProvider) {
            ((IItemParticleProvider) item).playParticle(context);
        }
    }

    private GlobalPos readGlobalPos(PacketBuffer buffer) {
        try {
            return buffer.readWithCodec(GlobalPos.CODEC);
        } catch (Exception e) {
            return null;
        }
    }
}
