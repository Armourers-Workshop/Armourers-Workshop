package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface IVertexFormat extends Supplier<VertexFormat> {

    void setupBufferState(long offset);

    void clearBufferState();

    int vertexSize();

    enum Mode {
        LINES,
        LINE_STRIP,
        DEBUG_LINES,
        DEBUG_LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        QUADS,
    }
}
