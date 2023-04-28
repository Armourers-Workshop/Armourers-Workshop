package moe.plushie.armourers_workshop.compatibility.client;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public interface AbstractEntityRendererLayerProvider extends AbstractEntityRendererProviderImpl {

    <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> RenderLayer<T, M> createHumanoidArmorLayer(Context context, A innerModel, A outerModel) ;

    <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> RenderLayer<T, M> createItemInHandLayer(Context context);

    <T extends LivingEntity, M extends EntityModel<T>> RenderLayer<T, M> createElytraLayer(Context context);

    <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> RenderLayer<T, M> createCustomHeadLayer(Context context);
}
