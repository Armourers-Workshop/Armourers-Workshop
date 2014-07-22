package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.common.registry.GameRegistry;

public abstract class AbstractModItem extends Item {

	public AbstractModItem(String name) {
		setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
		setUnlocalizedName(name);
		setHasSubtypes(false);
		setNoRepair();
	}
	
	@Override
	public Item setUnlocalizedName(String name) {
		GameRegistry.registerItem(this, "item." + LibModInfo.ID.toLowerCase() + ":" + name);
		return super.setUnlocalizedName(name);
	}
	
	@Override
	public String getUnlocalizedName() {
		return getModdedUnlocalizedName(super.getUnlocalizedName());
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return getModdedUnlocalizedName(super.getUnlocalizedName(itemStack));
	}
	
	protected String getModdedUnlocalizedName(String unlocalizedName) {
		String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
		return "item." + LibModInfo.ID.toLowerCase() + ":" + name;
	}
}
