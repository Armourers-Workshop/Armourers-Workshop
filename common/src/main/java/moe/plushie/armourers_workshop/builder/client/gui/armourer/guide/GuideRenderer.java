package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;

public interface GuideRenderer {

    void render(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context);
}
