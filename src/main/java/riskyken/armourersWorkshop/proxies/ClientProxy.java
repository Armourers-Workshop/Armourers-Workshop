package riskyken.armourersWorkshop.proxies;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.handler.BlockHighlightRenderHandler;
import riskyken.armourersWorkshop.client.handler.DebugTextHandler;
import riskyken.armourersWorkshop.client.handler.EquipmentWardrobeHandler;
import riskyken.armourersWorkshop.client.handler.ItemTooltipHandler;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.handler.PlayerTextureHandler;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.client.library.ClientLibraryManager;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.render.block.RenderBlockColourMixer;
import riskyken.armourersWorkshop.client.render.block.RenderBlockGlowing;
import riskyken.armourersWorkshop.client.render.entity.EntitySkinRenderHandler;
import riskyken.armourersWorkshop.client.render.entity.RenderSkinnedArrow;
import riskyken.armourersWorkshop.client.render.item.RenderItemBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.item.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemMannequin;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockColourable;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockGlobalSkinLibrary;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockMannequin;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockSkinnable;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.blocks.BlockColourMixer;
import riskyken.armourersWorkshop.common.blocks.BlockColourable;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static EquipmentWardrobeHandler equipmentWardrobeHandler;
    public static PlayerTextureHandler playerTextureHandler;
    
    private static boolean shadersModLoaded;
    private static boolean moreplayermodelsLoaded;
    private static boolean coloredLightsLoaded;
    private static boolean smartMovingLoaded;
    private static boolean jrbaClientLoaded;
    
    public static int renderPass;
    public static IIcon dyeBottleSlotIcon;
    
    public static boolean isJrbaClientLoaded() {
        return jrbaClientLoaded;
    }
    
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    public void preInit() {
        enableCrossModSupport();
        spamSillyMessages();
    }
    
    @Override
    public void initLibraryManager() {
        libraryManager = new ClientLibraryManager();
    }

    @Override
    public void initRenderers() {
        SkinModelRenderer.init();
        EntitySkinRenderHandler.init();
        new BlockHighlightRenderHandler();
        new ItemTooltipHandler();
        Render arrowRender = new RenderSkinnedArrow();
        arrowRender.setRenderManager(RenderManager.instance);
        RenderManager.instance.entityRenderMap.put(EntityArrow.class, arrowRender);
        
        //Register tile entity renderers.
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourer.class, new RenderBlockArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMannequin.class, new RenderBlockMannequin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMiniArmourer.class, new RenderBlockMiniArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkinnable.class, new RenderBlockSkinnable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColourable.class, new RenderBlockColourable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBoundingBox.class, new RenderBlockColourable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGlobalSkinLibrary.class, new RenderBlockGlobalSkinLibrary());
        
        //Register item renderers.
        ModelMannequin modelMannequin = new ModelMannequin();
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.miniArmourer), new RenderItemBlockMiniArmourer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.mannequin), new RenderItemMannequin(modelMannequin));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.doll), new RenderItemMannequin(modelMannequin));
        
        //Register block renderers.
        RenderingRegistry.registerBlockHandler(new RenderBlockColourMixer());
        RenderingRegistry.registerBlockHandler(new RenderBlockGlowing());
    }
    
    @Override
    public void init() {
        equipmentWardrobeHandler = new EquipmentWardrobeHandler();
        playerTextureHandler = new PlayerTextureHandler();
        ClientSkinCache.init();
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new DebugTextHandler());
    }
    
    @Override
    public void postInit() {
        ModAddonManager.initRenderers();
        EntitySkinRenderHandler.INSTANCE.initRenderer();
        if (HolidayHelper.valentins.isHolidayActive()) {
            enableValentinsClouds();
        }
    }
    
    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            dyeBottleSlotIcon = event.map.registerIcon(LibItemResources.SLOT_DYE_BOTTLE);
        }
    }
    
    private void enableValentinsClouds() {
        ModLogger.log("Love is in the air!");
        try {
            Object o = ReflectionHelper.getPrivateValue(RenderGlobal.class, null, "locationCloudsPng", "field_110925_j");
            Field f = ReflectionHelper.findField(ResourceLocation.class, "resourceDomain", "field_110626_a");
            f.setAccessible(true);
            f.set(o, LibModInfo.ID.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void enableCrossModSupport() {
        try {
            Class.forName("shadersmodcore.client.Shaders");
            ModLogger.log("Shaders mod support active");
            shadersModLoaded = true;
        } catch (Exception e) {
        }
        if (Loader.isModLoaded("moreplayermodels")) {
            moreplayermodelsLoaded = true;
            ModLogger.log("More Player Models support active");
        }
        if (Loader.isModLoaded("easycoloredlights")) {
            coloredLightsLoaded = true;
            ModLogger.log("Colored Lights support active");
        }
        if (Loader.isModLoaded("SmartMoving")) {
            smartMovingLoaded = true;
            ModLogger.log("Smart Moving support active");
        }
        try {
            Class.forName("JinRyuu.JBRA.JBRA");
            jrbaClientLoaded = true;
            ModLogger.log("JRBA Client support active");
        } catch (Exception e) {
        }
        if (moreplayermodelsLoaded & smartMovingLoaded) {
            ModLogger.log(Level.WARN, "Smart Moving and More Player Models are both installed. Armourer's Workshop cannot support this.");
        }
        if (coloredLightsLoaded & smartMovingLoaded) {
            ModLogger.log(Level.WARN, "Colored Lights and Smart Moving are both installed. Armourer's Workshop cannot support this.");
        }
        
        ModLogger.log("Skin render type set to: " + getSkinRenderType().toString().toLowerCase());
    }
    
    public static SkinRenderType getSkinRenderType() {
        switch (ConfigHandlerClient.skinRenderType) {
        case 1: //Force render event
            return SkinRenderType.RENDER_EVENT;
        case 2: //Force model attachment
            return SkinRenderType.MODEL_ATTACHMENT;
        case 3: //Force render layer
            return SkinRenderType.RENDER_LAYER;
        default: //Auto
            if (moreplayermodelsLoaded) {
                return SkinRenderType.RENDER_EVENT;
            }
            if (shadersModLoaded & !smartMovingLoaded) {
                return SkinRenderType.RENDER_EVENT;
            }
            if (coloredLightsLoaded & !smartMovingLoaded) {
                return SkinRenderType.RENDER_EVENT;
            }
            if (jrbaClientLoaded) {
                return SkinRenderType.RENDER_EVENT;
            }
            return SkinRenderType.MODEL_ATTACHMENT;
        }
    }
    
    public static boolean useSafeTextureRender() {
        if (shadersModLoaded) {
            return true;
        }
        if (ConfigHandlerClient.skinTextureRenderOverride) {
            return true;
        }
        if (coloredLightsLoaded) {
            return true;
        }
        return false;
    }
    
    public static boolean useMultipassSkinRendering() {
        return ConfigHandlerClient.multipassSkinRendering;
    }
    
    public static int getNumberOfRenderLayers() {
        if (useMultipassSkinRendering()) {
            return 4;
        } else {
            return 2;
        }
    }
    
    private void spamSillyMessages() {
        if (Loader.isModLoaded("Tails")) {
            ModLogger.log("Tails detected! - Sand praising module active.");
        }
        if (Loader.isModLoaded("BuildCraft|Core")) {
            ModLogger.log("Buildcraft detected! - Enabling knishes support.");
        }
        if (Loader.isModLoaded("integratedcircuits")) {
            ModLogger.log("Integrated Circuits detected! - Applying cosplay to mannequins.");
        }
    }
    
    //TODO Remove this and use IWorldAccess
    public static void playerLeftTrackingRange(PlayerPointer playerPointer) {
        SkinModelRenderer.INSTANCE.removeEquipmentData(playerPointer);
        equipmentWardrobeHandler.removeEquipmentWardrobeData(playerPointer);
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.openCustomArmourGui);
        ClientRegistry.registerKeyBinding(Keybindings.undo);
    }
    
    @Override
    public void addEquipmentData(PlayerPointer playerPointer, EntityEquipmentData equipmentData) {
        SkinModelRenderer.INSTANCE.addEquipmentData(playerPointer, equipmentData);
    }

    @Override
    public int getPlayerModelCacheSize() {
        return ClientSkinCache.INSTANCE.getCacheSize();
    }

    @Override
    public void receivedEquipmentData(Skin skin) {
        ModelBakery.INSTANCE.receivedUnbakedModel(skin);
    }
    
    @Override
    public void receivedCommandFromSever(CommandType command) {
        switch (command) {
        case CLEAR_MODEL_CACHE:
            ClientSkinCache.INSTANCE.clearCache();
            break;
        }
    }
    
    @Override
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        EntitySkinHandler.INSTANCE.receivedEquipmentData(equipmentData, entityId);
    }
    
    @Override
    public void receivedSkinFromLibrary(String fileName, Skin skin) {
        SkinIOUtils.saveSkinFromFileName(fileName + ".armour", skin);
        ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, skin.getSkinType()), LibraryFileType.LOCAL, null);
    }
    
    @Override
    public int getBlockRenderType(Block block) {
        if (block instanceof BlockColourable) {
            return RenderBlockGlowing.renderId;
        }
        if (block instanceof BlockColourMixer) {
            return RenderBlockColourMixer.renderId;
        }
        return super.getBlockRenderType(block);
    }
    
    public static enum SkinRenderType {
        RENDER_EVENT,
        MODEL_ATTACHMENT,
        RENDER_LAYER
    }
}
