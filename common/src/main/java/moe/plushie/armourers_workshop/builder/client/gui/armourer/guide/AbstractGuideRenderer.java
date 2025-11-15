package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGuideRenderer {

    public abstract void init(GuideRendererManager rendererManager);
}
