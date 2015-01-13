package riskyken.armourersWorkshop.proxies;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.abstraction.RenderBridge;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.handler.BlockHighlightRenderHandler;
import riskyken.armourersWorkshop.client.handler.DebugTextHandler;
import riskyken.armourersWorkshop.client.handler.PlayerSkinHandler;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.client.render.block.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.block.RenderBlockColourMixer;
import riskyken.armourersWorkshop.client.render.block.RenderBlockGlowing;
import riskyken.armourersWorkshop.client.render.block.RenderBlockMannequin;
import riskyken.armourersWorkshop.client.render.block.RenderBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.item.RenderItemBlockMiniArmourer;
import riskyken.armourersWorkshop.client.render.item.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemMannequin;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.blocks.BlockColourMixer;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.EntityNakedInfo;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static boolean shadersModLoaded;
    public static int blockColourMixerRenderId;
    public static int renderPass;
    
    @Override
    public void preInit() {
        RenderBridge.init();
    }

    @Override
    public void initRenderers() {
        EquipmentModelRenderer.init();
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
    }

    @Override
    public void init() {
        PlayerSkinHandler.init();
        ClientEquipmentModelCache.init();
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new DebugTextHandler());
    }
    
    @Override
    public void postInit() {
        Addons.initRenderers();
        try {
            Class.forName("shadersmodcore.client.Shaders");
            ModLogger.log("Shaders mod support active");
            shadersModLoaded = true;
        } catch (Exception e) {
            ModLogger.log("Shaders mod not found");
        }
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.openCustomArmourGui);
        ClientRegistry.registerKeyBinding(Keybindings.undo);
    }
    
    @Override
    public void addEquipmentData(UUID playerId, EntityEquipmentData equipmentData) {
        EquipmentModelRenderer.INSTANCE.addEquipmentData(playerId, equipmentData);
    }

    @Override
    public void removeEquipmentData(UUID playerId) {
        EquipmentModelRenderer.INSTANCE.removeEquipmentData(playerId);
    }

    @Override
    public int getPlayerModelCacheSize() {
        return ClientEquipmentModelCache.INSTANCE.getCacheSize();
    }

    @Override
    public void setPlayersNakedData(UUID playerId, EntityNakedInfo nakedInfo) {
        PlayerSkinHandler.INSTANCE.setPlayersSkinData(playerId, nakedInfo);
    }

    @Override
    public PlayerSkinInfo getPlayersNakedData(UUID playerId) {
        return PlayerSkinHandler.INSTANCE.getPlayersNakedData(playerId);
    }

    @Override
    public int getRenderType(Block block) {
        if (block instanceof BlockColourMixer) {
            return blockColourMixerRenderId;
        }
        return 0;
    }

    @Override
    public void receivedEquipmentData(CustomEquipmentItemData equipmentData) {
        ClientEquipmentModelCache.INSTANCE.receivedEquipmentData(equipmentData);
    }
}
