package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.customarmor.AbstractCustomArmour;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    public static HashMap<String, AbstractCustomArmour> customArmor = new HashMap<String, AbstractCustomArmour>();

    public static ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public static ModelCustomArmourHead customHead = new ModelCustomArmourHead();

    public static AbstractCustomArmour getPlayerCustomArmour(Entity entity, ArmourerType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        if (!customArmor.containsKey(player.getDisplayName() + ":" + type.name())) {
            return null;
        }

        AbstractCustomArmour armorData = customArmor.get(player.getDisplayName() + ":" + type.name());

        if (armorData.getArmourType() != type) {
            return null;
        }

        return armorData;
    }

    public static void AddCustomArmour(Entity entity, ArmourerType type, AbstractCustomArmour armourData) {
        if (!(entity instanceof AbstractClientPlayer)) { return; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        if (customArmor.containsKey(player.getDisplayName() + ":" + type.name())) {
            customArmor.remove(player.getDisplayName() + ":" + type.name());
        }
        customArmor.put(player.getDisplayName() + ":" + type.name(), armourData);
    }
    
    public static void RemoveCustomArmour(Entity entity, ArmourerType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        if (customArmor.containsKey(player.getDisplayName() + ":" + type.name())) {
            customArmor.remove(player.getDisplayName() + ":" + type.name());
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