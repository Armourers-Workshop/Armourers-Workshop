package moe.plushie.armourers_workshop.compat.client.renderer.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractItemSpecialRenderer extends AbstractItemSpecialRendererImpl {

    private final OpenResourceKey base;
    private final SpecialModelRenderer<?> renderer;

    public AbstractItemSpecialRenderer(OpenResourceKey base, SpecialModelRenderer<?> renderer) {
        this.base = base;
        this.renderer = renderer;
    }

    public OpenResourceKey base() {
        return base;
    }

    @Override
    public SpecialModelRenderer<?> renderer() {
        return renderer;
    }
}


