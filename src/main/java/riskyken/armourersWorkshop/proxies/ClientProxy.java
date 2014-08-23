package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.customarmor.AbstractCustomArmour;
import riskyken.armourersWorkshop.common.customarmor.ArmourPart;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    public static HashMap<String, AbstractCustomArmour> customArmor = new HashMap<String, AbstractCustomArmour>();

    public static ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public static ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public static ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    
    public static AbstractCustomArmour getPlayerCustomArmour(Entity entity, ArmourerType type, ArmourPart part) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name() + ":" + part.name();
        if (!customArmor.containsKey(key)) {
            return null;
        }

        AbstractCustomArmour armorData = customArmor.get(key);
        if (armorData.getArmourType() != type) {
            return null;
        }
        
        if (armorData.getArmourPart() != part) {
            return null;
        }
        
        return armorData;
    }

    public static void AddCustomArmour(Entity entity, AbstractCustomArmour armourData) {
        if (!(entity instanceof AbstractClientPlayer)) { return; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getDisplayName() + ":" + armourData.getArmourType().name() + ":" + armourData.getArmourPart().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
    }
    
    public static void RemoveCustomArmour(Entity entity, ArmourerType type, ArmourPart part) {
        if (!(entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
    }
    
    public static void RemoveAllCustomArmourData(Entity entity) {
        if (!(entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        ModLogger.log("Removing custom armour for " + player.getDisplayName());
        RemoveCustomArmour(player, ArmourerType.HEAD, ArmourPart.HEAD);
        RemoveCustomArmour(player, ArmourerType.CHEST, ArmourPart.CHEST);
        RemoveCustomArmour(player, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        RemoveCustomArmour(player, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        RemoveCustomArmour(player, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        RemoveCustomArmour(player, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        RemoveCustomArmour(player, ArmourerType.LEGS, ArmourPart.SKIRT);
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
}