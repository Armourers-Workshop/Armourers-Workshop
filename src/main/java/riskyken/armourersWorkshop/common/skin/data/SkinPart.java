package riskyken.armourersWorkshop.common.skin.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.renderer.GLAllocation;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.skin.data.ISkinPart;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.model.bake.ColouredVertexWithUV;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.cubes.LegacyCubeHelper;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinPart implements ISkinPart {
    
    private static final String TAG_BLOCK_DATA = "blockData";
    private static final String TAG_PART = "part";
    private static final String TAG_ID = "id";
    
    private ArrayList<ICube> armourData;
    private ISkinPartType skinPart;
    
    @SideOnly(Side.CLIENT)
    public ArrayList<ColouredVertexWithUV> normalVertexList;
    @SideOnly(Side.CLIENT)
    public ArrayList<ColouredVertexWithUV> glowingVertexList;
    
    public boolean hasNormalBlocks;
    public boolean hasGlowingBlocks;
    
    @SideOnly(Side.CLIENT)
    public int[] totalCubesInPart;
    @SideOnly(Side.CLIENT)
    public boolean displayNormalCompiled;
    @SideOnly(Side.CLIENT)
    public boolean displayGlowingCompiled;
    
    @SideOnly(Side.CLIENT)
    public int displayListNormal;
    @SideOnly(Side.CLIENT)
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
    
    public SkinPart(ArrayList armourData, ISkinPartType skinPart) {
        this.armourData = armourData;
        this.skinPart = skinPart;
    }

    public SkinPart(ByteBuf buf) {
        readFromBuf(buf);
    }

    public SkinPart(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        readFromStream(stream, version);
    }

    @Override
    public ISkinPartType getPartType() {
        return this.skinPart;
    }

    public ArrayList<ICube> getArmourData() {
        return armourData;
    }

    public void writeToBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skinPart.getRegistryName());
        buf.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(ByteBufUtils.readUTF8String(buf));
        int size = buf.readInt();
        armourData = new ArrayList<ICube>();
        for (int i = 0; i < size; i++) {
            byte id = buf.readByte();
            ICube cube;
            try {
                cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(id);
                cube.readFromBuf(buf);
                armourData.add(cube);
            } catch (InvalidCubeTypeException e) {
                ModLogger.log(Level.ERROR, "Unable to load skin. Unknown cube types found.");
                e.printStackTrace();
                return;
            }
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeUTF(skinPart.getRegistryName());
        stream.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        if (version < 6) {
            skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromLegacyId(stream.readByte());
        } else {
            skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(stream.readUTF());
        }
        int size = stream.readInt();
        armourData = new ArrayList<ICube>();
        for (int i = 0; i < size; i++) {
            ICube cube;
            if (version < 3) {
                cube = LegacyCubeHelper.loadlegacyCube(stream, version, skinPart);
            } else {
                byte id = stream.readByte();
                cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(id);
                cube.readFromStream(stream, version, skinPart);
            }
            armourData.add(cube);
        }
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
