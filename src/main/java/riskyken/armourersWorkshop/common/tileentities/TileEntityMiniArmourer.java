package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.skin.cubes.Cube;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMiniArmourer extends AbstractTileEntityInventory {

    private static final String TAG_TYPE = "type";
    
    @SideOnly(Side.CLIENT)
    public int red;
    @SideOnly(Side.CLIENT)
    public int green;
    @SideOnly(Side.CLIENT)
    public int blue;

    private ISkinType skinType;
    private ArrayList<SkinPart> skinParts;
    
    public TileEntityMiniArmourer() {
        this.items = new ItemStack[2];
        this.skinParts = new ArrayList<SkinPart>();
    }
    
    public ISkinType getSkinType() {
        return skinType;
    }
    
    public ArrayList<SkinPart> getSkinParts() {
        return skinParts;
    }
    
    public void setSkinParts(ArrayList<SkinPart> skinParts) {
        ModLogger.log("setting parts");
        this.skinParts = skinParts;
    }
    
    public void setSkinType(ISkinType skinType) {
        this.skinType = skinType;
        this.skinParts.clear();
        if (this.skinType != null) {
            
            ArrayList<ISkinPartType> skinPartTypes = this.skinType.getSkinParts();
            for (int i = 0; i <skinPartTypes.size(); i++) {
                SkinPart skinPart = new SkinPart(skinPartTypes.get(i));
                skinParts.add(skinPart);
            }
            Cube cube = new Cube();
            cube.setY((byte) -1);
            cube.setId((byte) 0);
            skinParts.get(0).getArmourData().add(cube);
        }
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (skinType != null) {
            compound.setString(TAG_TYPE, skinType.getRegistryName());
        }
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.MINI_ARMOURER;
    }
}
