package riskyken.armorersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armorersWorkshop.common.lib.LibBlockNames;
import riskyken.armorersWorkshop.common.tileentities.TileEntityArmorerChest;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

	public static Block armorerChest;
	
	public static void init()
	{
		armorerChest = new BlockArmorerChest();
	}
	
	public static void registerTileEntities() {
		registerTileEntity(TileEntityArmorerChest.class, LibBlockNames.ARMORER_CHEST);
	}
	
	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
	}
}
