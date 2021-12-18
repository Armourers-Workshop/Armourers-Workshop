package moe.plushie.armourers_workshop.core.skin.cubes;

import moe.plushie.armourers_workshop.core.api.common.skin.ICube;
import moe.plushie.armourers_workshop.core.utils.SkinLog;
import net.minecraft.block.Block;

import java.util.ArrayList;


public final class SkinCubes {
    
    public static SkinCubes INSTANCE;
    
    private ArrayList<ICube> cubeList;
    
    public static void init() {
        INSTANCE = new SkinCubes();
        INSTANCE.registerCubes();
    }
    
    public SkinCubes() {
        cubeList = new ArrayList<>();
    }
    
    public ICube getCubeFormId(byte id) {
        if (id >= 0 && id < cubeList.size()) {
            return cubeList.get(id);
        }
        return null;
    }
    
    public byte getTotalCubes() {
        return (byte) cubeList.size();
    }
    
    private void registerCube(ICube cube) {
        cubeList.add(cube);
        SkinLog.info("Registering equipment cube - id:" + cube.getId() + " name:" + cube.getClass().getSimpleName());
    }
    
    public void registerCubes() {
        registerCube(new SkinCube());
        registerCube(new SkinCubeGlowing());
        registerCube(new SkinCubeGlass());
        registerCube(new SkinCubeGlassGlowing());
    }

    public boolean isBuildingBlock(Block block) {
        for (int i = 0; i < cubeList.size(); i++) {
            if (cubeList.get(i).getMinecraftBlock() == block) {
                return true;
            }
        }
        return false;
    }
    
    public ICube getCubeFromBlock(Block block) {
        for (int i = 0; i < cubeList.size(); i++) {
            if (cubeList.get(i).getMinecraftBlock() == block) {
                return cubeList.get(i);
            }
        }
        return null;
    }
}
