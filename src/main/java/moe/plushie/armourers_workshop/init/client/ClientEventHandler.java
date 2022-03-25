package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.core.handler.KeyboardHandler;
import moe.plushie.armourers_workshop.core.handler.PlacementHighlightHandler;
import moe.plushie.armourers_workshop.core.handler.PlayerNetworkHandler;
import moe.plushie.armourers_workshop.core.render.skin.*;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;


@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    public static void init(IEventBus eventBus) {
        eventBus.register(new ClientEventHandler());
        eventBus.register(new ItemTooltipHandler());
        eventBus.register(new KeyboardHandler());
        eventBus.register(new PlayerNetworkHandler());
        eventBus.register(new PlacementHighlightHandler());
    }


    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer != null) {
            EntityModel<?> entityModel = event.getRenderer().getModel();
            renderer.willRender(entity, entityModel, event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
        }
    }

    public static void onRenderLiving(LivingRenderer<?, ?> livingRenderer, LivingEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer buffers, int p_225623_6_) {
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer != null) {
            EntityModel<?> entityModel = livingRenderer.getModel();
            renderer.willRenderModel(entity, entityModel, p_225623_6_, p_225623_3_, matrixStack, buffers);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer != null) {
            EntityModel<?> entityModel = event.getRenderer().getModel();
            renderer.didRender(entity, entityModel, event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
        }
    }

//    @SubscribeEvent
//    public void onLivingEquipmentChangeEvent(LivingEvent.LivingUpdateEvent event) {
//        if (event.isCanceled()) {
//            return;
//        }
//        LivingEntity entity = event.getEntityLiving();
//        ClientPlayerEntity player = Minecraft.getInstance().player;
//        if (entity instanceof ServerPlayerEntity && player != null && player.getId() == entity.getId()) {
//            entity = player;
//        }
//        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
//        if (wardrobe != null) {
//            wardrobe.refresh();
//        }
//    }


//    @SubscribeEvent
//    public void onRenderLiving(RenderLivingEvent<LivingEntity, EntityModel<LivingEntity>> event) {
//        if (event.getEntity().getType() != EntityType.ARMOR_STAND) {
//            return;
//        }
////        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        MatrixStack stack = event.getMatrixStack();
//
//        SkinRenderBuffer buffer1 = SkinRenderBuffer.getInstance();
//        SkinItemRenderer.renderSkinAsItem(stack, AWCore.bakery.loadSkin(outfit), event.getLight(), true, false, 32, 32, buffer1);
//        SkinItemRenderer.renderSkinAsItem(stack, AWCore.bakery.loadSkin(sword), event.getLight(), true, false, 32, 32, buffer1);
//        buffer1.endBatch();
//    }

//    @SubscribeEvent
//    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.isCanceled()) {
//            return;
//        }
//        SkinWardrobe wardrobe = SkinWardrobe.of(event.getEntityLiving());
//        if (wardrobe != null) {
//            wardrobe.sync();
//        }
//    }

}
