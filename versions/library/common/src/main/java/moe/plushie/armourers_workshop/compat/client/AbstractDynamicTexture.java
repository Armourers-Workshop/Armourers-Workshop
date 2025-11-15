package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.renderer.texture.DynamicTexture;

@Available("[1.16, 1.22)")
public abstract class AbstractDynamicTexture extends DynamicTexture {

    private final String name;

    public AbstractDynamicTexture(String name, int width, int height, boolean bl) {
        super(width, height, bl);
        this.name = name;
    }

    public String name() {
        return name;
    }
}
