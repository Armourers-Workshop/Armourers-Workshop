package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ResponseSkinPacket extends CustomPacket {

    private final String identifier;
    private final Mode mode;
    private final boolean compress;
    private final Exception exp;
    private final Skin skin;

    public ResponseSkinPacket(String identifier, Skin skin, Exception exp) {
        this.identifier = identifier;
        this.exp = exp;
        this.skin = skin;
        this.mode = skin != null ? Mode.STREAM : Mode.EXCEPTION;
        this.compress = ModConfig.Common.serverCompressesSkins;
    }

    public ResponseSkinPacket(PacketBuffer buffer) {
        this.identifier = buffer.readUtf();
        this.mode = buffer.readEnum(Mode.class);
        this.compress = buffer.readBoolean();
        this.exp = readException(buffer);
        this.skin = readSkinStream(buffer);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(identifier);
        buffer.writeEnum(mode);
        buffer.writeBoolean(compress);
        writeException(buffer, exp);
        writeSkinStream(buffer, skin);
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        SkinLoader.getInstance().addSkin(identifier, skin, exp);
    }

    private Exception readException(PacketBuffer buffer) {
        if (mode != Mode.EXCEPTION) {
            return null;
        }
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = createInputStream(buffer);
            objectInputStream = new ObjectInputStream(inputStream);
            return (Exception) objectInputStream.readObject();
        } catch (Exception exception) {
            return exception;
        } finally {
            StreamUtils.closeQuietly(objectInputStream, inputStream);
        }
    }

    private void writeException(PacketBuffer buffer, Exception exception) {
        if (mode != Mode.EXCEPTION) {
            return;
        }
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = createOutputStream(buffer);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(exception);
        } catch (Exception exception1) {
            exception1.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(objectOutputStream, outputStream);
        }
    }

    private Skin readSkinStream(PacketBuffer buffer) {
        if (mode != Mode.STREAM) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = createInputStream(buffer);
            return SkinIOUtils.loadSkinFromStream(inputStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(inputStream);
        }
        return null;
    }

    private void writeSkinStream(PacketBuffer buffer, Skin skin) {
        if (mode != Mode.STREAM) {
            return;
        }
        OutputStream outputStream = null;
        try {
            outputStream = createOutputStream(buffer);
            SkinIOUtils.saveSkinToStream(outputStream, skin);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(outputStream);
        }
    }

    private InputStream createInputStream(PacketBuffer buffer) throws Exception {
        InputStream inputStream = new ByteBufInputStream(buffer);
        if (this.compress) {
            return new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    private OutputStream createOutputStream(PacketBuffer buffer) throws Exception {
        ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer);
        if (this.compress) {
            return new GZIPOutputStream(outputStream);
        }
        return outputStream;
    }

    public enum Mode {
        EXCEPTION, STREAM
    }
}
