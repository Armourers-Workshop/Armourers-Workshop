package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerChest;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

	public static Block armorerChest;
	
	public static void init()
	{
		armorerChest = new BlockArmourerChest();
	}
	
	public static void registerTileEntities() {
		registerTileEntity(TileEntityArmourerChest.class, LibBlockNames.ARMORER_CHEST);
	}
	
	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
	}
}
