package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.customarmor.AbstractCustomArmour;
import riskyken.armourersWorkshop.common.customarmor.ArmourPart;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
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
        if (!(entity instanceof AbstractClientPlayer)) { return; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
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