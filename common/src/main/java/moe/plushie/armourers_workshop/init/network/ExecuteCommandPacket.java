package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class ExecuteCommandPacket extends CustomPacket {

    private final Class<?> object;
    private final Mode mode;
    private final String key;
    private final Object value;

    public ExecuteCommandPacket(Class<?> object, String key, Object value, Mode mode) {
        this.object = object;
        this.mode = mode;
        this.key = key;
        this.value = value;
    }

    public ExecuteCommandPacket(FriendlyByteBuf buffer) {
        this.object = readClass(buffer);
        this.mode = buffer.readEnum(Mode.class);
        this.key = buffer.readUtf(Short.MAX_VALUE);
        this.value = readObject(buffer);
    }


    public static ExecuteCommandPacket set(Class<?> obj, String key, Object value) {
        return new ExecuteCommandPacket(obj, key, value, Mode.SET);
    }

    public static ExecuteCommandPacket get(Class<?> obj, String key) {
        return new ExecuteCommandPacket(obj, key, null, Mode.GET);
    }


    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(object.getName());
        buffer.writeEnum(mode);
        buffer.writeUtf(key);
        writeObject(buffer, value);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        try {
            Object data = value;
            switch (mode) {
                case GET: {
                    Field field = object.getField(key);
                    data = field.get(object);
                    break;
                }
                case SET: {
                    Field field = object.getField(key);
                    field.set(object, data);
                    break;
                }
            }
            player.sendSystemMessage(Component.literal(key + " = " + data));
            // auto-save when change
            if (ModConfig.Client.class == object) {
                ModConfigSpec.CLIENT.save();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Object readObject(FriendlyByteBuf buffer) {
        if (mode != Mode.SET) {
            return null;
        }
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = new ByteBufInputStream(buffer);
            objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (Exception exception) {
            return exception;
        } finally {
            StreamUtils.closeQuietly(objectInputStream, inputStream);
        }
    }

    private void writeObject(FriendlyByteBuf buffer, Object object) {
        if (mode != Mode.SET) {
            return;
        }
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = new ByteBufOutputStream(buffer);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
        } catch (Exception exception1) {
            exception1.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(objectOutputStream, outputStream);
        }
    }

    private Class<?> readClass(FriendlyByteBuf buffer) {
        try {
            return Class.forName(buffer.readUtf(Short.MAX_VALUE));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public enum Mode {
        SET, GET
    }
}
