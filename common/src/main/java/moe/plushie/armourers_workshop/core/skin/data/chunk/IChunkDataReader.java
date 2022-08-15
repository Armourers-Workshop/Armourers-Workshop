package moe.plushie.armourers_workshop.core.skin.data.chunk;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface IChunkDataReader {

    int readByte();

    int readShort();

    int readInt();

    String readUTF();

    ResourceLocation readKey();

    IPaintColor readColor();

    Object read(Consumer<IChunkReader> consumer);

    boolean readable();

    void skip(int count);
}
