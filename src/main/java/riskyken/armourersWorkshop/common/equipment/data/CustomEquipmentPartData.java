package riskyken.armourersWorkshop.common.equipment.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.common.equipment.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.equipment.cubes.ICube;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CustomEquipmentPartData {
    
    private static final String TAG_BLOCK_DATA = "blockData";
    private static final String TAG_PART = "part";
    private static final String TAG_ID = "id";
    
    private ArrayList<ICube> armourData;
    private EnumEquipmentPart part;
    
    public boolean facesBuild;
    
    public boolean hasNormalBlocks;
    public boolean hasGlowingBlocks;
    
    public boolean displayNormalCompiled;
    public boolean displayGlowingCompiled;
    
    public int displayListNormal;
    public int displayListGlowing;

    @SideOnly(Side.CLIENT)
    public void cleanUpDisplayLists() {
        if (this.displayNormalCompiled) {
            if (hasNormalBlocks) {
                GLAllocation.deleteDisplayLists(this.displayListNormal);
            }
        }
        if (this.displayGlowingCompiled) {
            if (hasGlowingBlocks) {
                GLAllocation.deleteDisplayLists(this.displayListGlowing);  
            }
        }
    }
    
    public CustomEquipmentPartData(ArrayList armourData,
            EnumEquipmentPart part) {
        this.armourData = armourData;
        this.part = part;
    }

    public CustomEquipmentPartData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomEquipmentPartData(NBTTagCompound compound) {
        readFromNBT(compound);
    }

    public CustomEquipmentPartData(DataInputStream stream, int version) throws IOException {
        readFromStream(stream, version);
    }

    public EnumEquipmentPart getArmourPart() {
        return this.part;
    }

    public ArrayList<ICube> getArmourData() {
        return armourData;
    }
    
    private boolean blockCanBeSeen(ArrayList<ICube> partBlocks, ICube block) {
        int sidesCovered = 0;
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            for (int j = 0; j < partBlocks.size(); j++) {
                ICube checkBlock = partBlocks.get(j);
                if (block.getX() + dir.offsetX == checkBlock.getX() &&
                        block.getY() + dir.offsetY == checkBlock.getY() &&
                        block.getZ() + dir.offsetZ == checkBlock.getZ())
                {
                    sidesCovered++;
                    break;
                }
            }
        }
        return sidesCovered < 6;
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(part.ordinal());
        buf.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        part = EnumEquipmentPart.getOrdinal(buf.readByte());
        int size = buf.readInt();
        armourData = new ArrayList<ICube>();
        for (int i = 0; i < size; i++) {
            byte id = buf.readByte();
            ICube cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(id);
            cube.readFromBuf(buf);
            armourData.add(cube);
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_PART, (byte) part.ordinal());
        
        NBTTagList blockData = new NBTTagList();
        for (int i = 0; i < armourData.size(); i++) {
            ICube data = armourData.get(i);
            NBTTagCompound dataNBT = new NBTTagCompound();
            data.writeToNBT(dataNBT);
            blockData.appendTag(dataNBT);
        }
        compound.setTag(TAG_BLOCK_DATA, blockData);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        part = EnumEquipmentPart.getOrdinal(compound.getByte(TAG_PART));
        
        NBTTagList blockData = compound.getTagList(TAG_BLOCK_DATA, NBT.TAG_COMPOUND);
        armourData = new ArrayList<ICube>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            byte id = data.getByte(TAG_ID);
            ICube cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(id);
            cube.readFromNBT(data);
            armourData.add(cube);
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(part.ordinal());
        stream.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream, int version) throws IOException {
        part = EnumEquipmentPart.getOrdinal(stream.readByte());
        int size = stream.readInt();
        armourData = new ArrayList<ICube>();
        for (int i = 0; i < size; i++) {
            ICube cube;
            
            if (version < 3) {
                cube = LegacyCubeHelper.loadlegacyCube(stream, version, part);
            } else {
                byte id = stream.readByte();
                cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(id);
                cube.readFromStream(stream, version, part);
            }
            
            armourData.add(cube);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < armourData.size(); i++) {
            result = prime * result + armourData.get(i).hashCode();
        }
        result = prime * result + ((part == null) ? 0 : part.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomEquipmentPartData other = (CustomEquipmentPartData) obj;
        if (armourData == null) {
            if (other.armourData != null)
                return false;
        } else if (!armourData.equals(other.armourData))
            return false;
        if (part != other.part)
            return false;
        return true;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < armourData.size(); i++) {
            result += armourData.get(i).toString();
        }
        return "CustomArmourPartData [armourData=" + armourData + "" + result;
    }
}
