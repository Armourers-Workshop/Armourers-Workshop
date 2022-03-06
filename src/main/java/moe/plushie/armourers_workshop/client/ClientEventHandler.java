package moe.plushie.armourers_workshop.client;

import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.core.handler.KeyboardHandler;
import moe.plushie.armourers_workshop.core.handler.PlacementHighlightHandler;
import moe.plushie.armourers_workshop.core.handler.PlayerNetworkHandler;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import moe.plushie.armourers_workshop.core.render.skin.*;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;


@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    static ClientEventHandler handler;

    //    @SubscribeEvent
//    public void onPlayerClone(PlayerEvent.Clone event) {
//        PlayerEntity player = event.getPlayer();
//        LazyOptional<SkinWardrobe> newWardrobe = player.getCapability(SkinWardrobeProvider.WARDROBE_KEY, null);
//        LazyOptional<SkinWardrobe> oldWardrobe = event.getOriginal().getCapability(SkinWardrobeProvider.WARDROBE_KEY, null);
////        mana.setMana(oldMana.getMana());
//    }
    public static void init(IEventBus eventBus) {
        handler = new ClientEventHandler();
        eventBus.register(handler);
        eventBus.register(new ItemTooltipHandler());
        eventBus.register(new KeyboardHandler());
        eventBus.register(new PlayerNetworkHandler());
        eventBus.register(new PlacementHighlightHandler());
        handler.addCustomLayers();
    }


    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        SkinWardrobeState snapshot = wardrobe.snapshot();
        if (snapshot.hasPart(SkinPartTypes.BIPED_SKIRT)) {
            if (entity.animationSpeed > 0.25F) {
                entity.animationSpeed = 0.25F;
                entity.animationSpeedOld = 0.25F;
            }
        }
        if (!AWConfig.enableModelOverridden) {
            return;
        }
        EntityModel<?> entityModel = event.getRenderer().getModel();
        SkinRenderer<LivingEntity, EntityModel<?>> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer == null) {
            return;
        }
        renderer.override(entity, entityModel, wardrobe);
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


    private void addCustomLayers() {
        SkinRendererManager rendererManager = SkinRendererManager.getInstance();
        rendererManager.register(EntityProfile.PLAYER, LivingSkinRenderer::new);
        rendererManager.register(EntityProfile.ARROW, ArrowSkinRenderer::new);
        rendererManager.register(EntityProfile.VILLAGER, VillagerSkinRenderer::new);
        rendererManager.register(EntityProfile.SKELETON, LivingSkinRenderer::new);
        rendererManager.register(EntityProfile.ZOMBIE, LivingSkinRenderer::new);
//        rendererManager.register(EntityProfile.SLIME, LivingSkinRenderer::new);

        rendererManager.register(EntityProfile.MANNEQUIN, LivingSkinRenderer::new);
    }
}
