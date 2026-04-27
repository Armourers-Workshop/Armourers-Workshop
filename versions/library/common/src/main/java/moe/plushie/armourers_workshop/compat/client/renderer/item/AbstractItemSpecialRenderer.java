package moe.plushie.armourers_workshop.compat.client.renderer.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractSpecialModelRenderer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractItemSpecialRenderer extends AbstractItemSpecialRendererImpl {

    private final OpenResourceKey base;
    private final AbstractSpecialModelRenderer<?> renderer;

    public AbstractItemSpecialRenderer(OpenResourceKey base, AbstractSpecialModelRenderer<?> renderer) {
        this.base = base;
        this.renderer = renderer;
    }

    public OpenResourceKey base() {
        return base;
    }

    @Override
    public AbstractSpecialModelRenderer<?> renderer() {
        return renderer;
    }
}


