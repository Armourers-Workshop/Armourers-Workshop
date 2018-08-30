package riskyken.armourersWorkshop.proxies;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.handler.BlockHighlightRenderHandler;
import riskyken.armourersWorkshop.client.handler.DebugTextHandler;
import riskyken.armourersWorkshop.client.handler.EquipmentWardrobeHandler;
import riskyken.armourersWorkshop.client.handler.ItemTooltipHandler;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.handler.PlayerTextureHandler;
import riskyken.armourersWorkshop.client.handler.RehostedJarHandler;
import riskyken.armourersWorkshop.client.handler.SkinPreviewHandler;
import riskyken.armourersWorkshop.client.library.ClientLibraryManager;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.render.entity.EntitySkinRenderHandler;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockColourable;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockGlobalSkinLibrary;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockHologramProjector;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockMannequin;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockSkinnable;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.client.texture.PlayerTextureDownloader;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;
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
    public static PlayerTextureDownloader playerTextureDownloader;
    
    public static int renderPass;
    
    public static boolean isJrbaClientLoaded() {
        return ModAddonManager.addonJBRAClient.isModLoaded();
    }
    
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        enableCrossModSupport();
        spamSillyMessages();
        new RehostedJarHandler(event.getSourceFile(), "Armourers-Workshop-" + LibModInfo.VERSION + ".jar");
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
        new SkinPreviewHandler();
        /*
        Render arrowRender = new RenderSkinnedArrow();
        arrowRender.setRenderManager(RenderManager.instance);
        RenderManager.instance.entityRenderMap.put(EntityArrow.class, arrowRender);
        */
        //Register tile entity renderers.
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourer.class, new RenderBlockArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMannequin.class, new RenderBlockMannequin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMiniArmourer.class, new RenderBlockMiniArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkinnable.class, new RenderBlockSkinnable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColourable.class, new RenderBlockColourable());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBoundingBox.class, new RenderBlockColourable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGlobalSkinLibrary.class, new RenderBlockGlobalSkinLibrary());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHologramProjector.class, new RenderBlockHologramProjector());
        
        //Register item renderers.
        /*
        ModelMannequin modelSteve = new ModelMannequin(false);
        ModelMannequin modelAlex = new ModelMannequin(true);
        
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.miniArmourer), new RenderItemBlockMiniArmourer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.mannequin), new RenderItemMannequin(modelSteve, modelAlex));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.doll), new RenderItemMannequin(modelSteve, modelAlex));
        
        
        //Register block renderers.
        RenderingRegistry.registerBlockHandler(new RenderBlockColourMixer());
        RenderingRegistry.registerBlockHandler(new RenderBlockGlowing());
        */
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        equipmentWardrobeHandler = new EquipmentWardrobeHandler();
        playerTextureHandler = new PlayerTextureHandler();
        playerTextureDownloader = new PlayerTextureDownloader();
        ClientSkinCache.init();
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new DebugTextHandler());
    }
    
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ModAddonManager.initRenderers();
        EntitySkinRenderHandler.INSTANCE.initRenderer();
        if (HolidayHelper.valentins.isHolidayActive()) {
            enableValentinsClouds();
        }
        
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable()
        {
            public String call() throws Exception
            {
                int bakeQueue = ModelBakery.INSTANCE.getBakingQueueSize();
                return "\n" + 
                        "\t\tRender Type: " + getSkinRenderType().toString() + "\n" + 
                        "\t\tTexture Render: " + useSafeTextureRender() + "\n" + 
                        "\t\tBaking Queue: " + bakeQueue + "\n" +
                        "\t\tRequest Queue: " + (ClientSkinCache.INSTANCE.getRequestQueueSize() - bakeQueue) + "\n" +
                        "\t\tTexture Painting: " + useTexturePainting() + "\n" +
                        "\t\tMultipass Skin Rendering: " + useMultipassSkinRendering();
            }

            public String getLabel()
            {
                return "Armourer's Workshop";
            }
        });
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
        if (ModAddonManager.addonMorePlayerModels.isModLoaded() & ModAddonManager.addonSmartMoving.isModLoaded()) {
            ModLogger.log(Level.WARN, "Smart Moving and More Player Models are both installed. Armourer's Workshop can not support this.");
        }
        if (ModAddonManager.addonColoredLights.isModLoaded() & ModAddonManager.addonSmartMoving.isModLoaded()) {
            ModLogger.log(Level.WARN, "Colored Lights and Smart Moving are both installed. Armourer's Workshop can not support this.");
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
            if (ModAddonManager.addonMorePlayerModels.isModLoaded()) {
                return SkinRenderType.RENDER_EVENT;
            }
            if (ModAddonManager.addonShaders.isModLoaded() & !ModAddonManager.addonSmartMoving.isModLoaded()) {
                return SkinRenderType.RENDER_EVENT;
            }
            if (ModAddonManager.addonColoredLights.isModLoaded() & !ModAddonManager.addonSmartMoving.isModLoaded()) {
                return SkinRenderType.RENDER_EVENT;
            }
            if (ModAddonManager.addonJBRAClient.isModLoaded()) {
                return SkinRenderType.RENDER_EVENT;
            }
            return SkinRenderType.MODEL_ATTACHMENT;
        }
    }
    
    public static boolean useSafeTextureRender() {
        if (ModAddonManager.addonShaders.isModLoaded()) {
            return true;
        }
        if (ConfigHandlerClient.skinTextureRenderOverride) {
            return true;
        }
        if (ModAddonManager.addonColoredLights.isModLoaded()) {
            return true;
        }
        return false;
    }
    
    public static boolean useTexturePainting() {
        if (ConfigHandlerClient.texturePainting == 1) {
            return true;
        }
        if (ConfigHandlerClient.texturePainting == 2) {
            return false;
        }
        if (ModAddonManager.addonJBRAClient.isModLoaded()) {
            return false;
        }
        return true;
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
    public void receivedCommandFromSever(CommandType command) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        switch (command) {
        case CLEAR_MODEL_CACHE:
            ClientSkinCache.INSTANCE.clearCache();
            CommonSkinCache.INSTANCE.clearAll();
            break;
        case OPEN_ADMIN_PANEL:
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.ADMIN_PANEL, player.getEntityWorld(), 0, 0, 0);
            break;
        }
    }
    
    @Override
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        EntitySkinHandler.INSTANCE.receivedEquipmentData(equipmentData, entityId);
    }
    
    @Override
    public void receivedSkinFromLibrary(String fileName, String filePath, Skin skin, SendType sendType) {
        switch (sendType) {
        case LIBRARY_SAVE:
            SkinIOUtils.saveSkinFromFileName(filePath, fileName + SkinIOUtils.SKIN_FILE_EXTENSION, skin);
            ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, filePath, skin.getSkinType()), LibraryFileType.LOCAL, null);
            break;
        case GLOBAL_UPLOAD:
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GuiGlobalLibrary) {
                ((GuiGlobalLibrary)screen).gotSkinFromServer(skin);
            }
            break;
        }
    }
    
    @Override
    public boolean isLocalPlayer(String username) {
        GameProfile gameProfile = getLocalGameProfile();
        if (gameProfile != null && !StringUtils.isNullOrEmpty(gameProfile.getName())) {
            if (username.equals(gameProfile.getName())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean haveFullLocalProfile() {
        GameProfile gameProfile = getLocalGameProfile();
        if (gameProfile.isComplete()) {
            if (gameProfile.getProperties().containsKey("textures")) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public GameProfile getLocalGameProfile() {
        return Minecraft.getMinecraft().player.getGameProfile();
    }
    
    public static enum SkinRenderType {
        RENDER_EVENT,
        MODEL_ATTACHMENT,
        RENDER_LAYER
    }
}
