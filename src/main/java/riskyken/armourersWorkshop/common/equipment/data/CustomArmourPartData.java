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
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CustomArmourPartData {
    
    private static final String TAG_BLOCK_DATA = "blockData";
    private static final String TAG_PART = "part";
    
    private ArrayList<CustomEquipmentBlockData> armourData;
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
    
    public CustomArmourPartData(ArrayList armourData,
            EnumEquipmentPart part) {
        this.armourData = armourData;
        this.part = part;
    }

    public CustomArmourPartData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomArmourPartData(NBTTagCompound compound) {
        readFromNBT(compound);
    }

    public CustomArmourPartData(DataInputStream stream) throws IOException {
        readFromStream(stream);
    }

    public EnumEquipmentPart getArmourPart() {
        return this.part;
    }

    public ArrayList<CustomEquipmentBlockData> getArmourData() {
        return armourData;
    }
    
    private boolean blockCanBeSeen(ArrayList<CustomEquipmentBlockData> partBlocks, CustomEquipmentBlockData block) {
        int sidesCovered = 0;
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            for (int j = 0; j < partBlocks.size(); j++) {
                CustomEquipmentBlockData checkBlock = partBlocks.get(j);
                if (block.x + dir.offsetX == checkBlock.x &&
                        block.y + dir.offsetY == checkBlock.y &&
                        block.z + dir.offsetZ == checkBlock.z)
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
        armourData = new ArrayList<CustomEquipmentBlockData>();
        for (int i = 0; i < size; i++) {
            armourData.add(new CustomEquipmentBlockData(buf));
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_PART, (byte) part.ordinal());
        
        NBTTagList blockData = new NBTTagList();
        for (int i = 0; i < armourData.size(); i++) {
            CustomEquipmentBlockData data = armourData.get(i);
            NBTTagCompound dataNBT = new NBTTagCompound();
            data.writeToNBT(dataNBT);
            blockData.appendTag(dataNBT);
        }
        compound.setTag(TAG_BLOCK_DATA, blockData);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        part = EnumEquipmentPart.getOrdinal(compound.getByte(TAG_PART));
        
        NBTTagList blockData = compound.getTagList(TAG_BLOCK_DATA, NBT.TAG_COMPOUND);
        armourData = new ArrayList<CustomEquipmentBlockData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            armourData.add(new CustomEquipmentBlockData(data));
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(part.ordinal());
        stream.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream) throws IOException {
        part = EnumEquipmentPart.getOrdinal(stream.readByte());
        int size = stream.readInt();
        armourData = new ArrayList<CustomEquipmentBlockData>();
        for (int i = 0; i < size; i++) {
            armourData.add(new CustomEquipmentBlockData(stream));
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
        CustomArmourPartData other = (CustomArmourPartData) obj;
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
