package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.compat.api.entity.BoatAccessor;

public class BoatRenderState extends EntityRenderState {

    protected Object variant;

    public Object variant() {
        return variant;
    }

    public static void extract(BoatAccessor entity, BoatRenderState renderState) {
        renderState.variant = entity.aw2$variant();
    }
}
