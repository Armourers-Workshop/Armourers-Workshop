package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    protected AbstractEntityRenderer(AbstractEntityRendererContext context) {
        super(context);
    }
}
