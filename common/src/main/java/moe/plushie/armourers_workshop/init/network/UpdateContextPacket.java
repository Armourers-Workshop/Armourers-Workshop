package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.data.TickTracker;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.world.entity.player.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateContextPacket extends CustomPacket {

    private UUID token = null;
    private ByteBuf buffer = null;

    public UpdateContextPacket() {
    }

    public UpdateContextPacket(Player player) {
        this.token = player.getUUID();
    }

    public UpdateContextPacket(IFriendlyByteBuf buffer) {
        this.buffer = buffer.asByteBuf();
        this.buffer.retain();
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        if (token != null) {
            buffer.writeBoolean(true);
            buffer.writeUUID(ModContext.t2(token));
            buffer.writeUUID(ModContext.t3(token));
            buffer.writeFloat(TickTracker.server().animationTicks());
            buffer.writeUtf(ModConstants.MOD_NET_ID);
        } else {
            buffer.writeBoolean(false);
        }
        writeConfigSpec(buffer);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        if (buffer != null) {
            IFriendlyByteBuf reader = IFriendlyByteBuf.wrap(buffer);
            if (buffer.readBoolean()) {
                ModContext.init(reader.readUUID(), reader.readUUID());
                TickTracker.client().setAnimationTicks(reader.readFloat());
                checkNetworkVersion(reader.readUtf());
            }
            readConfigSpec(reader);
            buffer.release();
        }
    }

    private void writeConfigSpec(IFriendlyByteBuf buffer) {
        try {
            Map<String, Object> fields = new HashMap<>();
            if (EnvironmentManager.isDedicatedServer()) {
                fields = ModConfigSpec.COMMON.snapshot();
            }
            buffer.writeInt(fields.size());
            if (fields.isEmpty()) {
                return;
            }
            ByteBufOutputStream bo = new ByteBufOutputStream(buffer.asByteBuf());
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            for (var entry : fields.entrySet()) {
                oo.writeUTF(entry.getKey());
                oo.writeObject(entry.getValue());
            }
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readConfigSpec(IFriendlyByteBuf buffer) {
        try {
            int size = buffer.readInt();
            if (size == 0) {
                return;
            }
            HashMap<String, Object> fields = new HashMap<>();
            ByteBufInputStream bi = new ByteBufInputStream(buffer.asByteBuf());
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

    private void checkNetworkVersion(String version) {
        if (!version.equals(ModConstants.MOD_NET_ID)) {
            ModLog.warn("network protocol conflict, server: {}, client: {}", version, ModConstants.MOD_NET_ID);
        }
    }
}
