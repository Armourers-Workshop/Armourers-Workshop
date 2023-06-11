package extensions.net.minecraft.client.renderer.RenderType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import net.minecraft.client.renderer.RenderType;

@Available("[1.20, )")
@Extension
public class ColorLogicProvider extends RenderType {

    private ColorLogicProvider(RenderType delegate) {
        super(null, delegate.format(), delegate.mode(), delegate.bufferSize(), false, false, null, null);
    }

    public static class CompositeState {

        @Extension
        public static class CompositeStateBuilder  {

            public static RenderType.CompositeState.CompositeStateBuilder setColorLogicState(@This RenderType.CompositeState.CompositeStateBuilder builder, IRenderTypeBuilder.ColorLogic state) {
                switch (state) {
                    case OR_REVERSE:
                        return builder.setColorLogicState(OR_REVERSE_COLOR_LOGIC);

                    default:
                        return builder.setColorLogicState(NO_COLOR_LOGIC);
                }
            }

        }
    }
}
