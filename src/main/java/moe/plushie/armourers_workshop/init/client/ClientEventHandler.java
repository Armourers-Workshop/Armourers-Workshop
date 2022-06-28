package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.core.handler.KeyboardHandler;
import moe.plushie.armourers_workshop.core.handler.PlacementHighlightHandler;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
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
        eventBus.register(new PlacementHighlightHandler());
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        EntityModel<?> entityModel = event.getRenderer().getModel();
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity, entityModel, event.getRenderer());
        if (renderer != null) {
            renderer.willRender(entity, entityModel, event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
        }
    }

    public static void onRenderLiving(LivingRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer, LivingEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer buffers, int p_225623_6_) {
        EntityModel<?> entityModel = livingRenderer.getModel();
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity, entityModel, livingRenderer);
        if (renderer != null) {
            renderer.willRenderModel(entity, entityModel, p_225623_6_, p_225623_3_, matrixStack, buffers);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        EntityModel<?> entityModel = event.getRenderer().getModel();
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity, entityModel, event.getRenderer());
        if (renderer != null) {
            renderer.didRender(entity, entityModel, event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
        }
    }
}
