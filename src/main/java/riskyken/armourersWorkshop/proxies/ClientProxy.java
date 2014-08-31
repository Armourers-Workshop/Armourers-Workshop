package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.customarmor.ArmourPart;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    public static HashMap<String, CustomArmourData> customArmor = new HashMap<String, CustomArmourData>();

    public static ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public static ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public static ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public static ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static CustomArmourData getPlayerCustomArmour(Entity entity, ArmourerType type, ArmourPart part) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name() + ":" + part.name();
        if (!customArmor.containsKey(key)) {
            return null;
        }

        CustomArmourData armorData = customArmor.get(key);
        if (armorData.getArmourType() != type) {
            return null;
        }
        
        if (armorData.getArmourPart() != part) {
            return null;
        }
        
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
    }

    @Override
    public void addCustomArmour(String playerName, CustomArmourData armourData) {
        String key = playerName + ":" + armourData.getArmourType().name() + ":" + armourData.getArmourPart().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourerType type, ArmourPart part) {
        String key = playerName + ":" + type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
        removeCustomArmour(playerName, ArmourerType.HEAD, ArmourPart.HEAD);
        removeCustomArmour(playerName, ArmourerType.CHEST, ArmourPart.CHEST);
        removeCustomArmour(playerName, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        removeCustomArmour(playerName, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        removeCustomArmour(playerName, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        removeCustomArmour(playerName, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        removeCustomArmour(playerName, ArmourerType.LEGS, ArmourPart.SKIRT);
    }

    @Override
    public boolean playerHasSkirt(String playerName) {
        String key = playerName + ":" + ArmourerType.LEGS.name() + ":" + ArmourPart.SKIRT.name();
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