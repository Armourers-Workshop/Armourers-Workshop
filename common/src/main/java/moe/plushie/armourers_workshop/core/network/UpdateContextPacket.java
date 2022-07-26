package moe.plushie.armourers_workshop.core.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.other.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.PreferenceManager;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateContextPacket extends CustomPacket {

    public UpdateContextPacket() {
    }

    public UpdateContextPacket(FriendlyByteBuf buffer) {
        ModContext.init(buffer.readUUID(), buffer.readUUID());
        readConfigSpec(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(Objects.requireNonNull(ModContext.t0()));
        buffer.writeUUID(Objects.requireNonNull(ModContext.t1()));
        writeConfigSpec(buffer);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
    }

    private void writeConfigSpec(FriendlyByteBuf buffer) {
        try {
            Map<String, Object> fields = new HashMap<>();
            if (EnvironmentManager.isDedicatedServer()) {
                fields = ModConfigSpec.COMMON.snapshot();
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

    private void readConfigSpec(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        if (size == 0) {
            return;
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
            ModConfigSpec.COMMON.apply(fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
