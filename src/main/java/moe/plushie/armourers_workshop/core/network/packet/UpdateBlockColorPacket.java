package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class UpdateBlockColorPacket extends CustomPacket {

    final Item item;
    final HashMap<GlobalPos, HashMap<Direction, IPaintColor>> changes;

    public UpdateBlockColorPacket(PacketBuffer buffer) {
        this.item = Item.byId(buffer.readInt());
        this.changes = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; ++i) {
            try {
                GlobalPos pos = buffer.readWithCodec(GlobalPos.CODEC);
                HashMap<Direction, IPaintColor> colors = new HashMap<>();
                int colorTotal = buffer.readByte();
                for (int j = 0; j < colorTotal; ++j) {
                    Direction dir = buffer.readEnum(Direction.class);
                    IPaintColor color = PaintColor.of(buffer.readInt());
                    colors.put(dir, color);
                }
                changes.put(pos, colors);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public UpdateBlockColorPacket(Item item, HashMap<IPaintable, HashMap<Direction, IPaintColor>> changes) {
        this.item = item;
        this.changes = new HashMap<>();
        changes.forEach((target, colors) -> {
            GlobalPos pos = by(target);
            if (pos != null) {
                this.changes.put(pos, colors);
            }
        });
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(Item.getId(item));
        buffer.writeInt(changes.size());
        changes.forEach((pos, colors) -> {
            try {
                buffer.writeWithCodec(GlobalPos.CODEC, pos);
                buffer.writeByte(colors.size());
                colors.forEach((dir, color) -> {
                    buffer.writeEnum(dir);
                    buffer.writeInt(color.getRawValue());
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check player
        MinecraftServer server = player.server;
        changes.forEach((pos, colors) -> {
            World world = server.getLevel(pos.dimension());
            TileEntity tileEntity = null;
            if (world != null) {
                tileEntity = world.getBlockEntity(pos.pos());
            }
            if (tileEntity instanceof IPaintable) {
                ((IPaintable) tileEntity).setColors(colors);
            }
        });
    }

    public GlobalPos by(IPaintable target) {
        if (target instanceof TileEntity) {
            TileEntity tileEntity = (TileEntity) target;
            World world = tileEntity.getLevel();
            if (world != null) {
                return GlobalPos.of(world.dimension(), tileEntity.getBlockPos());
            }
        }
        return null;
    }
}
