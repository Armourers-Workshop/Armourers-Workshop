package moe.plushie.armourers_workshop.compat.client.renderer.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.utils.FastMapper;

@Available("[18, )")
public abstract class AbstractVertexFormatImpl implements IVertexFormat {

    protected static final FastMapper<Mode, VertexFormat.Mode> MODE_MAPPER = FastMapper.builder(Mode.LINES, VertexFormat.Mode.LINES, it -> {
        it.put(Mode.LINES, VertexFormat.Mode.LINES);
        it.put(Mode.DEBUG_LINES, VertexFormat.Mode.DEBUG_LINES);
        it.put(Mode.DEBUG_LINE_STRIP, VertexFormat.Mode.DEBUG_LINE_STRIP);
        it.put(Mode.TRIANGLES, VertexFormat.Mode.TRIANGLES);
        it.put(Mode.TRIANGLE_STRIP, VertexFormat.Mode.TRIANGLE_STRIP);
        it.put(Mode.TRIANGLE_FAN, VertexFormat.Mode.TRIANGLE_FAN);
        it.put(Mode.QUADS, VertexFormat.Mode.QUADS);
    });

    public static AbstractVertexFormat create(VertexFormat format, VertexFormat.Mode mode) {
        return new AbstractVertexFormat(format, MODE_MAPPER.getKey(mode));
    }
}
