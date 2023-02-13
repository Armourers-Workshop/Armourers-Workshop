package moe.plushie.armourers_workshop.core.skin.data.serialize;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV1;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;

import java.io.IOException;

public final class LegacyCubeHelper {

    // Used by file versions less than 10
    public static void loadLegacyCubeData(SkinCubesV1 cubeData, SkinCubesV1.BufferSlice slice, IDataInputStream input, int version, ISkinPartType skinPart) throws IOException {
        if (version < 3) {
            loadLegacyCube(cubeData, slice, input, version, skinPart);
            return;
        }
        slice.setId(input.readByte());
        slice.setX(input.readByte());
        slice.setY(input.readByte());
        slice.setZ(input.readByte());
        if (version < 7) {
            int color = input.readInt();
            byte r = (byte) (color >> 16 & 0xff);
            byte g = (byte) (color >> 8 & 0xff);
            byte b = (byte) (color & 0xff);
            for (int i = 0; i < 6; i++) {
                slice.setR(i, r);
                slice.setG(i, g);
                slice.setB(i, b);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                slice.setR(i, input.readByte());
                slice.setG(i, input.readByte());
                slice.setB(i, input.readByte());
            }
        }
    }

    // Used by file versions less than 3
    public static void loadLegacyCube(SkinCubesV1 cubeData, SkinCubesV1.BufferSlice slice, IDataInputStream stream, int version, ISkinPartType skinPart) throws IOException {
        byte x;
        byte y;
        byte z;
        int color;
        byte blockType;

        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        color = stream.readInt();
        blockType = stream.readByte();

        if (version < 2) {
            if (skinPart == SkinPartTypes.ITEM_SWORD) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_SKIRT) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_LEFT_LEG) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_RIGHT_LEG) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_LEFT_FOOT) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_RIGHT_FOOT) {
                y -= 1;
            }
        }

        slice.setId(blockType);
        slice.setX(x);
        slice.setY(y);
        slice.setZ(z);
        byte r = (byte) (color >> 16 & 0xff);
        byte g = (byte) (color >> 8 & 0xff);
        byte b = (byte) (color & 0xff);
        for (int i = 0; i < 6; i++) {
            slice.setR(i, r);
            slice.setG(i, g);
            slice.setB(i, b);
        }
    }
}
