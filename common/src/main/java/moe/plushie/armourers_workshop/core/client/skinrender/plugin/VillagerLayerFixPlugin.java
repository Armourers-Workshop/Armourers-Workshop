package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class VillagerLayerFixPlugin<T extends LivingEntity, M extends IHumanoidModel> extends ForwardingLayerPlugin<T, M> {

    public VillagerLayerFixPlugin() {
        register(VillagerProfessionLayer.class, this::forwardingWhenHatVisible);
    }

    private boolean forwardingWhenHatVisible(T entity, M model) {
        return model.getHatPart().isVisible();
    }
}
