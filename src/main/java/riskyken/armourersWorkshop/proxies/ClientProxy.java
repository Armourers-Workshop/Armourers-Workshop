package riskyken.armourersWorkshop.proxies;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.handler.DebugTextHandler;
import riskyken.armourersWorkshop.client.handler.EquipmentWardrobeHandler;
import riskyken.armourersWorkshop.client.handler.ItemTooltipHandler;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.handler.PlayerTextureHandler;
import riskyken.armourersWorkshop.client.library.ClientLibraryManager;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.render.entity.layers.LayerSkin;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockColourable;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockMannequin;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.tileEntity.RenderBlockSkinnable;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
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
    public static int renderPass;
    
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
        new ItemTooltipHandler();
        //Render arrowRender = new RenderSkinnedArrow();
        //arrowRender.setRenderManager(RenderManager.instance);
        //RenderManager.instance.entityRenderMap.put(EntityArrow.class, arrowRender);
        
        //Register tile entity renderers.
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourer.class, new RenderBlockArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMannequin.class, new RenderBlockMannequin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMiniArmourer.class, new RenderBlockMiniArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkinnable.class, new RenderBlockSkinnable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColourable.class, new RenderBlockColourable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBoundingBox.class, new RenderBlockColourable());
        
        //Register item renderers.
        ModelMannequin modelMannequin = new ModelMannequin();
        /*
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.miniArmourer), new RenderItemBlockMiniArmourer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.mannequin), new RenderItemMannequin(modelMannequin));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.doll), new RenderItemMannequin(modelMannequin));
        
        //Register block renderers.
        RenderingRegistry.registerBlockHandler(new RenderBlockColourMixer());
        RenderingRegistry.registerBlockHandler(new RenderBlockGlowing());
        */
        
        String[] skinTypes = {"default", "slim"};
        for (int i = 0; i < skinTypes.length; i++) {
            RenderPlayer playerRenderer = Minecraft.getMinecraft().getRenderManager().getSkinMap().get(skinTypes[i]);
            playerRenderer.addLayer(new LayerSkin(playerRenderer));
        }
        
        registerRender(ModItems.equipmentSkinTemplate);
        registerRender(ModItems.equipmentSkin);
        
        registerRender(ModItems.paintbrush);
        registerRender(ModItems.paintRoller);
        registerRender(ModItems.colourPicker);
        registerRender(ModItems.burnTool);
        registerRender(ModItems.dodgeTool);
        registerRender(ModItems.colourNoiseTool);
        registerRender(ModItems.shadeNoiseTool);
        registerRender(ModItems.hueTool);
        //registerRender(ModItems.blendingTool);
        registerRender(ModItems.blockMarker);
        
        registerRender(ModItems.mannequinTool);
        registerRender(ModItems.wandOfStyle);
        registerRender(ModItems.soap);
        registerRender(ModItems.dyeBottle);
        registerRender(ModItems.guideBook);
        registerRender(ModItems.armourersHammer);
        registerRender(ModItems.armourContainerItem);
        registerRender(ModItems.armourContainer[0]);
        registerRender(ModItems.armourContainer[1]);
        registerRender(ModItems.armourContainer[2]);
        registerRender(ModItems.armourContainer[3]);
        
        registerRender(ModBlocks.armourerBrain);
        registerRender(ModBlocks.miniArmourer);
        registerRender(ModBlocks.armourLibrary, 0);
        registerRender(ModBlocks.armourLibrary, 1);
        registerRender(ModBlocks.colourable);
        registerRender(ModBlocks.colourableGlowing);
        registerRender(ModBlocks.colourableGlass);
        registerRender(ModBlocks.colourableGlassGlowing);
        registerRender(ModBlocks.colourMixer);
        registerRender(ModBlocks.skinningTable);
        registerRender(ModBlocks.dyeTable);
        registerRender(ModBlocks.mannequin);
        
        registerItemColorHandler(ModItems.paintbrush);
        registerItemColorHandler(ModItems.paintRoller);
        registerItemColorHandler(ModItems.colourPicker);
        registerItemColorHandler(ModItems.hueTool);
        
        registerBlockColorHandler(ModBlocks.colourMixer);
        registerBlockColorHandler(ModBlocks.colourable);
        registerBlockColorHandler(ModBlocks.colourableGlowing);
        registerBlockColorHandler(ModBlocks.colourableGlass);
        registerBlockColorHandler(ModBlocks.colourableGlassGlowing);
    }
    
    private void registerBlockColorHandler(Block block) {
        if (block instanceof IBlockColor) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)block, block);
        } else {
            ModLogger.log(Level.ERROR, String.format(
                    "Tried to register block %s as colourable but it has no IBlockColor interface.",
                    block.getRegistryName().toString()));
        }
    }
    
    private void registerItemColorHandler(Item item) {
        if (item instanceof IItemColor) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor)item, item);
        } else {
            ModLogger.log(Level.ERROR, String.format(
                    "Tried to register item %s as colourable but it has no IItemColor interface.",
                    item.getRegistryName().toString()));
        }
    }
    
    private void registerRender(Block block) {
        registerRender(block, 0);
    }
    
    private void registerRender(Block block, int meta) {
        registerRender(Item.getItemFromBlock(block), meta);
    }
    
    private void registerRender(Item item) {
        registerRender(item, 0);
    }
    
    private void registerRender(Item item, int meta) {
        ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        imm.register(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
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
        Addons.initRenderers();
        if (HolidayHelper.valentins.isHolidayActive()) {
            enableValentinsClouds();
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
            //ModLogger.log("Shaders mod not found");
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
    }
    
    public static boolean useAttachedModelRender() {
        if (smartMovingLoaded) {
            return true;
        }
        return ConfigHandler.useAttachedModelRender;
    }
    
    public static boolean useSafeTextureRender() {
        if (shadersModLoaded) {
            return true;
        }
        if (ConfigHandler.skinTextureRenderOverride) {
            return true;
        }
        if (coloredLightsLoaded) {
            return true;
        }
        return false;
    }
    
    public static boolean useMultipassSkinRendering() {
        return ConfigHandler.multipassSkinRendering;
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
        //EntitySkinHandler.INSTANCE.receivedEquipmentData(equipmentData, entityId);
    }
    
    @Override
    public void receivedSkinFromLibrary(String fileName, Skin skin) {
        SkinIOUtils.saveSkinFromFileName(fileName + ".armour", skin);
        ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, skin.getSkinType()), LibraryFileType.LOCAL, null);
    }
}
