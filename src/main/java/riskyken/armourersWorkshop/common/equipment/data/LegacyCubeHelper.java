package riskyken.armourersWorkshop.common.equipment.data;

import java.io.DataInputStream;
import java.io.IOException;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.common.equipment.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.equipment.cubes.ICube;

public final class LegacyCubeHelper {

    public static ICube loadlegacyCube(DataInputStream stream, int version, EnumEquipmentPart part) throws IOException {
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
            switch (part) {
            case WEAPON:
                y -= 1;
                break;
            case SKIRT:
                y -= 1;
                break;
            case LEFT_LEG:
                y -= 1;
                break;
            case RIGHT_LEG:
                y -= 1;
                break;
            case LEFT_FOOT:
                y -= 1;
                break;
            case RIGHT_FOOT:
                y -= 1;
                break;
            default:
                break;
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
