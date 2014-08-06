package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.common.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourChestData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;

public class TileEntityArmourer extends TileEntity {

	private static final String TAG_TYPE = "type";
	private ArmourerType type;
	
	public TileEntityArmourer() {}
	
	public TileEntityArmourer(ArmourerType type) {
		this.type = type;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	public void buildArmourItem(EntityPlayer player) {
		
		ArrayList<ArmourBlockData> armourBlockData = new ArrayList<ArmourBlockData>();
		
		for (int ix = 0; ix < 10; ix++) {
			for (int iy = 0; iy < 12; iy++) {
				for (int iz = 0; iz < 6; iz++) {
					if (ix == 0 | ix == 9 | iz == 0 | iz == 5) {
						if (!worldObj.isAirBlock(xCoord + ix - 8, yCoord + iy, zCoord + iz + 3)) {
							if (worldObj.getBlock(xCoord + ix - 8, yCoord + iy, zCoord + iz + 3) == Blocks.wool) {
								int colour = worldObj.getBlockMetadata(xCoord + ix - 8, yCoord + iy, zCoord + iz + 3);
								colour = UtilColour.getMinecraftColor(colour);
								ArmourBlockData blockData = new ArmourBlockData(3 - (ix - 1), 12 - (iy + 1), 2 - (-iz + 5), colour);
								armourBlockData.add(blockData);
								ModLogger.log(blockData);
							}
							
						}
						//worldObj.setBlock(xCoord + ix - 8, yCoord + iy, zCoord + iz + 3, Blocks.glass);
					}
				}
			}
		}
		
		ModLogger.log("");
		
		if (armourBlockData.size() > 0) {
			CustomArmourChestData armourData = new CustomArmourChestData(armourBlockData);
			ClientProxy.AddCustomArmour(player, ArmourerType.CHEST, armourData);
		}
	}
	
	public ArmourerType getType() {
		return type;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		type = ArmourerType.getOrdinal(compound.getInteger(TAG_TYPE));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger(TAG_TYPE, type.ordinal());
	}
}
