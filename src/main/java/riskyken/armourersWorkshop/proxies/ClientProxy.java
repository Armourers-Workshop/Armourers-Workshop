package riskyken.armourersWorkshop.proxies;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.handler.BlockHighlightRenderHandler;
import riskyken.armourersWorkshop.client.handler.DebugTextHandler;
import riskyken.armourersWorkshop.client.handler.ItemTooltipHandler;
import riskyken.armourersWorkshop.client.handler.PlayerSkinHandler;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.client.render.block.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.block.RenderBlockColourMixer;
import riskyken.armourersWorkshop.client.render.block.RenderBlockGlowing;
import riskyken.armourersWorkshop.client.render.block.RenderBlockMannequin;
import riskyken.armourersWorkshop.client.render.block.RenderBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.entity.EntitySkinRenderHandler;
import riskyken.armourersWorkshop.client.render.item.RenderItemBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.item.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemMannequin;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.blocks.BlockColourMixer;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.minecraftWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static boolean shadersModLoaded;
    public static boolean moreplayermodelsLoaded;
    public static int blockColourMixerRenderId;
    public static int renderPass;
    
    @Override
    public void preInit() {
        RenderBridge.init();
    }

    @Override
    public void initRenderers() {
        EquipmentModelRenderer.init();
        EntitySkinRenderHandler.init();
        ModelMannequin modelMannequin = new ModelMannequin();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerBrain.class, new RenderBlockArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMannequin.class, new RenderBlockMannequin());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMiniArmourer.class, new RenderBlockMiniArmourer());
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.miniArmourer), new RenderItemBlockMiniArmourer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.mannequin), new RenderItemMannequin(modelMannequin));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.doll), new RenderItemMannequin(modelMannequin));
        blockColourMixerRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderBlockColourMixer());
        RenderingRegistry.registerBlockHandler(new RenderBlockGlowing(RenderingRegistry.getNextAvailableRenderId()));
        new BlockHighlightRenderHandler();
        new ItemTooltipHandler();
    }

    @Override
    public void init() {
        PlayerSkinHandler.init();
        ClientModelCache.init();
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new DebugTextHandler());
        //Cross mod support
        try {
            Class.forName("shadersmodcore.client.Shaders");
            ModLogger.log("Shaders mod support active");
            shadersModLoaded = true;
        } catch (Exception e) {
            //ModLogger.log("Shaders mod not found");
        }
        if (Loader.isModLoaded("moreplayermodels")) {
            moreplayermodelsLoaded = true;
            ConfigHandler.compatibilityRender = true;
            ModLogger.log("More Player Models support active");
        }
    }
    
    @Override
    public void postInit() {
        Addons.initRenderers();
        EntitySkinRenderHandler.INSTANCE.initRenderer();
        spamSillyMessages();
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
        ClientRegistry.registerKeyBinding(Keybindings.openCustomArmourGui);
        ClientRegistry.registerKeyBinding(Keybindings.undo);
    }
    
    @Override
    public void addEquipmentData(PlayerPointer playerPointer, EntityEquipmentData equipmentData) {
        EquipmentModelRenderer.INSTANCE.addEquipmentData(playerPointer, equipmentData);
    }

    @Override
    public void removeEquipmentData(PlayerPointer playerPointer) {
        EquipmentModelRenderer.INSTANCE.removeEquipmentData(playerPointer);
    }

    @Override
    public int getPlayerModelCacheSize() {
        return ClientModelCache.INSTANCE.getCacheSize();
    }

    @Override
    public void setPlayersNakedData(PlayerPointer playerPointer, EntityNakedInfo nakedInfo) {
        PlayerSkinHandler.INSTANCE.setPlayersSkinData(playerPointer, nakedInfo);
    }

    @Override
    public PlayerSkinInfo getPlayersNakedData(PlayerPointer playerPointer) {
        return PlayerSkinHandler.INSTANCE.getPlayersNakedData(playerPointer);
    }

    @Override
    public int getRenderType(Block block) {
        if (block instanceof BlockColourMixer) {
            return blockColourMixerRenderId;
        }
        return 0;
    }

    @Override
    public void receivedEquipmentData(Skin skin) {
        ModelBakery.INSTANCE.receivedUnbakedModel(skin);
    }
    
    @Override
    public void receivedCommandFromSever(CommandType command) {
        ClientModelCache.INSTANCE.clearCache();
    }
    
    @Override
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        EntitySkinHandler.INSTANCE.receivedEquipmentData(equipmentData, entityId);
    }
    
    @Override
    public void receivedSkinFromLibrary(String fileName, Skin skin) {
        SkinIOUtils.saveSkinFromFileName(fileName, skin);
    }
}
