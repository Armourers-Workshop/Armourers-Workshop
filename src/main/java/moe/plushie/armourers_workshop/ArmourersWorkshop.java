package moe.plushie.armourers_workshop;


import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.client.layer.WardrobeArmorLayer;
import moe.plushie.armourers_workshop.common.ArmourersConfig;
import moe.plushie.armourers_workshop.common.SkinCommands;
import moe.plushie.armourers_workshop.common.item.SkinItem;
import moe.plushie.armourers_workshop.common.item.SkinItems;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.model.bake.ModelBakery;
import moe.plushie.armourers_workshop.core.render.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.render.renderer.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.core.utils.SkinPacketHandler;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeProvider;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

@Mod("armourers_workshop")
public class ArmourersWorkshop {
    public static BakedSkin outfit;
    public static BakedSkin sword;
    public static BakedSkin outfit2;
    public static BakedSkin sword2;
    public static ArrayList<BakedSkin> skins = new ArrayList<>();
    static float sx = 1.0f;
    PlayerRenderer playerRenderer;
    int mouseX = 0;
    int screenHeight = 0;
    int screenWidth = 0;
    private EntityRendererManager entityRenderManager;

//    @SubscribeEvent
//    void renderWorldLastEvent(RenderWorldLastEvent event) {
//        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
////        IVertexBuilder builder = buffer.getBuffer(SkinPartRenderer.SOLID);
//        MatrixStack matrixStack = event.getMatrixStack();
//        ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
////    Matrix4f projectedView = event.getProjectionMatrix();
////        event.getProjectionMatrix();
////        stack.pushPose();
////        stack.scale(16f, 16f, 16f);
////        playerRenderer.render(playerEntity, 0, 0, stack, buffer, 0);
////        stack.popPose();
//
//        Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//
//        matrixStack.pushPose();
//
//        matrixStack.translate(-cam.x, -cam.y + 32, -cam.z);
//
//        SkinItemRenderer.renderSkinAsItem(matrixStack, buffer, bakedSkin, true, false, 32, 32);
//        matrixStack.popPose();
//    }

    public ArmourersWorkshop() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

        ArmourersConfig.init();
        ClientWardrobeHandler.init();
        SkinPacketHandler.init();

        SkinItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

//        CapabilityManager

//        CapabilityManager.INSTANCE.register(IWardrobeCap.class, new WardrobeStorage(), new Callable<IWardrobeCap>() {
//
//            @Override
//            public IWardrobeCap call() throws Exception {
//                return null;
//            }
//        });
//        Skin skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Projects/Minecraft/Armourers-Workshop/web/list/Witch's Skirt.armour"));

//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Projects/Minecraft/Armourers-Workshop/web/list/Witch's Skirt.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/胡桃/胡桃.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/钟离/钟离.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/胡桃/护摩之杖.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/套装（完全）.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/优菈.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12531 - 早柚.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T.armour"));

        // URL => armour://armourers_workshop.plushie.moe/library/userid/name.armour

        String[] paths = {
                "/Users/sagesse/Downloads/胡桃/护摩之杖.armour",
                "/Users/sagesse/Downloads/胡桃/胡桃.armour",
                "/Users/sagesse/Downloads/钟离/钟离.armour",
                "/Users/sagesse/Downloads/浊心斯卡蒂/浊心斯卡蒂+海嗣背饰.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12531 - 早柚.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12740 - V1 Wings.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T-SW.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T-RH.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/TR-H.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T2-H.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T2.armour"
        };
        for (String p : paths) {
            this.skins.add(loadSkin(p));
        }

        this.sword = this.skins.get(0);
        this.outfit = this.skins.get(paths.length - 1);
        BakedSkin.skins = this.skins;

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

//    @SubscribeEvent
//    public void registerItems(RegistryEvent.Register<Item> event) {
//        SkinItems.registerItems(event.getRegistry());;
//    }

