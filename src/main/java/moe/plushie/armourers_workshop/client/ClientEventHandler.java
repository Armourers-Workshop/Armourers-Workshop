package moe.plushie.armourers_workshop.client;

import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.core.handler.KeyboardHandler;
import moe.plushie.armourers_workshop.core.handler.PlayerNetworkHandler;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
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
        if (entityModel instanceof BipedModel) {
            BipedModel<?> bipedModel = (BipedModel<?>) entityModel;
            PlayerModel<?> playerModel = null;
            if (entityModel instanceof PlayerModel) {
                playerModel = (PlayerModel<?>) entityModel;
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
                bipedModel.leftArm.visible = false;
                if (playerModel != null) {
                    playerModel.leftSleeve.visible = false;
                }
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
                bipedModel.rightArm.visible = false;
                if (playerModel != null) {
                    playerModel.rightSleeve.visible = false;
                }
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
                bipedModel.head.visible = false;
                bipedModel.hat.visible = false; // when override the head, the hat needs to override too
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
                bipedModel.body.visible = false;
                if (playerModel != null) {
                    playerModel.jacket.visible = false;
                }
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
                bipedModel.leftLeg.visible = false;
                if (playerModel != null) {
                    playerModel.leftPants.visible = false;
                }
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
                bipedModel.rightLeg.visible = false;
                if (playerModel != null) {
                    playerModel.rightPants.visible = false;
                }
            }
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


    private void addCustomLayers() {
        // Add our own custom armor layer to the various player renderers.
        EntityRendererManager entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        for (Map.Entry<String, PlayerRenderer> entry : entityRenderManager.getSkinMap().entrySet()) {
            addCustomLayers(EntityType.PLAYER, entry.getValue());
        }
//        //Add our own custom armor layer to everything that has an armor layer
//        //Note: This includes any modded mobs that have vanilla's BipedArmorLayer added to them
//        for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : entityRenderManager.renderers.entrySet()) {
//            EntityRenderer<?> renderer = entry.getValue();
//            if (renderer instanceof LivingRenderer) {
//                LivingRenderer livingRenderer = (LivingRenderer) renderer;
//                if (livingRenderer.getModel() instanceof BipedModel || livingRenderer.getModel() instanceof VillagerModel) {
//                    addCustomLayers(entry.getKey(), livingRenderer);
//                }
//            }
//        }
    }

    // this.minecraft.options.getCameraType().isFirstPerson()
    private <T extends LivingEntity, M extends BipedModel<T>> void addCustomLayers(EntityType<?> type, LivingRenderer<T, M> renderer) {
//        BipedArmorLayer<T, M, ?> bipedArmorLayer = null;
//        ElytraLayer<T, M> elytraLayer = null;
////        HeldItemLayer<T, M> heldItemLayer = null;
//        for (LayerRenderer<T, M> layerRenderer : renderer.layers) {
//            // Validate against the layer render being null, as it seems like some mods do stupid things and add in null layers
//            if (layerRenderer == null) {
//                continue;
//            }
//            // Only allow an exact class match, so we don't add to modded entities that only have a modded extended armor or elytra layer
//            Class<?> layerClass = layerRenderer.getClass();
//            if (layerClass == BipedArmorLayer.class) {
//                bipedArmorLayer = (BipedArmorLayer<T, M, ?>) layerRenderer;
//            } else if (layerClass == ElytraLayer.class) {
//                elytraLayer = (ElytraLayer<T, M>) layerRenderer;
////            } else if (layerClass == HeldItemLayer.class) {
////                heldItemLayer = (HeldItemLayer<T, M>) layerRenderer;
//            }
//        }
//        if (bipedArmorLayer != null) {
////            renderer.layers.remove(bipedArmorLayer);
////            SkinLog.info("Added Custom Armor Layer to entity of type: {}", type.getRegistryName());
//        }

        SkinWardrobeArmorLayer<T, M> player = new SkinWardrobeArmorLayer<>(renderer);
        renderer.addLayer(player);

//        SkinHeldItemLayer<T, M> heldItem = new SkinHeldItemLayer<>(renderer);
//        heldItem.bakedSkin = sword;
//        renderer.addLayer(heldItem);

//        if (elytraLayer != null) {
//            renderer.addLayer(new ExampleElytraLayer(renderer));
//            LOGGER.debug("Added Custom Elytra Layer to entity of type: {}", type.getRegistryName());
//        }
    }

}
