package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.List;
import java.util.function.Supplier;

public interface IVertexFormat extends Supplier<VertexFormat> {

    default void setupBufferState(long address) {
        var strict = byteSize();
        var elements = elements();
        for (int i = 0; i < elements.size(); ++i) {
            var element = elements.get(i);
            element.setupBufferState(i, strict, address);
            address += element.byteSize();
        }
    }

    default void clearBufferState() {
        var elements = elements();
        for (int i = 0; i < elements.size(); ++i) {
            var element = elements.get(i);
            element.clearBufferState(i);
        }
    }

    int byteSize();

    List<? extends IVertexElement> elements();

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