    public static File getModDirectory() {
        return null;
    }


//    @SubscribeEvent
//    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
//        SkinWardrobe wardrobe = SkinWardrobe.of(event.getEntityLiving());
//        if (wardrobe != null) {
//            wardrobe.update();
//        }
//    }


//    void xkmka() {
//        SkinLog.info("Setting up player render layers.");
//        EntityRendererManager entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
//        for (Map.Entry<String, PlayerRenderer> entry : entityRenderManager.getSkinMap().entrySet()) {
//            PlayerRenderer playerRenderer = entry.getValue();
////            List layerRenderers = playerRenderer.layers;
////            for (int i = 0; i < layerRenderers.size(); /*nothing*/) {
////                Object layerRenderer = layerRenderers.get(i);
////                playerRenderer.layers.remove(layerRenderer);
////            }
////            List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> layerRenderers = playerRenderer.layers;
////                Object object = ReflectionHelper.getPrivateValue(RenderLivingBase.class, playerRender, "field_177097_h", "layerRenderers");
////                if (object != null) {
////                    List<LayerRenderer<?>> layerRenderers = (List<LayerRenderer<?>>) object;
////            layerRenderers.add(0, new ModelResetLayer(playerRenderer));
////            SkinLog.info("Adding reset layer to " + playerRenderer);
////            // Looking for held item layer.
////            for (int i = 0; i < layerRenderers.size(); i++) {
////                LayerRenderer layerRenderer = layerRenderers.get(i);
////                if (layerRenderer.getClass().getName().contains("LayerHeldItem")) {
////                    // Replacing held item layer.
////                    SkinLog.info("Removing held item layer from " + playerRenderer);
////                    layerRenderers.remove(i);
////                    SkinLog.info("Adding skinned held item layer to " + playerRenderer);
////                    layerRenderers.add(new SkinLayerRendererHeldItem(playerRenderer, layerRenderer));
////                    break;
////                }
////            }
//            for (Object layerRenderer : playerRenderer.layers) {
//
//            }
//
//
////            SkinLog.info(Level.WARN, "Failed to get 'layerRenderers' on " + playerRenderer);
//            SkinLog.info("Adding 'SkinLayerRendererPlayer' to " + playerRenderer);
//            playerRenderer.addLayer(new SkinLayerRendererPlayer(playerRenderer));
//        }
//        SkinLog.info("Finished setting up player render layers.");
//    }


//    @SubscribeEvent
//    public void onRenderPlayerHand(RenderHandEvent event) {
//        if (event.isCanceled()) {
//            return;
//        }
//        SkinWardrobe wardrobe = SkinWardrobe.of(Minecraft.getInstance().player);
//        if (wardrobe == null) {
//            return;
//        }
//        ISkinPartType partType = SkinPartTypes.HAND_MAIN;
//        if (event.getHand() == Hand.OFF_HAND) {
//            partType = SkinPartTypes.HAND_OFF;
//        }
//        event.setCanceled(wardrobe.hasOverriddenEquipment(partType));
//    }

    public static File getSkinLibraryDirectory() {
        return null;
    }

    public static File getGlobalSkinDatabaseDirectory() {
        return null;
    }

    private BakedSkin loadSkin(String file) {
        Skin skin = SkinIOUtils.loadSkinFromFile(new File(file));
        return ModelBakery.INSTANCE.backedModel(skin);
    }

    @SubscribeEvent
    void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(SkinCommands.commands());
    }

//    @SubscribeEvent
//    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getPlayer() instanceof ClientPlayerEntity) {
//            ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
//            PlayerTextureReader reader = new PlayerTextureReader(player);
//        }
//    }


    @SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (!SkinConfig.isSkinnableEntity(entity)) {
            return;
        }
        event.addCapability(SkinWardrobeProvider.WARDROBE_ID, new SkinWardrobeProvider(entity));
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTooltip(RenderTooltipEvent.PostText event) {
        if (event.isCanceled()) {
            return;
        }
        if (!SkinConfig.skinPreEnabled) {
            return;
        }

        ItemStack itemStack = event.getStack();
        MatrixStack matrixStack = event.getMatrixStack();
        BakedSkin skin = BakedSkin.of(itemStack);
        if (skin == null) {
            return;
        }

        int x, y;
        int t = (int) System.currentTimeMillis();
        int size = SkinConfig.skinPreSize;
        if (SkinConfig.skinPreLocFollowMouse) {
            x = event.getX() - 28 - size;
            y = event.getY() - 4;
            if (event.getX() < mouseX) {
                x = event.getX() + event.getWidth() + 28;
            }
            y = MathHelper.clamp(y, 0, screenHeight - size);
        } else {
            x = MathHelper.ceil((screenWidth - size) * SkinConfig.skinPreLocHorizontal);
            y = MathHelper.ceil((screenHeight - size) * SkinConfig.skinPreLocVertical);
        }

        if (SkinConfig.skinPreDrawBackground) {
            GuiUtils.drawContinuousTexturedBox(matrixStack, SkinCore.TEX_GUI_PREVIEW, x, y, 0, 0, size, size, 62, 62, 4, 400);
        }

        matrixStack.pushPose();
        matrixStack.translate(x + size / 2f, y + size / 2f, 500f);
        matrixStack.scale(-1, 1, 1);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(150));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (float) (t / 10 % 360)));
        SkinItemRenderer.renderSkin(skin, 0xf000f0, 0, size, size, ItemCameraTransforms.TransformType.NONE, null, matrixStack, null);
        matrixStack.popPose();
    }

