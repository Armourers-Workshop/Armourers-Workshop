package moe.plushie.armourers_workshop.compatibility.client.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractMannequinArmorModel<T extends MannequinEntity> extends HumanoidModel<T> {

    public AbstractMannequinArmorModel(AbstractEntityRendererProvider.Context context, Type type) {
        super(context.bakeLayer(type.layer));
    }

    public enum Type {
        NORMAL_INNER(ModelLayers.PLAYER_INNER_ARMOR),
        NORMAL_OUTER(ModelLayers.PLAYER_OUTER_ARMOR),
        SLIM_INNER(ModelLayers.PLAYER_SLIM_INNER_ARMOR),
        SLIM_OUTER(ModelLayers.PLAYER_SLIM_OUTER_ARMOR);

        final ModelLayerLocation layer;

        Type(ModelLayerLocation layer) {
            this.layer = layer;
        }
    }
}

