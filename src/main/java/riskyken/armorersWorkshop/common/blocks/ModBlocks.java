package riskyken.armorersWorkshop.common.blocks;

import riskyken.armorersWorkshop.common.lib.LibBlockNames;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

	//public static Block testBlock;
	
	public static void init()
	{
		//testBlock = new BlockTestBlock();
	}
	
	public static void registerTileEntities() {
		//registerTileEntity(TileEntityTestBlock.class, LibBlockNames.TEST_BLOCK);
	}
	
	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
	}
}
