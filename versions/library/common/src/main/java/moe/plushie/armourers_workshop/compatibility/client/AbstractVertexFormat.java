package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.utils.EnumMapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public class AbstractVertexFormat implements IVertexFormat {

    private static final EnumMapper<IVertexFormat.Mode, VertexFormat.Mode> MAPPER = EnumMapper.create(IVertexFormat.Mode.LINES, VertexFormat.Mode.LINES, it -> {
        it.put(IVertexFormat.Mode.LINES, VertexFormat.Mode.LINES);
        it.put(IVertexFormat.Mode.LINE_STRIP, VertexFormat.Mode.LINE_STRIP);
        it.put(IVertexFormat.Mode.DEBUG_LINES, VertexFormat.Mode.DEBUG_LINES);
        it.put(IVertexFormat.Mode.DEBUG_LINE_STRIP, VertexFormat.Mode.DEBUG_LINE_STRIP);
        it.put(IVertexFormat.Mode.TRIANGLES, VertexFormat.Mode.TRIANGLES);
        it.put(IVertexFormat.Mode.TRIANGLE_STRIP, VertexFormat.Mode.TRIANGLE_STRIP);
        it.put(IVertexFormat.Mode.TRIANGLE_FAN, VertexFormat.Mode.TRIANGLE_FAN);
        it.put(IVertexFormat.Mode.QUADS, VertexFormat.Mode.QUADS);
    });

    private final VertexFormat impl;

    private AbstractVertexFormat(VertexFormat impl) {
        this.impl = impl;
    }

    public static IVertexFormat of(VertexFormat format) {
        return new AbstractVertexFormat(format);
    }

    public static IVertexFormat.Mode of(VertexFormat.Mode mode) {
        return MAPPER.getKey(mode);
    }

    @Override
    public void setupBufferState(long offset) {
        impl.setupBufferState(offset);
    }

    @Override
    public void clearBufferState() {
        impl.clearBufferState();
    }

    @Override
    public int vertexSize() {
        return impl.getVertexSize();
    }

    @Override
    public VertexFormat get() {
        return impl;
    }
}
