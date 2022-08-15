package moe.plushie.armourers_workshop.core.skin.data.chunk;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface IChunkDataWriter {

    void writeByte(int value);

    void writeShort(int value);

    void writeInt(int value);

    void writeUTF(String value);

    void writeKey(ResourceLocation key);

    void writeColor(IPaintColor color);

    void write(Object chunks, Consumer<IChunkWriter> consumer);
}
