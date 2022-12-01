package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public interface AbstractClientFactory {

    static <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> HumanoidArmorLayer<T, M, A> createHumanoidArmorLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context, A innerModel, A outerModel) {
        return new HumanoidArmorLayer<>(renderer, innerModel, outerModel);
    }

    static <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> ItemInHandLayer<T, M> createItemInHandLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context) {
        return new ItemInHandLayer<>(renderer);
    }

    static <T extends LivingEntity, M extends EntityModel<T>> ElytraLayer<T, M> createElytraLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context) {
        return new ElytraLayer<>(renderer, context.getModelSet());
    }

    static <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> CustomHeadLayer<T, M> createCustomHeadLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context) {
        return new CustomHeadLayer<>(renderer, context.getModelSet());
    }

//        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
//    //#if MC >= 11800
//        this.addLayer(new );
//        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
//    //#else
//    //# this.addLayer(new ElytraLayer<>(this));
//    //# this.addLayer(new CustomHeadLayer<>(this));
//    //#endif

}