//    @SubscribeEvent
//    public void onPlayerClone(PlayerEvent.Clone event) {
//        PlayerEntity player = event.getPlayer();
//        LazyOptional<SkinWardrobe> newWardrobe = player.getCapability(SkinWardrobeProvider.WARDROBE_KEY, null);
//        LazyOptional<SkinWardrobe> oldWardrobe = event.getOriginal().getCapability(SkinWardrobeProvider.WARDROBE_KEY, null);
////        mana.setMana(oldMana.getMana());
//    }

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
        if (wardrobe.hasPart(SkinPartTypes.BIPED_SKIRT)) {
            if (entity.animationSpeed > 0.25F) {
                entity.animationSpeed = 0.25F;
                entity.animationSpeedOld = 0.25F;
            }
        }
        if (!SkinConfig.enableModelOverridden) {
            return;
        }
        EntityModel<?> entityModel = event.getRenderer().getModel();
        if (entityModel instanceof BipedModel) {
            BipedModel<?> playerModel = (BipedModel<?>) entityModel;
            if (wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
                playerModel.leftArm.visible = false;
            }
            if (wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
                playerModel.rightArm.visible = false;
            }
            if (wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
                playerModel.head.visible = false;
            }
            if (wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
                playerModel.body.visible = false;
            }
            if (wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
                playerModel.leftLeg.visible = false;
            }
            if (wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || wardrobe.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
                playerModel.rightLeg.visible = false;
            }
        }
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.getEntity().getType() != EntityType.ARMOR_STAND) {
            return;
        }
//        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        MatrixStack stack = event.getMatrixStack();
//        stack.pushPose();
//        float f = sx;
//        stack.scale(f, f, f);
//        ItemStack item = Minecraft.getInstance().player.getMainHandItem();
//        if (!item.isEmpty()) {
//            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
//            IBakedModel ibakedmodel = renderer.getModel(item, null, null);
////            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.NONE, 0xf00000, 0, event.getMatrixStack(), buffer);
////            IVertexBuilder q = buffer.getBuffer(Atlases.translucentCullBlockSheet());
//            IVertexBuilder q = buffer.getBuffer(SkinPartRenderer.CustomRenderType.SOLID333);
//
//            renderer.renderModelLists(ibakedmodel, item, 0xf00000, 0, stack, q);
//
//        }
        SkinRenderBuffer buffer1 = SkinRenderBuffer.getInstance();
        SkinItemRenderer.renderSkinAsItem(stack, outfit, event.getLight(), true, false, 32, 32, buffer1);
        SkinItemRenderer.renderSkinAsItem(stack, sword, event.getLight(), true, false, 32, 32, buffer1);
        SkinItemRenderer.renderSkinAsItem(stack, sword2, event.getLight(), true, false, 32, 32, buffer1);
        buffer1.endBatch();
//        stack.popPose();
    }

    @SubscribeEvent
    public void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (event.isCanceled()) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(event.getEntityLiving());
        if (wardrobe != null) {
            wardrobe.refresh();
        }
    }

//    @SubscribeEvent
//    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.isCanceled()) {
//            return;
//        }
//        SkinWardrobe wardrobe = SkinWardrobe.of(event.getEntityLiving());
//        if (wardrobe != null) {
//            wardrobe.sync();
//        }
//
//
////        IEntitySkinCapability skinCapability = EntitySkinCapability.get(event.player);
////        if (skinCapability != null) {
////            skinCapability.syncToPlayer((EntityPlayerMP) event.player);
////        }
////
////        IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(event.player);
////        if (wardrobeCapability != null) {
////            wardrobeCapability.syncToPlayer((EntityPlayerMP) event.player);
////        }
//    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe != null) {
            wardrobe.sync(player);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.isCanceled()) {
            return;
        }
        if (!SkinConfig.isSkinnableEntity(event.getTarget())) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(event.getTarget());
        if (wardrobe != null) {
            wardrobe.sync((ServerPlayerEntity) event.getPlayer());
        }
    }


    private void loadComplete(FMLLoadCompleteEvent evt) {
        CapabilityManager.INSTANCE.register(SkinWardrobe.class, new SkinWardrobeStorage(), () -> null);

        evt.enqueueWork(() -> {
            // Add our own custom armor layer to the various player renderers.
            entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
            for (Map.Entry<String, PlayerRenderer> entry : entityRenderManager.getSkinMap().entrySet()) {
                if (playerRenderer == null) {
                    playerRenderer = entry.getValue();
                }
                addCustomLayers(EntityType.PLAYER, entry.getValue());
            }
//            //Add our own custom armor layer to everything that has an armor layer
//            //Note: This includes any modded mobs that have vanilla's BipedArmorLayer added to them
//            for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : entityRenderManager.renderers.entrySet()) {
//                EntityRenderer<?> renderer = entry.getValue();
//                if (renderer instanceof LivingRenderer) {
//                    LivingRenderer livingRenderer = (LivingRenderer)renderer;
//                    if (livingRenderer.getModel() instanceof BipedModel || livingRenderer.getModel() instanceof VillagerModel) {
//                        addCustomLayers(entry.getKey(), livingRenderer);
//                    }
//                }
//            }
        });
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

        WardrobeArmorLayer<T, M> player = new WardrobeArmorLayer<>(renderer);
        renderer.addLayer(player);

//        SkinHeldItemLayer<T, M> heldItem = new SkinHeldItemLayer<>(renderer);
//        heldItem.bakedSkin = sword;
//        renderer.addLayer(heldItem);

//        if (elytraLayer != null) {
//            renderer.addLayer(new ExampleElytraLayer(renderer));
//            LOGGER.debug("Added Custom Elytra Layer to entity of type: {}", type.getRegistryName());
//        }
    }

