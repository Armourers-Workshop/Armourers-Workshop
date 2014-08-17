package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourChestData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;

public class TileEntityArmourerBrain extends TileEntity {

    private static final String TAG_TYPE = "type";
    private static final int MULTI_BLOCK_SIZE = 22;
    private ArmourerType type;
    
    public TileEntityArmourerBrain() {
        this.type = ArmourerType.CHEST;
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
    
    public void buildArmourItem(EntityPlayer player) {
        ArrayList<ArmourBlockData> armourBlockData = new ArrayList<ArmourBlockData>();
        ModLogger.log("");
        for (int ix = 0; ix < 14; ix++) {
            for (int iy = 0; iy < 12; iy++) {
                for (int iz = 0; iz < 10; iz++) {
                    addArmourToList(xCoord + ix - 10, yCoord + iy, zCoord + iz + 1, ix, iy, iz, armourBlockData);
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            ModLogger.log("setting armour data size " + armourBlockData.size());
            CustomArmourChestData armourData = new CustomArmourChestData(armourBlockData);
            ClientProxy.AddCustomArmour(player, ArmourerType.CHEST, armourData);
        }
    }
    
    private void addArmourToList(int x, int y, int z, int ix, int iy, int iz, ArrayList<ArmourBlockData> list) {
        if (worldObj.isAirBlock(x, y, z)) { return; }
        Block block = worldObj.getBlock(x, y, z);
        
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = getColourFromTileEntity(x, y, z);
            ArmourBlockData blockData = new ArmourBlockData(3 - (ix - 3), 12 - (iy + 1), 2 - (-iz + 7), colour, block == ModBlocks.colourableGlowing);
            list.add(blockData);
            ModLogger.log(blockData);
        }
    }
    
    private int getColourFromTileEntity(int x, int y, int z) {
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te != null & te instanceof IColourable) {
            return ((IColourable)te).getColour();
        }
        return UtilColour.getMinecraftColor(0);
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
