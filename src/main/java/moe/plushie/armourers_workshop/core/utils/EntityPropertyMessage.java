package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityPropertyMessage {

    private final int typeId;
    private final int entityId;
    private final CompoundNBT nbt;

    public EntityPropertyMessage(EntityPropertyType<?> type, Entity entity, CompoundNBT nbt) {
        this.typeId = type.getId();
        this.entityId = entity.getId();
        this.nbt = nbt;
    }

    public EntityPropertyMessage(PacketBuffer buffer) {
        this.typeId = buffer.readInt();
        this.entityId = buffer.readInt();
        this.nbt = buffer.readNbt();
    }

    public void write(PacketBuffer buffer) {
        buffer.writeInt(typeId);
        buffer.writeInt(entityId);
        buffer.writeNbt(nbt);
    }

    public void execute(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().level;
            if (world == null) {
                return;
            }
            EntityPropertyType<?> type = EntityPropertyType.byId(typeId);
            Entity entity = world.getEntity(entityId);
            if (type != null) {
                type.apply(entity, nbt);
            }
        });
        context.get().setPacketHandled(true);
    }
}
