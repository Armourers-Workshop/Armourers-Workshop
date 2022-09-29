package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class VillagerLayerFixPlugin<T extends LivingEntity, V extends EntityModel<T>, M extends IHumanoidModelHolder<V>> extends ForwardingLayerPlugin<T, V, M> {

    public VillagerLayerFixPlugin() {
        register(VillagerProfessionLayer.class, this::forwardingWhenHatVisible);
    }

    private boolean forwardingWhenHatVisible(T entity, M model) {
        return model.getHatPart().visible;
    }
}
