package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    public static HashMap<String, CustomArmourItemData> customArmor = new HashMap<String, CustomArmourItemData>();

    public static ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public static ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public static ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public static ModelCustomArmourSkirt customSkirt = new ModelCustomArmourSkirt();
    public static ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static CustomArmourItemData getPlayerCustomArmour(Entity entity, ArmourerType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name();
        if (!customArmor.containsKey(key)) {
            return null;
        }

        CustomArmourItemData armorData = customArmor.get(key);
        if (armorData.getType() != type) { return null; }
        return armorData;
    }

    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerBrain.class, new RenderBlockArmourer());
    }

    @Override
    public void postInit() {
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
    }

    @Override
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
        String key = playerName + ":" + armourData.getType().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourerType type) {
        String key = playerName + ":" + type.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
        removeCustomArmour(playerName, ArmourerType.HEAD);
        removeCustomArmour(playerName, ArmourerType.CHEST);
        removeCustomArmour(playerName, ArmourerType.LEGS);
        removeCustomArmour(playerName, ArmourerType.SKIRT);
        removeCustomArmour(playerName, ArmourerType.FEET);
    }

    @Override
    public boolean playerHasSkirt(String playerName) {
        String key = playerName + ":" + ArmourerType.SKIRT.name();
        return customArmor.containsKey(key);
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (playerHasSkirt(player.getDisplayName())) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
            }
        }
    }
}