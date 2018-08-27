package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.data.MiniCube;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class TileEntityMiniArmourer extends AbstractTileEntityInventory {

    private static final String TAG_TYPE = "type";
    private static final int INVENTORY_SIZE = 2;
    
    @SideOnly(Side.CLIENT)
    public int red;
    @SideOnly(Side.CLIENT)
    public int green;
    @SideOnly(Side.CLIENT)
    public int blue;

    private ISkinType skinType;
    private ArrayList<SkinPart> skinParts;
    
    /*
     * Around 1.65MB for the 20*62*56 sword skin, may want to change this if skins get any bigger.
     * Thats around 4.96MB for the bow skin, the biggest skin so far.
     * 
     * Should have an array with each skin part then an array when each part layer.
     */
    
    public TileEntityMiniArmourer() {
        super(INVENTORY_SIZE);
        this.skinParts = new ArrayList<SkinPart>();
        setSkinType(SkinTypeRegistry.skinHead, false);
    }
    
    public void cubeUpdateFromServer(ISkinPartType skinPartType, MiniCube cube, boolean remove) {
        /*
        for (int i = 0; i < skinParts.size(); i++) {
            if (skinParts.get(i).getPartType() == skinPartType) {
                ArrayList<ICube> cubeData = skinParts.get(i).getArmourData();
                for (int j = 0; j < cubeData.size(); j++) {
                    ICube curCube = cubeData.get(j);
                    if (curCube.getX() == cube.getX() & curCube.getY() == cube.getY() & curCube.getZ() == cube.getZ()) {
                        cubeData.remove(j);
                        break;
                    }
                }
                if (!remove) {
                    cubeData.add(cube);
                }
                return;
            }
        }
        */
    }
    
    public ISkinType getSkinType() {
        return skinType;
    }
    
    public ArrayList<SkinPart> getSkinParts() {
        return skinParts;
    }
    
    public void setSkinParts(ArrayList<SkinPart> skinParts) {
        this.skinParts = skinParts;
    }
    
    public void setSkinType(ISkinType skinType) {
        if (skinType != this.skinType) {
            setSkinType(skinType, true);
        }
    }
    
    public void setSkinType(ISkinType skinType, boolean update) {
        this.skinType = skinType;
        this.skinParts.clear();
        if (this.skinType != null) {
            /*
            ArrayList<ISkinPartType> skinPartTypes = this.skinType.getSkinParts();
            for (int i = 0; i <skinPartTypes.size(); i++) {
                SkinPart skinPart = new SkinPart(skinPartTypes.get(i));
                skinParts.add(skinPart);
            }
            */
        }
        if (update) {
            this.markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(compound.getString(TAG_TYPE));
        if (skinType != null) {
            setSkinType(skinType, false);
            /*
            for (int i = 0; i < skinParts.size(); i++) {
                SkinPart skinPart = skinParts.get(i);
                String partName = skinPart.getPartType().getRegistryName();
                if (compound.hasKey(partName)) {
                    NBTTagCompound partCompound = compound.getCompoundTag(partName);
                    try {
                        skinPart.readFromCompound(partCompound);
                    } catch (InvalidCubeTypeException e) {
                        e.printStackTrace();
                        setSkinType(null, false);
                        return;
                    }
                }
            }
            */
        }
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (skinType != null) {
            compound.setString(TAG_TYPE, skinType.getRegistryName());
            /*
            for (int i = 0; i < skinParts.size(); i++) {
                NBTTagCompound partCompound = new NBTTagCompound();
                SkinPart skinPart = skinParts.get(i);
                skinPart.writeToCompound(partCompound);
                compound.setTag(skinPart.getPartType().getRegistryName(), partCompound);
            }
            */
        }
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(
                xCoord, yCoord, zCoord,
                xCoord + 1, yCoord + 2, zCoord + 1
                );
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.MINI_ARMOURER;
    }
}
