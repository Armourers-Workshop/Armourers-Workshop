package moe.plushie.armourers_workshop.compat.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.HashMap;
import java.util.List;

public class AbstractVertexFormat extends AbstractVertexFormatImpl implements IVertexFormat {

    private static final HashMap<VertexFormat, AbstractVertexFormat> CACHED = new HashMap<>();

    private final VertexFormat impl;
    private final List<AbstractVertexElement> elements;

    private AbstractVertexFormat(VertexFormat impl) {
        this.impl = impl;
        this.elements = Collections.compactMap(impl.getElements(), AbstractVertexElement::wrap);
    }

    public static AbstractVertexFormat wrap(VertexFormat format) {
        return CACHED.computeIfAbsent(format, AbstractVertexFormat::new);
    }

    @Override
    public int byteSize() {
        return impl.getVertexSize();
    }

    @Override
    public List<AbstractVertexElement> elements() {
        return elements;
    }

    @Override
    public VertexFormat get() {
        return impl;
    }
}
