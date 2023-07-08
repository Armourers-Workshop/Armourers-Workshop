package extensions.net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.platform.GlStateManager;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.RenderType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.20)")
@Extension
public class ColorLogicProvider extends RenderType {

    private static final TexturingStateShard OR_REVERSE_COLOR_LOGIC = new TexturingStateShard("aw_or_reverse", () -> {
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
    }, () -> {
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    });

    private ColorLogicProvider(RenderType delegate) {
        super(null, delegate.format(), delegate.mode(), delegate.bufferSize(), false, false, null, null);
    }

    @Extension
    public static class CompositeState {

        @Extension
        public static class CompositeStateBuilder  {

            public static RenderType.CompositeState.CompositeStateBuilder setColorLogicState(@This RenderType.CompositeState.CompositeStateBuilder builder, IRenderTypeBuilder.ColorLogic state) {
                switch (state) {
                    case OR_REVERSE:
                        return builder.setTexturingState(OR_REVERSE_COLOR_LOGIC);

                    default:
                        return builder.setTexturingState(DEFAULT_TEXTURING);
                }
            }

        }
    }
}
