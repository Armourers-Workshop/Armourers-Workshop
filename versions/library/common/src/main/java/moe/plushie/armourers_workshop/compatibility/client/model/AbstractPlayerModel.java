package moe.plushie.armourers_workshop.compatibility.client.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractPlayerModel<T extends LivingEntity> extends PlayerModel<T> {

    public AbstractPlayerModel(AbstractEntityRendererProvider.Context context, float scale, Type type) {
        super(context.bakeLayer(type.layer), type == Type.SLIM);
    }

    public enum Type {
        NORMAL(ModelLayers.PLAYER),
        SLIM(ModelLayers.PLAYER_SLIM);

        final ModelLayerLocation layer;

        Type(ModelLayerLocation layer) {
            this.layer = layer;
        }
    }
}
