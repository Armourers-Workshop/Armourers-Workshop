package riskyken.armourersWorkshop.proxies;

import java.util.BitSet;

import net.minecraft.block.Block;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.ModForgeEventHandler;
import riskyken.armourersWorkshop.client.render.EquipmentItemRenderCache;
import riskyken.armourersWorkshop.client.render.EquipmentPlayerRenderCache;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.RenderBlockColourMixer;
import riskyken.armourersWorkshop.client.render.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.blocks.BlockColourMixer;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static int blockColourMixerRenderId;
    public static int renderPass;
    
    public EquipmentPlayerRenderCache equipmentRenderManager;
    
    @Override
    public void init() {
        equipmentRenderManager = new EquipmentPlayerRenderCache();
    }

    @Override
    public void initRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerBrain.class, new RenderBlockArmourer());
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
        blockColourMixerRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderBlockColourMixer());
    }

    @Override
    public void postInit() {
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModForgeEventHandler());
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.openCustomArmourGui);
    }
    
    @Override
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
        equipmentRenderManager.addCustomArmour(playerName, armourData);
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourType type) {
        equipmentRenderManager.removeCustomArmour(playerName, type);
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
        equipmentRenderManager.removeAllCustomArmourData(playerName);
    }

    @Override
    public int getPlayerModelCacheSize() {
        return equipmentRenderManager.getCacheSize();
    }

    @Override
    public void setPlayersNakedData(String playerName, boolean isNaked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        equipmentRenderManager.setPlayersSkinData(playerName, isNaked, skinColour, pantsColour, armourOverride, headOverlay);
    }

    @Override
    public PlayerSkinInfo getPlayersNakedData(String playerName) {
        return equipmentRenderManager.getPlayersNakedData(playerName);
    }

    @Override
    public int getRenderType(Block block) {
        if (block instanceof BlockColourMixer) {
            return blockColourMixerRenderId;
        }
        return 0;
    }

    @Override
    public void receivedEquipmentData(CustomArmourItemData equipmentData) {
        EquipmentItemRenderCache.receivedEquipmentData(equipmentData);
    }
}
