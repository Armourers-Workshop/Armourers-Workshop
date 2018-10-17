package moe.plushie.armourers_workshop.proxies;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.handler.BlockHighlightRenderHandler;
import moe.plushie.armourers_workshop.client.handler.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.client.handler.DebugTextHandler;
import moe.plushie.armourers_workshop.client.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.client.handler.ModClientFMLEventHandler;
import moe.plushie.armourers_workshop.client.handler.PlayerTextureHandler;
import moe.plushie.armourers_workshop.client.handler.RehostedJarHandler;
import moe.plushie.armourers_workshop.client.handler.SkinPreviewHandler;
import moe.plushie.armourers_workshop.client.library.ClientLibraryManager;
import moe.plushie.armourers_workshop.client.model.ICustomModel;
import moe.plushie.armourers_workshop.client.model.ModelMannequin;
import moe.plushie.armourers_workshop.client.model.bake.ModelBakery;
import moe.plushie.armourers_workshop.client.render.RenderBridge;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.render.entity.EntitySkinRenderHandler;
import moe.plushie.armourers_workshop.client.render.item.RenderItemEquipmentSkin;
import moe.plushie.armourers_workshop.client.render.item.RenderItemMannequin;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockArmourer;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockBoundingBox;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockColourable;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockGlobalSkinLibrary;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockHologramProjector;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockMannequin;
import moe.plushie.armourers_workshop.client.render.tileEntity.RenderBlockSkinnable;
import moe.plushie.armourers_workshop.client.settings.Keybindings;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.client.texture.PlayerTextureDownloader;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.items.ItemGiftSack;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.library.LibraryFileType;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityBoundingBox;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.utils.HolidayHelper;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = LibModInfo.ID, value = { Side.CLIENT })
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static ClientWardrobeHandler equipmentWardrobeHandler;
    public static PlayerTextureHandler playerTextureHandler;
    public static PlayerTextureDownloader playerTextureDownloader;
    
    public static int renderPass;
    
    public static boolean isJrbaClientLoaded() {
        return ModAddonManager.addonJBRAClient.isModLoaded();
    }
    
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (int i = 0; i < ModBlocks.BLOCK_LIST.size(); i++) {
            Block block = ModBlocks.BLOCK_LIST.get(i);
            if (block instanceof ICustomModel) {
                ((ICustomModel)block).registerModels();
            }
        }
        for (int i = 0; i < ModItems.ITEM_LIST.size(); i++) {
            Item item = ModItems.ITEM_LIST.get(i);
            if (item instanceof ICustomModel) {
                ((ICustomModel)item).registerModels();
            }
        }
        ModItems.skin.setTileEntityItemStackRenderer(new RenderItemEquipmentSkin());
        ModelMannequin modelSteve = new ModelMannequin(false);
        ModelMannequin modelAlex = new ModelMannequin(true);
        RenderItemMannequin renderItemMannequin = new RenderItemMannequin(modelSteve, modelAlex);
        Item.getItemFromBlock(ModBlocks.mannequin).setTileEntityItemStackRenderer(renderItemMannequin);
        Item.getItemFromBlock(ModBlocks.doll).setTileEntityItemStackRenderer(renderItemMannequin);
    }
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        File configDir = event.getSuggestedConfigurationFile().getParentFile();
        configDir = new File(configDir, LibModInfo.ID);
        ConfigHandlerClient.init(new File(configDir, "client.cfg"));
        
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
        RenderBridge.init();
        /*
        Render arrowRender = new RenderSkinnedArrow();
        arrowRender.setRenderManager(RenderManager.instance);
        RenderManager.instance.entityRenderMap.put(EntityArrow.class, arrowRender);
        */
        // Register tile entity renderers.
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourer.class, new RenderBlockArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMannequin.class, new RenderBlockMannequin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkinnable.class, new RenderBlockSkinnable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColourable.class, new RenderBlockColourable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBoundingBox.class, new RenderBlockBoundingBox());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGlobalSkinLibrary.class, new RenderBlockGlobalSkinLibrary());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHologramProjector.class, new RenderBlockHologramProjector());
        
        
        // Register coloured items and blocks.
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.paintbrush);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.paintRoller);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.colourPicker);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.dyeBottle);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.hueTool);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.soap);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColour(), ModItems.giftSack);
        
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColour(), ModBlocks.skinCube);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColour(), ModBlocks.skinCubeGlass);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColour(), ModBlocks.skinCubeGlowing);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColour(), ModBlocks.skinCubeGlassGlowing);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColour(), ModBlocks.colourMixer);
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        equipmentWardrobeHandler = new ClientWardrobeHandler();
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
        if (HolidayHelper.valentines.isHolidayActive()) {
            enableValentinesClouds();
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
    
    private void enableValentinesClouds() {
        ModLogger.log("Love is in the air!");
        try {
            Object o = ReflectionHelper.getPrivateValue(RenderGlobal.class, null, "CLOUDS_TEXTURES", "field_110925_j");
            Field f = ReflectionHelper.findField(ResourceLocation.class, "namespace", "field_110626_a");
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
            /*
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
            */
            return SkinRenderType.RENDER_LAYER;
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
        return true;
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

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.KEY_UNDO);
        ClientRegistry.registerKeyBinding(Keybindings.OPEN_WARDROBE);
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
            player.openGui(ArmourersWorkshop.getInstance(), LibGuiIds.ADMIN_PANEL, player.getEntityWorld(), 0, 0, 0);
            break;
        }
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
    
    private static class BlockColour implements IBlockColor {

        @Override
        public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity != null && tileEntity instanceof IPantable) {
                return ((IPantable)tileEntity).getColour(tintIndex);
            }
            return 0xFFFFFFFF;
        }
    }
    
    private static class ItemColour implements IItemColor {

        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (stack.getItem() == ModItems.giftSack) {
                return ((ItemGiftSack)stack.getItem()).colorMultiplier(stack, tintIndex);
            }
            if (stack.getItem() == ModItems.dyeBottle) {
                if (tintIndex == 0) {
                    return PaintingHelper.getToolPaintColourRGB(stack);
                }
                return 0xFFFFFFFF;
            }
            if (stack.getItem() == ModItems.colourPicker) {
                if (tintIndex == 0) {
                    return PaintingHelper.getToolPaintColourRGB(stack);
                }
                return 0xFFFFFFFF;
            }
            if (tintIndex == 1) {
                return PaintingHelper.getToolPaintColourRGB(stack);
            }
            return 0xFFFFFFFF;
        }
    }
    
    @Override
    public MinecraftServer getServer() {
        return Minecraft.getMinecraft().getIntegratedServer();
    }
}
