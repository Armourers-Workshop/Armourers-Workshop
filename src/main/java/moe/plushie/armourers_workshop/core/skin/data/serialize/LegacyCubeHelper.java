package moe.plushie.armourers_workshop.core.skin.data.serialize;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;

import java.io.DataInputStream;
import java.io.IOException;

public final class LegacyCubeHelper {

    // Used by file versions less than 10
    public static void loadLegacyCubeData(SkinCubeData cubeData, int index, DataInputStream input, int version, ISkinPartType skinPart) throws IOException {
        if (version < 3) {
            loadLegacyCube(cubeData, index, input, version, skinPart);
            return;
        }
        byte id = input.readByte();
        byte x = input.readByte();
        byte y = input.readByte();
        byte z = input.readByte();
        byte[] r = new byte[6];
        byte[] g = new byte[6];
        byte[] b = new byte[6];
        if (version < 7) {
            int colour = input.readInt();
            for (int i = 0; i < 6; i++) {
                r[i] = (byte) (colour >> 16 & 0xff);
                g[i] = (byte) (colour >> 8 & 0xff);
                b[i] = (byte) (colour & 0xff);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                r[i] = input.readByte();
                g[i] = input.readByte();
                b[i] = input.readByte();
            }
        }
        cubeData.setCubeId(index, id);
        cubeData.setCubeLocation(index, x, y, z);
        for (int i = 0; i < 6; i++) {
            cubeData.setCubeColour(index, i, r[i], g[i], b[i]);
        }
    }

    // Used by file versions less than 3
    public static void loadLegacyCube(SkinCubeData cubeData, int index, DataInputStream stream, int version, ISkinPartType skinPart) throws IOException {
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
            if (skinPart == SkinPartTypes.ITEM_SWORD) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPED_SKIRT) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPED_LEFT_LEG) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPED_RIGHT_LEG) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPED_LEFT_FOOT) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPED_RIGHT_FOOT) {
                y -= 1;
            }
        }

        cubeData.setCubeId(index, blockType);
        cubeData.setCubeLocation(index, x, y, z);
        byte r = (byte) (colour >> 16 & 0xff);
        byte g = (byte) (colour >> 8 & 0xff);
        byte b = (byte) (colour & 0xff);
        for (int i = 0; i < 6; i++) {
            cubeData.setCubeColour(index, i, r, g, b);
        }
    }
}
