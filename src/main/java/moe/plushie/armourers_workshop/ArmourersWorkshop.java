package moe.plushie.armourers_workshop;


import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.model.bake.ModelBakery;
import moe.plushie.armourers_workshop.core.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.render.entity.SkinLayerRendererHeldItem;
import moe.plushie.armourers_workshop.core.render.entity.SkinLayerRendererPlayer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.type.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.SkinCommand;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;
import java.util.Map;

@Mod("armourers_workshop")
public class ArmourersWorkshop {
    public ArmourersWorkshop() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

        CubeRegistry.init();
        SkinModelRenderer.init();
        PaintTypeRegistry.init();

//        Skin skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Projects/Minecraft/Armourers-Workshop/web/list/Witch's Skirt.armour"));

//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Projects/Minecraft/Armourers-Workshop/web/list/Witch's Skirt.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/胡桃/胡桃.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/钟离/钟离.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/胡桃/护摩之杖.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/套装（完全）.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Downloads/优菈.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12531 - 早柚.armour"));
//        this.skin = SkinIOUtils.loadSkinFromFile(new File("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T.armour"));

//        this.sword = loadSkin("/Users/sagesse/Downloads/胡桃/护摩之杖.armour");
//        this.outfit = loadSkin("/Users/sagesse/Downloads/胡桃/胡桃.armour");

        this.sword = loadSkin("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T-SW.armour");
        this.outfit = loadSkin("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T.armour");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private BakedSkin loadSkin(String file) {
        Skin skin = SkinIOUtils.loadSkinFromFile(new File(file));
        BakedSkin bakedSkin = ModelBakery.INSTANCE.backedModel(skin);
        return bakedSkin;
    }

    private BakedSkin outfit;
    private BakedSkin sword;

    @SubscribeEvent
    void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(SkinCommand.register());
    }

    @SubscribeEvent
    void renderLivingEvent(RenderLivingEvent.Pre event) {
        EntityModel entityModel = event.getRenderer().getModel();
        if (entityModel instanceof PlayerModel) {
            PlayerModel playerModel = (PlayerModel) entityModel;
            Skin skin = outfit.getSkin();
            playerModel.leftArm.visible = skin.isModelOverridden(SkinPartTypes.BIPED_LEFT_ARM);
            playerModel.rightArm.visible = skin.isModelOverridden(SkinPartTypes.BIPED_RIGHT_ARM);
            playerModel.head.visible = skin.isModelOverridden(SkinPartTypes.BIPED_HEAD);
            playerModel.body.visible = skin.isModelOverridden(SkinPartTypes.BIPED_CHEST);
            playerModel.leftLeg.visible = skin.isModelOverridden(SkinPartTypes.BIPED_LEFT_LEG) || skin.isModelOverridden(SkinPartTypes.BIPED_LEFT_FOOT);
            playerModel.rightLeg.visible = skin.isModelOverridden(SkinPartTypes.BIPED_RIGHT_LEG) || skin.isModelOverridden(SkinPartTypes.BIPED_RIGHT_FOOT);
        }
    }


//    @SubscribeEvent
//    void renderHandEvent(RenderHandEvent event) {
//        event.getMatrixStack();
//        event.setCanceled(true);
//    }

//    @SubscribeEvent
//    public void onRender(RenderPlayerEvent.Pre event) {
//        PlayerEntity player = event.getPlayer();
//        // Limit the players limbs if they have a skirt equipped.
//        // A proper lady should not swing her legs around!
//        if (isPlayerWearingSkirt(player)) {
//            if (player.limbSwingAmount > 0.25F) {
//                player.limbSwingAmount = 0.25F;
//                player.prevLimbSwingAmount = 0.25F;
//            }
//        }
//    }

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

    @SubscribeEvent
    void renderLivingEvent(RenderLivingEvent event) {
        if (event.getEntity().getType() != EntityType.ARMOR_STAND) {
            return;
        }
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        MatrixStack stack = event.getMatrixStack();

        stack.pushPose();
        //SkinModelRenderer.INSTANCE.renderSkin(bakedSkin, event.getRenderer().getModel(), stack, buffer);
        SkinItemRenderer.renderSkinAsItem(stack, buffer, outfit, true, false, 32, 32);
        SkinItemRenderer.renderSkinAsItem(stack, buffer, sword, true, false, 32, 32);
        stack.popPose();
    }

    private  EntityRendererManager entityRenderManager;
    PlayerRenderer playerRenderer;
    private void loadComplete(FMLLoadCompleteEvent evt) {
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


// this.minecraft.options.getCameraType().isFirstPerson()
    private <T extends LivingEntity, M extends BipedModel<T>> void addCustomLayers(EntityType<?> type, LivingRenderer<T, M> renderer) {
        BipedArmorLayer<T, M, ?> bipedArmorLayer = null;
        ElytraLayer<T, M> elytraLayer = null;
        HeldItemLayer<T, M> heldItemLayer = null;
        for (LayerRenderer<T, M> layerRenderer : renderer.layers) {
            // Validate against the layer render being null, as it seems like some mods do stupid things and add in null layers
            if (layerRenderer == null) {
                continue;
            }
            // Only allow an exact class match, so we don't add to modded entities that only have a modded extended armor or elytra layer
            Class<?> layerClass = layerRenderer.getClass();
            if (layerClass == BipedArmorLayer.class) {
                bipedArmorLayer = (BipedArmorLayer<T, M, ?>) layerRenderer;
            } else if (layerClass == ElytraLayer.class) {
                elytraLayer = (ElytraLayer<T, M>) layerRenderer;
            } else if (layerClass == HeldItemLayer.class) {
                heldItemLayer = (HeldItemLayer<T, M>) layerRenderer;
            }
        }
        if (bipedArmorLayer != null) {
            renderer.layers.remove(bipedArmorLayer);
//            SkinLog.info("Added Custom Armor Layer to entity of type: {}", type.getRegistryName());
        }
        if (heldItemLayer != null) {
            renderer.layers.remove(heldItemLayer);
        }

        SkinLayerRendererPlayer<T, M> player = new SkinLayerRendererPlayer<>(renderer);
        player.bakedSkin = outfit;
        renderer.addLayer(player);

        SkinLayerRendererHeldItem<T, M> heldItem = new SkinLayerRendererHeldItem<>(renderer);
        heldItem.bakedSkin = sword;
        renderer.addLayer(heldItem);

//        if (elytraLayer != null) {
//            renderer.addLayer(new ExampleElytraLayer(renderer));
//            LOGGER.debug("Added Custom Elytra Layer to entity of type: {}", type.getRegistryName());
//        }
    }

    public static File getModDirectory() {
        return null;
    }

    public static File getSkinLibraryDirectory() {
        return null;
    }

    public static File getGlobalSkinDatabaseDirectory() {
        return null;
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
//import moe.plushie.armourers_workshop.core.skin.cache.CommonSkinCache;
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
