package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractMannequinArmorModel<T extends MannequinEntity> extends HumanoidModel<T> {

    public AbstractMannequinArmorModel(AbstractEntityRendererContext context, Type type) {
        super(context.bakeLayer(type.layer));
    }

    public enum Type {
        INNER(ModelLayers.PLAYER_INNER_ARMOR), OUTER(ModelLayers.PLAYER_OUTER_ARMOR);

        final ModelLayerLocation layer;

        Type(ModelLayerLocation layer) {
            this.layer = layer;
        }
    }
}

