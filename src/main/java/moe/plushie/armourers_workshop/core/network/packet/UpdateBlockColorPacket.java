package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.world.SkinCubeColorApplier;
import moe.plushie.armourers_workshop.builder.world.SkinCubeOptimizer;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.undo.action.SetBlockColorAction;
import moe.plushie.armourers_workshop.utils.undo.action.UndoNamedGroupAction;
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
    final SkinCubeColorApplier applier;
//    final HashMap<BlockPos, HashMap<Direction, IPaintColor>> changes;

    public UpdateBlockColorPacket(PacketBuffer buffer) {
        this.hand = buffer.readEnum(Hand.class);
        this.clickedPos = readGlobalPos(buffer);
        this.traceResult = buffer.readBlockHitResult();
        this.applier = new SkinCubeColorApplier(buffer);
//        this.changes = new HashMap<>();
//        int size = buffer.readInt();
//        for (int i = 0; i < size; ++i) {
//            BlockPos pos = buffer.readBlockPos();
//            HashMap<Direction, IPaintColor> colors = new HashMap<>();
//            int colorTotal = buffer.readByte();
//            for (int j = 0; j < colorTotal; ++j) {
//                Direction dir = buffer.readEnum(Direction.class);
//                IPaintColor color = PaintColor.of(buffer.readInt());
//                colors.put(dir, color);
//            }
//            changes.put(pos, colors);
//        }
    }

    public UpdateBlockColorPacket(ItemUseContext context, SkinCubeColorApplier applier) {
        this.hand = context.getHand();
        this.clickedPos = GlobalPos.of(context.getLevel().dimension(), context.getClickedPos());
        this.traceResult = new BlockRayTraceResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
        this.applier = applier;
//        this.changes = new HashMap<>();
//        changes.forEach((target, colors) -> {
//            BlockPos pos = by(target);
//            if (pos != null) {
//                this.changes.put(pos, colors);
//            }
//        });
    }

    @Override
    public void encode(PacketBuffer buffer) {
        try {
            buffer.writeEnum(hand);
            buffer.writeWithCodec(GlobalPos.CODEC, clickedPos);
            buffer.writeBlockHitResult(traceResult);
            applier.encode(buffer);
//            buffer.writeInt(changes.size());
//            changes.forEach((pos, colors) -> {
//                buffer.writeBlockPos(pos);
//                buffer.writeByte(colors.size());
//                colors.forEach((dir, color) -> {
//                    buffer.writeEnum(dir);
//                    buffer.writeInt(color.getRawValue());
//                });
//            });
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
        TileEntityUpdateCombiner.begin();
        try {
            ItemStack itemStack = player.getItemInHand(hand);
            ItemUseContext context = new ItemUseContext(world, player, hand, itemStack, traceResult);
            UndoNamedGroupAction group = new UndoNamedGroupAction(itemStack.getHoverName());
            applier.apply(context, new SkinCubeOptimizer(world) {
                @Override
                public void submit(IPaintable target) {
                    group.push(new SetBlockColorAction(world, targetPos, changes));
                }
            });
            UndoManager.of(player.getUUID()).push(group.apply());
            applyUseEffects(itemStack, context);
        } catch (Exception exception) {
            // ignore any exception.
            exception.printStackTrace();
        }
        TileEntityUpdateCombiner.end();
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
