package moe.plushie.armourers_workshop.compatibility.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProviderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractEntityRendererImpl<T extends Entity> extends EntityRenderer<T> implements AbstractEntityRendererProviderImpl {

    public AbstractEntityRendererImpl(Context context) {
        super(context);
    }
}
