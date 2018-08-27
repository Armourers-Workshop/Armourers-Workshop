package riskyken.armourersWorkshop.common.data;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;

public class MiniCube implements IPantable {
    
    private ICube type;
    private byte x;
    private byte y;
    private byte z;
    private CubeColour cc = new CubeColour();
    
    public MiniCube(ICube type) {
        this.type = type;
    }
    
    public MiniCube(ByteBuf buf) {
        this.type = CubeRegistry.INSTANCE.getCubeFormId(buf.readByte());
        readFromBuf(buf);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void setColour(int colour) {
        cc.setColour(colour);
    }

    public ICubeColour getCubeColour() {
        return cc;
    }

    public boolean isGlowing() {
        return false;
    }

    public int getZ() {
        return z;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setY(byte b) {
        y = b;
        
    }

    public void setX(byte b) {
        x = b;
    }
    
    public void setZ(byte b) {
        z = b;
    }
    
    @Override
    public void setColour(int colour, int side) {
        cc.setColour(colour, side);
    }
    
    @Override
    public void setColour(byte[] rgb, int side) {
        cc.setRed(rgb[0], side);
        cc.setGreen(rgb[1], side);
        cc.setBlue(rgb[2], side);
    }

    @Override
    public void setColour(ICubeColour cubeColour) {
        cc = new CubeColour(cubeColour);
    }

    @Override
    public int getColour(int side) {
        return cc.getColour(side);
    }

    @Override
    public void setPaintType(PaintType paintType, int side) {
        cc.setPaintType((byte) paintType.getKey(), side);
    }

    @Override
    public PaintType getPaintType(int side) {
        return PaintType.getPaintTypeFormSKey(cc.getPaintType(side));
    }

    @Override
    public ICubeColour getColour() {
        return cc;
    }
    
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
        NBTTagCompound compound = new NBTTagCompound();
        cc.writeToNBT(compound);
        ByteBufUtils.writeTag(buf, compound);
    }

    private void readFromBuf(ByteBuf buf) {
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
        NBTTagCompound compound = ByteBufUtils.readTag(buf);
        cc.readFromNBT(compound);
    }
}
