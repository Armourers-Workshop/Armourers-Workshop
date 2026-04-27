package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.entity.AbstractHumanoidEntityRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

@Available("[18, 26)")
@OnlyIn(Dist.CLIENT)
public class AbstractHumanoidModelImpl<T extends LivingEntity> extends PlayerModel<T> {

    public AbstractHumanoidModelImpl(Context context) {
        super(context.part(), context.slim());
    }

    public static Context getEmptyContext() {
        var context = AbstractHumanoidEntityRenderer.getEmptyContext();
        return Context.create(context, ModelLayers.PLAYER);
    }

    public interface Context {

        ModelPart part();

        boolean slim();

        static Context create(EntityRendererProvider.Context context, ModelLayerLocation id) {
            return new Context() {
                @Override
                public ModelPart part() {
                    return context.bakeLayer(id);
                }

                @Override
                public boolean slim() {
                    return id == ModelLayers.PLAYER_SLIM;
                }
            };
        }
    }
}
