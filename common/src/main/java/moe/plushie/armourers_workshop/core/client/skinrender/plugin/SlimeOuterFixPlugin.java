package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class SlimeOuterFixPlugin<T extends LivingEntity, M extends IModel> extends ForwardingLayerPlugin<T, M> {

    public SlimeOuterFixPlugin() {
        register(SlimeOuterLayer.class, this::forwardingWhenAnyVisible);
    }

    private boolean forwardingWhenAnyVisible(T entity, M model) {
        for (IModelPart part : model.getAllParts()) {
            return part.isVisible();
        }
        return true;
    }
}
