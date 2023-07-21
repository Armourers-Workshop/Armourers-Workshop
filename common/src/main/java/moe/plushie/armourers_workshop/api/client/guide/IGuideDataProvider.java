package moe.plushie.armourers_workshop.api.client.guide;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;

public interface IGuideDataProvider {

    boolean shouldRenderOverlay(ISkinProperty<Boolean> property);
}
