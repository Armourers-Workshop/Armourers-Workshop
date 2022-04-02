package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ResponseSkinPacket extends CustomPacket {

    private final String identifier;
    private final Mode mode;
    private final Exception exp;
    private final InputStream stream;

    public ResponseSkinPacket(String identifier, InputStream stream, Exception exp) {
        this.identifier = identifier;
        this.exp = exp;
        this.stream = stream;
        this.mode = stream != null ? Mode.STREAM : Mode.EXCEPTION;
    }

    public ResponseSkinPacket(PacketBuffer buffer) {
        this.identifier = buffer.readUtf();
        this.mode = buffer.readEnum(Mode.class);
        this.exp = readException(buffer);
        this.stream = readInputStream(buffer);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(identifier);
        buffer.writeEnum(mode);
        writeException(buffer, exp);
        writeInputStream(buffer, stream);
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        ModLog.debug("receive remote skin data, identifier: '{}'/{}", identifier, exp);
        SkinLoader.getInstance().addSkin(identifier, stream, exp);
    }

    private Exception readException(PacketBuffer buffer) {
        if (mode != Mode.EXCEPTION) {
            return null;
        }
        try {
            ByteBufInputStream bi = new ByteBufInputStream(buffer);
            GZIPInputStream gi = new GZIPInputStream(bi);
            ObjectInputStream oi = new ObjectInputStream(gi);
            Exception exception = (Exception) oi.readObject();
            oi.close();
            return exception;
        } catch (Exception ignored) {
            return null;
        }
    }

    private InputStream readInputStream(PacketBuffer buffer) {
        if (mode != Mode.STREAM) {
            return null;
        }
        try {
            buffer.retain();
            ByteBufInputStream bi = new ByteBufInputStream(buffer, true);
            return new GZIPInputStream(bi);
        } catch (Exception ignored) {
            return null;
        }
    }


    private void writeException(PacketBuffer buffer, Exception exception) {
        if (mode != Mode.EXCEPTION) {
            return;
        }
        try {
            ByteBufOutputStream bo = new ByteBufOutputStream(buffer);
            GZIPOutputStream go = new GZIPOutputStream(bo);
            ObjectOutputStream oo = new ObjectOutputStream(go);
            oo.writeObject(exception);
            oo.close();
        } catch (Exception ignored) {
        }
    }

    private void writeInputStream(PacketBuffer buffer, InputStream inputStream) {
        if (mode != Mode.STREAM) {
            return;
        }
        try {
            ByteBufOutputStream bo = new ByteBufOutputStream(buffer);
            GZIPOutputStream go = new GZIPOutputStream(bo);
            IOUtils.copy(inputStream, go);
            go.close();
        } catch (Exception ignored) {
        }
    }

    public enum Mode {
        EXCEPTION, STREAM
    }
}
