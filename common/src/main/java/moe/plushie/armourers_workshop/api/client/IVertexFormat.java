package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.function.Supplier;

public interface IVertexFormat extends Supplier<VertexFormat> {

    Mode mode();

    int byteSize();

    enum Mode {
        LINES,
        DEBUG_LINES,
        DEBUG_LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        QUADS,
    }
}
