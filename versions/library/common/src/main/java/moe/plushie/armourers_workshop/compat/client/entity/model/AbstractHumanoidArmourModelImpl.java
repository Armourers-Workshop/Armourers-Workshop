package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.18, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractHumanoidArmourModelImpl<T extends LivingEntity> extends HumanoidModel<T> {

    public AbstractHumanoidArmourModelImpl(Context context) {
        super(context.get());
    }

    public interface Context {

        ModelPart get();

        static Context create(EntityRendererProvider.Context context, ModelLayerLocation id) {
            return () -> context.bakeLayer(id);
        }
    }
}
