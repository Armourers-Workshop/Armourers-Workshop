package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModConfigSpec;
import moe.plushie.armourers_workshop.init.common.ModContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateContextPacket extends CustomPacket {

    public UpdateContextPacket() {
    }

    public UpdateContextPacket(PacketBuffer buffer) {
        ModContext.init(buffer.readUUID(), buffer.readUUID());
        ModConfigSpec.reloadSpec(readConfig(buffer));
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(Objects.requireNonNull(ModContext.t0()));
        buffer.writeUUID(Objects.requireNonNull(ModContext.t1()));
        writeConfigSpec(buffer);
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
    }

    private void writeConfigSpec(PacketBuffer buffer) {
        try {
            Map<String, Object> fields = new HashMap<>();
            if (FMLEnvironment.dist.isDedicatedServer()) {
                fields = ModConfigSpec.Common.Serializer.snapshot();
            }
            buffer.writeInt(fields.size());
            if (fields.size() == 0) {
                return;
            }
            ByteBufOutputStream bo = new ByteBufOutputStream(buffer);
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                oo.writeUTF(entry.getKey());
                oo.writeObject(entry.getValue());
            }
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> readConfig(PacketBuffer buffer) {
        int size = buffer.readInt();
        if (size == 0) {
            return null;
        }
        try {
            HashMap<String, Object> fields = new HashMap<>();
            ByteBufInputStream bi = new ByteBufInputStream(buffer);
            ObjectInputStream oi = new ObjectInputStream(bi);
            for (int i = 0; i < size; ++i) {
                String name = oi.readUTF();
                Object value = oi.readObject();
                fields.put(name, value);
            }
            oi.close();
            return fields;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
