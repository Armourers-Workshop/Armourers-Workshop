package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class ExecuteCommandPacket extends CustomPacket {

    private final Mode mode;
    private final String key;
    private final Object value;


    public ExecuteCommandPacket(Mode mode, String key, Object value) {
        this.mode = mode;
        this.key = key;
        this.value = value;
    }

    public ExecuteCommandPacket(PacketBuffer buffer) {
        this.mode = buffer.readEnum(Mode.class);
        this.key = buffer.readUtf();
        this.value = readObject(buffer);
    }


    public static ExecuteCommandPacket set(String key, Object value) {
        return new ExecuteCommandPacket(Mode.SET, key, value);
    }

    public static ExecuteCommandPacket get(String key) {
        return new ExecuteCommandPacket(Mode.GET, key, null);
    }


    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEnum(mode);
        buffer.writeUtf(key);
        writeObject(buffer, value);
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        try {
            Class<?> object = ModConfig.Client.class;
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
            player.sendMessage(new StringTextComponent(key + " = " + data), player.getUUID());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Object readObject(PacketBuffer buffer) {
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

    private void writeObject(PacketBuffer buffer, Object object) {
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

    public enum Mode {
        SET, GET
    }
}
