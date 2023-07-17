package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class MobLayerFixPlugin<T extends LivingEntity, M extends IHumanoidModel> extends ForwardingLayerPlugin<T, M> {

    public MobLayerFixPlugin() {
        register(StrayClothingLayer.class, this::forwardingWhenBodyVisible);
        register(DrownedOuterLayer.class, this::forwardingWhenBodyVisible);
    }

    private boolean forwardingWhenBodyVisible(T entity, M model) {
        return model.getBodyPart().isVisible();
    }
}
