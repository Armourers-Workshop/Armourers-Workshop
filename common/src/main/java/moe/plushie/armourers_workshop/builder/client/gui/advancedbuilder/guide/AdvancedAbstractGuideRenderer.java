package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;

public abstract class AdvancedAbstractGuideRenderer {

    public abstract void render(SkinDocument document, int lightmap, int overlay, IGraphicsContext context);
}