//    private void doClientStuff(final FMLClientSetupEvent event) {
//        // do something that can only be done on the client
//        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
//    }
//
//    private void enqueueIMC(final InterModEnqueueEvent event)
//    {
//        // some example code to dispatch IMC to another mod
//        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
//    }
//
//    private void processIMC(final InterModProcessEvent event)
//    {
//        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
//    }
//    // You can use SubscribeEvent and let the Event Bus discover methods to call
//    @SubscribeEvent
//    public void onServerStarting(FMLServerStartingEvent event) {
//        // do something when the server starts
//        LOGGER.info("HELLO from server starting");
//    }
//
//    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
//    // Event bus for receiving Registry Events)
//    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
//    public static class RegistryEvents {
//        @SubscribeEvent
//        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
//            // register a new block here
//            LOGGER.info("HELLO from Register Block");
//        }
//    }
}

//
//import org.apache.logging.log4j.Logger;
//
//import moe.plushie.armourers_workshop.core.command.CommandArmourers;
//import moe.plushie.armourers_workshop.core.creativetab.CreativeTabMain;
//import moe.plushie.armourers_workshop.core.creativetab.CreativeTabPaintingTools;
//import moe.plushie.armourers_workshop.core.lib.LibModInfo;
//import moe.plushie.armourers_workshop.core.cache.CommonSkinCache;
//import moe.plushie.armourers_workshop.proxies.CommonProxy;
//import moe.plushie.armourers_workshop.utils.ModLogger;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.common.Mod.EventHandler;
//import net.minecraftforge.fml.common.Mod.Instance;
//import net.minecraftforge.fml.common.SidedProxy;
//import net.minecraftforge.fml.common.event.FMLInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
//import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
//
//@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.MOD_VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS, dependencies = LibModInfo.DEPENDENCIES, acceptedMinecraftVersions = LibModInfo.MC_VERSION, updateJSON = LibModInfo.UPDATE_URL)
//public class ArmourersWorkshop {
//
//    /*
//     * Hello and welcome to the Armourer's Workshop source code.
//     *
//     * Note: Any time the texture that is used on the player model is referred to,
//     * (normal called the players skin) it will be called the player texture or
//     * entity texture to prevent confusion with AW skins.
//     */
//
//    @Instance(LibModInfo.ID)
//    private static ArmourersWorkshop instance;
//
//    private static Logger logger;
//
//    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
//    private static CommonProxy proxy;
//
//    public static final CreativeTabMain TAB_MAIN = new CreativeTabMain();
//    public static final CreativeTabPaintingTools TAB_PAINTING_TOOLS = new CreativeTabPaintingTools();
//
//    @EventHandler
//    public void perInit(FMLPreInitializationEvent event) {
//        logger = event.getModLog();
//        ModLogger.log(String.format("Loading %s version %s.", LibModInfo.NAME, LibModInfo.MOD_VERSION));
//        proxy.preInit(event);
//        proxy.initLibraryManager();
//    }
//
//    @EventHandler
//    public void init(FMLInitializationEvent event) {
//        proxy.init(event);
//        proxy.registerKeyBindings();
//        proxy.initRenderers();
//    }
//
//    @EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        proxy.postInit(event);
//    }
//
//    @EventHandler
//    public void serverStart(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandArmourers());
//        CommonSkinCache.INSTANCE.serverStarted();
//    }
//
//    @EventHandler
//    public void serverStopped(FMLServerStoppedEvent event) {
//        CommonSkinCache.INSTANCE.serverStopped();
//    }
//
//    public static boolean isDedicated() {
//        return proxy.getClass() == CommonProxy.class;
//    }
//
//    public static CommonProxy getProxy() {
//        return proxy;
//    }
//
//    public static ArmourersWorkshop getInstance() {
//        return instance;
//    }
//
//    public static Logger getLogger() {
//        return logger;
//    }
//}
