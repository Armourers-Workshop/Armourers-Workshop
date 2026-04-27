package moe.plushie.armourers_workshop.compat.client.renderer.rendertype;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Supplier;

@Available("[16, 26)")
public interface AbstractRenderType extends Supplier<RenderType> {

    static SkinRenderType wrap(RenderType renderType) {
        return DataContainer.of(renderType, AbstractRenderTypeImpl::create);
    }

    static SkinRenderType.Builder builder(SkinVertexFormat format) {
        return AbstractRenderTypeImpl.builder(format);
    }
}
