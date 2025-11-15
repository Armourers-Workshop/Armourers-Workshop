package moe.plushie.armourers_workshop.compat.client;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import moe.plushie.armourers_workshop.api.client.IVertexElement;

import java.util.HashMap;

public class AbstractVertexElement extends AbstractVertexElementImpl implements IVertexElement {

    private static final HashMap<VertexFormatElement, AbstractVertexElement> CACHED = new HashMap<>();

    private final VertexFormatElement impl;

    private AbstractVertexElement(VertexFormatElement impl) {
        this.impl = impl;
    }

    public static AbstractVertexElement wrap(VertexFormatElement element) {
        return CACHED.computeIfAbsent(element, AbstractVertexElement::new);
    }

    @Override
    public VertexFormatElement get() {
        return impl;
    }
}
