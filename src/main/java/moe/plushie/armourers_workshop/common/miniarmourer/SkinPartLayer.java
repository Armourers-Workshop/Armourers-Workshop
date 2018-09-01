package moe.plushie.armourers_workshop.common.miniarmourer;

import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;

public class SkinPartLayer {
    
    private byte[][][] cubeId;
    private byte[][][][] cubeColourR;
    private byte[][][][] cubeColourG;
    private byte[][][][] cubeColourB;
    private byte[][][][] cubePaintType;
    
    public SkinPartLayer(ISkinPartType partType) {
        IRectangle3D buildSpace = partType.getBuildingSpace();
        setCubeCount(buildSpace.getWidth(), buildSpace.getHeight(), buildSpace.getDepth());
    }
    
    public void setCubeCount(int width, int height, int depth) {
        cubeId = new byte[width][height][depth];
        cubeColourR = new byte[width][height][depth][6];
        cubeColourG = new byte[width][height][depth][6];
        cubeColourB = new byte[width][height][depth][6];
        cubePaintType = new byte[width][height][depth][6];
    }
}
