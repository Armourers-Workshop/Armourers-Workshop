package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
            worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
        }
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), compound);
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
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
        return compound;
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos());
    }

    @Override
    public String getName() {
        return LibBlockNames.MINI_ARMOURER;
    }
}
