package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class SlimeOuterFixPlugin<T extends LivingEntity, V extends EntityModel<T>, M extends IModelHolder<V>> extends ForwardingLayerPlugin<T, V, M> {

    public SlimeOuterFixPlugin() {
        register(SlimeOuterLayer.class, this::forwardingWhenAnyVisible);
    }

    private boolean forwardingWhenAnyVisible(T entity, M model) {
        for (ModelPart part : model.getAllParts()) {
            return part.visible;
        }
        return true;
    }
}
