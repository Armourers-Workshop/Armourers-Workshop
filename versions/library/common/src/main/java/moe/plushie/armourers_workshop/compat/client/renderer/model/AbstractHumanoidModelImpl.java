package moe.plushie.armourers_workshop.compat.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractHumanoidEntityRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.21, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractHumanoidModelImpl<T extends LivingEntity> extends PlayerModel<T> {

    public AbstractHumanoidModelImpl(Context context) {
        super(context.part(), context.slim());
    }

    public static Context getEmptyContext() {
        var context = AbstractHumanoidEntityRenderer.getEmptyContext();
        return Context.create(context, ModelLayers.PLAYER);
    }

    protected void translateAndRotate(PoseStack poseStack) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer builder, int light, int overlay, int color) {
        translateAndRotate(poseStack);
        super.renderToBuffer(poseStack, builder, light, overlay, color);
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
