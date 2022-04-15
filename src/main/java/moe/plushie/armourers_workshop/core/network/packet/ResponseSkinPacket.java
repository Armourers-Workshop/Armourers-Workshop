package moe.plushie.armourers_workshop.core.network.packet;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.io.IOUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ResponseSkinPacket extends CustomPacket {

    private final String identifier;
    private final Mode mode;
    private final Exception exp;
    private final Skin skin;

    public ResponseSkinPacket(String identifier, Skin skin, Exception exp) {
        this.identifier = identifier;
        this.exp = exp;
        this.skin = skin;
        this.mode = skin != null ? Mode.STREAM : Mode.EXCEPTION;
    }

    public ResponseSkinPacket(PacketBuffer buffer) {
        this.identifier = buffer.readUtf();
        this.mode = buffer.readEnum(Mode.class);
        this.exp = readException(buffer);
        this.skin = readSkinStream(buffer);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(identifier);
        buffer.writeEnum(mode);
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

    private Skin readSkinStream(PacketBuffer buffer) {
        if (mode != Mode.STREAM) {
            return null;
        }
        ByteBufInputStream byteInputStream = null;
        GZIPInputStream gzipInputStream = null;
        try {
            buffer.retain();
            byteInputStream = new ByteBufInputStream(buffer, true);
            gzipInputStream = new GZIPInputStream(byteInputStream);
            return SkinIOUtils.loadSkinFromStream(gzipInputStream);
        } catch (Exception ignored) {
        } finally {
            IOUtils.closeQuietly(byteInputStream, gzipInputStream);
        }
        return null;
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

    private void writeSkinStream(PacketBuffer buffer, Skin skin) {
        if (mode != Mode.STREAM) {
            return;
        }
        ByteBufOutputStream byteOutputStream = null;
        GZIPOutputStream gzipOutputStream = null;
        try {
            byteOutputStream = new ByteBufOutputStream(buffer);
            gzipOutputStream = new GZIPOutputStream(byteOutputStream);
            SkinIOUtils.saveSkinToStream(gzipOutputStream, skin);
        } catch (Exception ignored) {
        } finally {
            IOUtils.closeQuietly(gzipOutputStream, byteOutputStream);
        }
    }

    public enum Mode {
        EXCEPTION, STREAM
    }
}
