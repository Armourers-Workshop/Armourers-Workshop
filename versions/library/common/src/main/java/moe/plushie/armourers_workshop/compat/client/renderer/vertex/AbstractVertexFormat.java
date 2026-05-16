package moe.plushie.armourers_workshop.compat.client.renderer.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.List;

@Available("[16, )")
public class AbstractVertexFormat extends AbstractVertexFormatImpl {

    private final VertexFormat format;
    private final Mode mode;

    public AbstractVertexFormat(VertexFormat format, Mode mode) {
        this.format = format;
        this.mode = mode;
    }

    @Override
    public Mode mode() {
        return mode;
    }

    @Override
    public int byteSize() {
        return format.getVertexSize();
    }

    @Override
    public VertexFormat get() {
        return format;
    }
}
