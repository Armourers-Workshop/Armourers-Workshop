package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class AbstractRenderLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public AbstractRenderLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }
}
