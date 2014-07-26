package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.utils.ModLogger;

public class TileEntityArmourerChest extends TileEntity {

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	public void buildArmourItem() {
		
		for (int ix = 0; ix < 10; ix++) {
			for (int iy = 0; iy < 12; iy++) {
				for (int iz = 0; iz < 6; iz++) {
					if (ix == 0 | ix == 9 | iz == 0 | iz == 5) {
						worldObj.setBlock(xCoord + ix - 8, yCoord + iy, zCoord + iz + 1, Blocks.glass);
					}
				}
			}
		}
		
		ModLogger.log("click");
	}
	
}
