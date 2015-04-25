package riskyken.armourersWorkshop.common.skin.cubes;

import io.netty.buffer.ByteBuf;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.utils.ModLogger;

public class Cube implements ICube {

    protected static final String TAG_ID = "id";
    protected static final String TAG_X = "x";
    protected static final String TAG_Y = "y";
    protected static final String TAG_Z = "z";
    protected static final String TAG_COLOUR = "colour";
    
    protected byte id = -1;
    protected byte x;
    protected byte y;
    protected byte z;
    protected ICubeColour colour;
    protected BitSet faceFlags;
    
    public Cube() {
        this.colour = new CubeColour();
    }
    
    @Override
    public byte getX() {
        return x;
    }

    @Override
    public byte getY() {
        return y;
    }

    @Override
    public byte getZ() {
        return z;
    }
    
    @Override
    public void setX(byte x) {
        this.x = x;
    }
    
    @Override
    public void setY(byte y) {
        this.y = y;
    }
    
    @Override
    public void setZ(byte z) {
        this.z = z;
    }
    
    @Override
    public BitSet getFaceFlags() {
        return faceFlags;
    }
    
    @Override
    public void setFaceFlags(BitSet faceFlags) {
        this.faceFlags = faceFlags;
    }
    
    @Override
    public ICubeColour getCubeColour() {
        return colour;
    }
    
    @Override
    public int getColour() {
        return getColourSide(0);
    }
    
    @Override
    public int getColourSide(int side) {
        Color saveColour = new Color(colour.getRed(side) & 0xFF, colour.getGreen(side) & 0xFF, colour.getBlue(side) & 0xFF);
        return saveColour.getRGB();
    }
    
    @Override
    public void setColour(ICubeColour colour) {
        this.colour = colour;
    }
    
    @Override
    public void setColour(int colour) {
        this.colour.setColour(colour);
    }
    
    @Override
    public void setColour(int colour, int side) {
        this.colour.setColour(colour, side);
    }
    
    @Override
    public boolean isGlowing() {
        return false;
    }
    
    @Override
    public boolean needsPostRender() {
        return false;
    }
    
    @Override
    public void setId(byte id) {
        if (this.id != -1) {
            ModLogger.log(Level.WARN, "Resetting cube id.");
        }
        this.id = id;
    }
    
    @Override
    public byte getId() {
        return id;
    }
    
    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
        colour.writeToBuf(buf);
    }
    
    @Override
    public void readFromBuf(ByteBuf buf) {
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
        colour = new CubeColour(buf);
    }
    
    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(id);
        stream.writeByte(x);
        stream.writeByte(y);
        stream.writeByte(z);
        colour.writeToStream(stream);
    }
    
    @Override
    public void readFromStream(DataInputStream stream, int version, ISkinPartType skinPart) throws IOException {
        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        if (version < 7) {
            colour = new CubeColour(stream.readInt());
        } else {
            colour = new CubeColour(stream, version);
        }
    }
    
    @Override
    public String toString() {
        return "CustomEquipmentBlockData [x=" + x + ", y=" + y + ", z=" + z
        + ", colour=" + colour + ", blockType=" + id + "]";
    }
}
