package riskyken.armourersWorkshop.common.skin.cubes;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;

public interface ICube {
    
    public byte getX();
    
    public byte getY();
    
    public byte getZ();
    
    public void setX(byte x);
    
    public void setY(byte y);
    
    public void setZ(byte z);
    
    public BitSet getFaceFlags();
    
    public void setFaceFlags(BitSet faceFlags);
    
    public ICubeColour getCubeColour();
    
    public int getColour();
    
    public int getColourSide(int side);
    
    public void setColour(ICubeColour colour);
    
    public void setColour(int colour);
    
    public void setColour(int colour, int side);
    
    /** Will this cube glow in the dark? */
    public boolean isGlowing();
    
    /** Should this cube be rendered after the world? */
    public boolean needsPostRender();
    
    /** Set the cubes ID */
    public void setId(byte id);
    
    /** Get the cubes ID */
    public byte getId();
    
    public void writeToBuf(ByteBuf buf);
    
    public void readFromBuf(ByteBuf buf);
    
    public void writeToStream(DataOutputStream stream) throws IOException;
    
    public void readFromStream(DataInputStream stream, int version, ISkinPartType skinPart) throws IOException;
}
