package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.compat.client.renderer.rendertype.AbstractRenderType;

import java.util.Optional;

public interface IRenderType extends AbstractRenderType {

    String name();

    int ordinal();

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    IVertexFormat format();

    Optional<IRenderType> outline();
}
