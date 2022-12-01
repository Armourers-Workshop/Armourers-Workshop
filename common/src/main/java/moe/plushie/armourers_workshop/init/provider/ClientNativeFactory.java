package moe.plushie.armourers_workshop.init.provider;

import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
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
public interface ClientNativeFactory {

    <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> HumanoidArmorLayer<T, M, A> createHumanoidArmorLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context, A innerModel, A outerModel) ;

    <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> ItemInHandLayer<T, M> createItemInHandLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context);

    <T extends LivingEntity, M extends EntityModel<T>> ElytraLayer<T, M> createElytraLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context);

    <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> CustomHeadLayer<T, M> createCustomHeadLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context);

    IBufferBuilder createBuilderBuffer(int size);

    IResourceManager getResourceManager();
}
