package moe.plushie.armourers_workshop.compat.client.renderer.rendertype;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Supplier;

@Available("[16, 26)")
public interface AbstractRenderType extends Supplier<RenderType> {

    static SkinRenderType wrap(RenderType renderType) {
        return AbstractRenderTypeBuilder.create(renderType);
    }
}
