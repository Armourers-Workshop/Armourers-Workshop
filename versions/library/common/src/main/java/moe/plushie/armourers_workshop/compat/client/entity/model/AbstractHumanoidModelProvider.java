package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import java.util.function.Function;

@Available("[1.18, 1.26)")
@OnlyIn(Dist.CLIENT)
public interface AbstractHumanoidModelProvider<M, A> {

    EntityRendererProvider.Context context();

    M createEntityModel(ModelLayerLocation name);

    A createArmourModel(ModelLayerLocation name);

    static <M, A> AbstractHumanoidModelProvider<M, A> create(EntityRendererProvider.Context context, Function<AbstractHumanoidModel.Context, M> entityModelProvider, Function<AbstractHumanoidArmourModel.Context, A> entityArmourProvider) {
        return new AbstractHumanoidModelProvider<>() {

            @Override
            public EntityRendererProvider.Context context() {
                return context;
            }

            @Override
            public M createEntityModel(ModelLayerLocation id) {
                return entityModelProvider.apply(AbstractHumanoidModel.Context.create(context, id));
            }

            @Override
            public A createArmourModel(ModelLayerLocation id) {
                return entityArmourProvider.apply(AbstractHumanoidArmourModel.Context.create(context, id));
            }
        };
    }
}
