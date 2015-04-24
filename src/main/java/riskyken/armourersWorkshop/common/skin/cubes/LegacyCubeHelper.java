package riskyken.armourersWorkshop.common.skin.cubes;

import java.io.DataInputStream;
import java.io.IOException;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;

public final class LegacyCubeHelper {

    public static ICube loadlegacyCube(DataInputStream stream, int version, ISkinPartType skinPart) throws IOException, InvalidCubeTypeException {
        byte x;
        byte y;
        byte z;
        int colour;
        byte blockType;
        
        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        colour = stream.readInt();
        blockType = stream.readByte();
        
        if (version < 2) {
            String partName = skinPart.getRegistryName();
            if (partName.equals("armourers:sword.base")) {
                y -= 1;
            } else if (partName.equals("armourers:skirt.base")) {
                y -= 1;
            } else if (partName.equals("armourers:legs.leftLeg")) {
                y -= 1;
            } else if (partName.equals("armourers:legs.rightLeg")) {
                y -= 1;
            } else if (partName.equals("armourers:feet.leftFoot")) {
                y -= 1;
            } else if (partName.equals("armourers:feet.rightFoot")) {
                y -= 1;
            }
        }

        ICube cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(blockType);
        cube.setX(x);
        cube.setY(y);
        cube.setZ(z);
        cube.setColour(colour);
        
        return cube;
    }

}
