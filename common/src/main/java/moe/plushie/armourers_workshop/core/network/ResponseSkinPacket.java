package moe.plushie.armourers_workshop.core.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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
        this.compress = ModConfig.Common.enableServerCompressesSkins;
    }

    public ResponseSkinPacket(FriendlyByteBuf buffer) {
        this.identifier = buffer.readUtf(Short.MAX_VALUE);
        this.mode = buffer.readEnum(Mode.class);
        this.compress = buffer.readBoolean();
        this.exp = readException(buffer);
        this.skin = readSkinStream(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(identifier);
        buffer.writeEnum(mode);
        buffer.writeBoolean(compress);
        writeException(buffer, exp);
        writeSkinStream(buffer, skin);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        SkinLoader.getInstance().addSkin(identifier, skin, exp);
    }

    private Exception readException(FriendlyByteBuf buffer) {
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

    private void writeException(FriendlyByteBuf buffer, Exception exception) {
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

    private Skin readSkinStream(FriendlyByteBuf buffer) {
        if (mode != Mode.STREAM) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = createInputStream(buffer);
            return SkinFileStreamUtils.loadSkinFromStream(inputStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(inputStream);
        }
        return null;
    }

    private void writeSkinStream(FriendlyByteBuf buffer, Skin skin) {
        if (mode != Mode.STREAM) {
            return;
        }
        OutputStream outputStream = null;
        try {
            outputStream = createOutputStream(buffer);
            SkinFileStreamUtils.saveSkinToStream(outputStream, skin);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(outputStream);
        }
    }

    private InputStream createInputStream(FriendlyByteBuf buffer) throws Exception {
        InputStream inputStream = new ByteBufInputStream(buffer);
        if (this.compress) {
            return new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    private OutputStream createOutputStream(FriendlyByteBuf buffer) throws Exception {
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
