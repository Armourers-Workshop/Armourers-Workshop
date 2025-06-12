package moe.plushie.armourers_workshop.core.skin.serializer.v12;

import moe.plushie.armourers_workshop.core.skin.geometry.collection.SkinGeometrySetV1;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;

import java.io.IOException;

public final class LegacyCubeHelper {

    // Used by file versions less than 10
    public static void loadLegacyCubeData(SkinGeometrySetV1 geometries, SkinGeometrySetV1.BufferSlice slice, IInputStream input, int version, SkinPartType skinPart) throws IOException {
        if (version < 3) {
            loadLegacyCube(geometries, slice, input, version, skinPart);
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
                slice.setRed(i, r);
                slice.setGreen(i, g);
                slice.setBlue(i, b);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                slice.setRed(i, input.readByte());
                slice.setGreen(i, input.readByte());
                slice.setBlue(i, input.readByte());
            }
        }
    }

    // Used by file versions less than 3
    public static void loadLegacyCube(SkinGeometrySetV1 geometries, SkinGeometrySetV1.BufferSlice slice, IInputStream stream, int version, SkinPartType skinPart) throws IOException {
        byte x = stream.readByte();
        byte y = stream.readByte();
        byte z = stream.readByte();
        int color = stream.readInt();
        byte blockType = stream.readByte();

        if (version < 2) {
            if (skinPart == SkinPartTypes.ITEM_SWORD) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_SKIRT) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_LEFT_THIGH) {
                y -= 1;
            } else if (skinPart == SkinPartTypes.BIPPED_RIGHT_THIGH) {
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
            slice.setRed(i, r);
            slice.setGreen(i, g);
            slice.setBlue(i, b);
        }
    }
}
