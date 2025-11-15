package moe.plushie.armourers_workshop.core.client.render.state;

import net.minecraft.world.entity.vehicle.Boat;

public class BoatRenderState extends EntityRenderState {

    protected Object variant;

    public Object variant() {
        return variant;
    }

    public static void extract(Boat entity, BoatRenderState renderState) {
        renderState.variant = entity.variant();
    }
}
