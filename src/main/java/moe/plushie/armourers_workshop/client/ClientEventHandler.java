package moe.plushie.armourers_workshop.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.OpenWardrobePacket;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.AWKeyBindings;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    static ClientEventHandler handler;

    int mouseX = 0;
    int screenHeight = 0;
    int screenWidth = 0;


    //    @SubscribeEvent
//    public void onPlayerClone(PlayerEvent.Clone event) {
//        PlayerEntity player = event.getPlayer();
//        LazyOptional<SkinWardrobe> newWardrobe = player.getCapability(SkinWardrobeProvider.WARDROBE_KEY, null);
//        LazyOptional<SkinWardrobe> oldWardrobe = event.getOriginal().getCapability(SkinWardrobeProvider.WARDROBE_KEY, null);
////        mana.setMana(oldMana.getMana());
//    }
    public static void init() {
        handler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        handler.addCustomLayers();
    }


    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (event.isCanceled()) {
            return;
        }
        ArrayList<ITextComponent> tooltip = SkinItem.getTooltip(event.getItemStack());
        if (tooltip.size() != 0) {
            event.getToolTip().addAll(tooltip);
        }
    }

    @SubscribeEvent
    public void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        mouseX = event.getX();
        screenWidth = event.getScreenWidth();
        screenHeight = event.getScreenHeight();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderTooltip(RenderTooltipEvent.PostText event) {
        if (event.isCanceled()) {
            return;
        }
        if (!AWConfig.skinPreEnabled) {
            return;
        }
        ItemStack itemStack = event.getStack();
        if (itemStack.getItem() != AWItems.SKIN) {
            return;
        }
        MatrixStack matrixStack = event.getMatrixStack();
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(SkinDescriptor.of(itemStack));
        if (bakedSkin == null) {
            return;
        }

        int x, y;
        int t = (int) System.currentTimeMillis();
        int size = AWConfig.skinPreSize;
        if (AWConfig.skinPreLocFollowMouse) {
            x = event.getX() - 28 - size;
            y = event.getY() - 4;
            if (event.getX() < mouseX) {
                x = event.getX() + event.getWidth() + 28;
            }
            y = MathHelper.clamp(y, 0, screenHeight - size);
        } else {
            x = MathHelper.ceil((screenWidth - size) * AWConfig.skinPreLocHorizontal);
            y = MathHelper.ceil((screenHeight - size) * AWConfig.skinPreLocVertical);
        }

        if (AWConfig.skinPreDrawBackground) {
            GuiUtils.drawContinuousTexturedBox(matrixStack, RenderUtils.TEX_GUI_PREVIEW, x, y, 0, 0, size, size, 62, 62, 4, 400);
        }

        matrixStack.pushPose();
        matrixStack.translate(x + size / 2f, y + size / 2f, 500f);
        matrixStack.scale(-1, 1, 1);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(150));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (float) (t / 10 % 360)));
        SkinItemRenderer.renderSkin(bakedSkin, 0xf000f0, 0, size, size, ItemCameraTransforms.TransformType.NONE, null, matrixStack, null);
        matrixStack.popPose();
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
            BipedModel<?> playerModel = (BipedModel<?>) entityModel;
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
                playerModel.leftArm.visible = false;
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
                playerModel.rightArm.visible = false;
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
                playerModel.head.visible = false;
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
                playerModel.body.visible = false;
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
                playerModel.leftLeg.visible = false;
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
                playerModel.rightLeg.visible = false;
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

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (AWKeyBindings.OPEN_WARDROBE_KEY.consumeClick()) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                NetworkHandler.getInstance().sendToServer(new OpenWardrobePacket(player));
            }
        }
    }

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

    @SubscribeEvent
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.start();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.stop();
            SkinLoader.getInstance().clear();
        }
    }
}
