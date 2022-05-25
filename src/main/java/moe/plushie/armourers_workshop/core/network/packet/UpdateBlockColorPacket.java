package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class UpdateBlockColorPacket extends CustomPacket {

    final Item item;
    final RegistryKey<World> dimension;
    final HashMap<BlockPos, HashMap<Direction, IPaintColor>> changes;

    public UpdateBlockColorPacket(PacketBuffer buffer) {
        this.item = Item.byId(buffer.readInt());
        this.dimension = getDimension(buffer);
        this.changes = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = buffer.readBlockPos();
            HashMap<Direction, IPaintColor> colors = new HashMap<>();
            int colorTotal = buffer.readByte();
            for (int j = 0; j < colorTotal; ++j) {
                Direction dir = buffer.readEnum(Direction.class);
                IPaintColor color = PaintColor.of(buffer.readInt());
                colors.put(dir, color);
            }
            changes.put(pos, colors);
        }
    }

    public UpdateBlockColorPacket(Item item, HashMap<IPaintable, HashMap<Direction, IPaintColor>> changes) {
        this.item = item;
        this.dimension = getDimension(changes.keySet());
        this.changes = new HashMap<>();
        changes.forEach((target, colors) -> {
            BlockPos pos = by(target);
            if (pos != null) {
                this.changes.put(pos, colors);
            }
        });
    }

    @Override
    public void encode(PacketBuffer buffer) {
        try {
            buffer.writeInt(Item.getId(item));
            buffer.writeWithCodec(GlobalPos.CODEC, GlobalPos.of(dimension, BlockPos.ZERO));
            buffer.writeInt(changes.size());
            changes.forEach((pos, colors) -> {
                buffer.writeBlockPos(pos);
                buffer.writeByte(colors.size());
                colors.forEach((dir, color) -> {
                    buffer.writeEnum(dir);
                    buffer.writeInt(color.getRawValue());
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check player
        // we don't support modify blocks in multiple dimensions at the same time.
        World world = player.server.getLevel(dimension);
        if (world == null) {
            return;
        }
        TileEntityUpdateCombiner.begin();
        changes.forEach((pos, colors) -> {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof IPaintable) {
                ((IPaintable) tileEntity).setColors(colors);
            }
        });
        TileEntityUpdateCombiner.end();
    }

    public BlockPos by(IPaintable target) {
        if (target instanceof TileEntity) {
            return ((TileEntity) target).getBlockPos();
        }
        return null;
    }

    private RegistryKey<World> getDimension(PacketBuffer buffer) {
        try {
            GlobalPos pos = buffer.readWithCodec(GlobalPos.CODEC);
            return pos.dimension();
        } catch (Exception e) {
            return null;
        }
    }

    private RegistryKey<World> getDimension(Collection<IPaintable> targets) {
        for (IPaintable target : targets) {
            if (target instanceof TileEntity) {
                World world = ((TileEntity) target).getLevel();
                if (world != null) {
                    return world.dimension();
                }
            }
        }
        return null;
    }
}
