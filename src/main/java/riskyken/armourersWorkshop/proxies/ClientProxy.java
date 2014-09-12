package riskyken.armourersWorkshop.proxies;

import net.minecraftforge.client.MinecraftForgeClient;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.render.CustomEquipmentRenderManager;
import riskyken.armourersWorkshop.client.render.ItemModelRenderManager;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
    
    CustomEquipmentRenderManager equipmentRenderManager;
    
    @Override
    public void init() {
        equipmentRenderManager = new CustomEquipmentRenderManager();
        ItemModelRenderManager.init();
    }

    @Override
    public void initRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerBrain.class, new RenderBlockArmourer());
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
    }

    @Override
    public void postInit() {
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
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
}
