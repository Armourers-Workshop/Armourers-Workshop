package riskyken.armourersWorkshop.common.items.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ModItemBlockWithMetadata extends ItemBlockWithMetadata {
	
	public ModItemBlockWithMetadata(Block block) {
		super(block, block);
	}
	
	@Override
	public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
		return super.getUnlocalizedNameInefficiently(par1ItemStack);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return field_150939_a.getUnlocalizedName() + itemstack.getItemDamage();
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List par3List, boolean par4) {
		String unlocalized;
		String localized;
		
		unlocalized = itemStack.getUnlocalizedName() + ".lore";
		localized= StatCollector.translateToLocal(unlocalized);
		if (!unlocalized.equals(localized)) {
			par3List.add(localized);
		}
		
		unlocalized = field_150939_a.getUnlocalizedName() + ".lore";
		localized= StatCollector.translateToLocal(unlocalized);
		if (!unlocalized.equals(localized)) {
			par3List.add(localized);
		}
		
		for (int i = 0; i < 10; i++) {
			unlocalized = itemStack.getUnlocalizedName() + ".lore" + i;
			localized= StatCollector.translateToLocal(unlocalized);
			if (!unlocalized.equals(localized)) {
				par3List.add(localized);
			} else {
				break;
			}
		}
		
		super.addInformation(itemStack, player, par3List, par4);
	}
}
