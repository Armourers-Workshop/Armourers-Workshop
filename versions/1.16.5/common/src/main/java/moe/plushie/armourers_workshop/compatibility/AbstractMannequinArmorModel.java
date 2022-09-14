package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractMannequinArmorModel<T extends MannequinEntity> extends HumanoidModel<T> {

    public AbstractMannequinArmorModel(AbstractEntityRendererContext context, Type type) {
        super(type.scale, 0.0f, 64, 32);
    }

    public enum Type {
        INNER(0.5f), OUTER(1.0f);

        final float scale;

        Type(float scale) {
            this.scale = scale;
        }
    }

}

