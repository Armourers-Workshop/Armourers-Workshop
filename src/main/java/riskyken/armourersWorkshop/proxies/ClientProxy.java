package riskyken.armourersWorkshop.proxies;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.ModForgeEventHandler;
import riskyken.armourersWorkshop.client.abstraction.RenderBridge;
import riskyken.armourersWorkshop.client.handler.BlockHighlightRenderHandler;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.EquipmentItemRenderCache;
import riskyken.armourersWorkshop.client.render.EquipmentPlayerRenderCache;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.RenderBlockColourMixer;
import riskyken.armourersWorkshop.client.render.RenderBlockMannequin;
import riskyken.armourersWorkshop.client.render.RenderItemBowSkin;
import riskyken.armourersWorkshop.client.render.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.render.RenderItemMannequin;
import riskyken.armourersWorkshop.client.render.RenderItemSwordSkin;
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
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static int blockColourMixerRenderId;
    public static int renderPass;
    
    @Override
    public void preInit() {
        RenderBridge.init();
    }

    @Override
    public void initRenderers() {
        ModelMannequin modelMannequin = new ModelMannequin();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerBrain.class, new RenderBlockArmourer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMannequin.class, new RenderBlockMannequin(modelMannequin));
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.mannequin), new RenderItemMannequin(modelMannequin));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.doll), new RenderItemMannequin(modelMannequin));
        MinecraftForgeClient.registerItemRenderer(Items.wooden_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.stone_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.iron_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.golden_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.diamond_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.bow, new RenderItemBowSkin());
        blockColourMixerRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderBlockColourMixer());
        new BlockHighlightRenderHandler();
    }

    @Override
    public void init() {
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModForgeEventHandler());
    }
    
    @Override
    public void postInit() {
        Addons.initRenderers();
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.openCustomArmourGui);
        ClientRegistry.registerKeyBinding(Keybindings.undo);
    }
    
    @Override
    public void addEquipmentData(UUID playerId, EntityEquipmentData equipmentData) {
        EquipmentPlayerRenderCache.INSTANCE.addEquipmentData(playerId, equipmentData);
    }

    @Override
    public void removeEquipmentData(UUID playerId) {
        EquipmentPlayerRenderCache.INSTANCE.removeEquipmentData(playerId);
    }

    @Override
    public int getPlayerModelCacheSize() {
        return EquipmentPlayerRenderCache.INSTANCE.getCacheSize();
    }

    @Override
    public void setPlayersNakedData(UUID playerId, EntityNakedInfo nakedInfo) {
        EquipmentPlayerRenderCache.INSTANCE.setPlayersSkinData(playerId, nakedInfo);
    }

    @Override
    public PlayerSkinInfo getPlayersNakedData(UUID playerId) {
        return EquipmentPlayerRenderCache.INSTANCE.getPlayersNakedData(playerId);
    }

    @Override
    public int getRenderType(Block block) {
        if (block instanceof BlockColourMixer) {
            return blockColourMixerRenderId;
        }
        return 0;
    }

    @Override
    public void receivedEquipmentData(CustomEquipmentItemData equipmentData, byte target) {
        switch (target) {
        case 0:
            EquipmentItemRenderCache.receivedEquipmentData(equipmentData);
            break;
        case 1:
            EquipmentPlayerRenderCache.INSTANCE.receivedEquipmentData(equipmentData);
            break; 
        default:
            break;
        }
        
    }
}
