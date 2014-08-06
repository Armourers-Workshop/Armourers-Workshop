package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import cpw.mods.fml.client.registry.ClientRegistry;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourChestData;
import riskyken.armourersWorkshop.common.customarmor.AbstractCustomArmour;
import riskyken.armourersWorkshop.common.items.ItemCustomArmour;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

public class ClientProxy extends CommonProxy {
	
	public static HashMap<String, AbstractCustomArmour> customArmor = new HashMap<String, AbstractCustomArmour>();
	
	public static ModelCustomArmourChest customChest = new ModelCustomArmourChest();
	
	public static AbstractCustomArmour getPlayerCustomArmour(Entity entity, ArmourerType type) {
		if (!(entity instanceof AbstractClientPlayer)) { return null; }
		AbstractClientPlayer player = (AbstractClientPlayer) entity;
		if (!customArmor.containsKey(player.getDisplayName())) { return null; }
		
		AbstractCustomArmour armorData = customArmor.get(player.getDisplayName());
		
		if (armorData.getArmourType() != type) { return null; }
		
		return armorData;
	}
	
	public static void AddCustomArmour(Entity entity, ArmourerType type, AbstractCustomArmour armourData) {
		if (!(entity instanceof AbstractClientPlayer)) { return; }
		AbstractClientPlayer player = (AbstractClientPlayer) entity;
		if (customArmor.containsKey(player.getDisplayName())) {
			customArmor.remove(player.getDisplayName());
		}
		customArmor.put(player.getDisplayName(), armourData);
	}
	
	@Override
	public void init() {}
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourer.class, new RenderBlockArmourer());
	}
	
	@Override
	public void postInit() {}
	
}