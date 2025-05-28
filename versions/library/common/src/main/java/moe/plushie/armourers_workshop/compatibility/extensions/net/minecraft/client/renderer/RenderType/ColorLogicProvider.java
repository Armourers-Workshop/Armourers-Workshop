package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.renderer.RenderType;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import net.minecraft.client.renderer.RenderType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.20, )")
@Extension
public class ColorLogicProvider extends RenderType {

    private ColorLogicProvider(RenderType delegate) {
        super(null, delegate.format(), delegate.mode(), delegate.bufferSize(), false, false, null, null);
    }

    @Extension
    public static class CompositeState {

        @Extension
        public static class CompositeStateBuilder {

            public static RenderType.CompositeState.CompositeStateBuilder setColorLogicState(@This RenderType.CompositeState.CompositeStateBuilder builder, IRenderType.ColorLogic state) {
                return switch (state) {
                    case OR_REVERSE -> builder.setColorLogicState(OR_REVERSE_COLOR_LOGIC);
                    default -> builder.setColorLogicState(NO_COLOR_LOGIC);
                };
            }
        }
    }
}
