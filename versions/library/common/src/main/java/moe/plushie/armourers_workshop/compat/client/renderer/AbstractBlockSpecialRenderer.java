package moe.plushie.armourers_workshop.compat.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractBlockSpecialRenderer {

    private final String renderType;

    public AbstractBlockSpecialRenderer(String renderType) {
        this.renderType = renderType;
    }

    public String renderType() {
        return renderType;
    }
}

