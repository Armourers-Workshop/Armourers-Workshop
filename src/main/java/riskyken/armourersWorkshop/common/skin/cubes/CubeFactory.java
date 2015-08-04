package riskyken.armourersWorkshop.common.skin.cubes;

import java.util.ArrayList;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.utils.ModLogger;


public final class CubeFactory {
    
    public static CubeFactory INSTANCE;
    
    private ArrayList<Class<? extends ICube>> cubeList;
    
    public static void init() {
        INSTANCE = new CubeFactory();
    }
    
    public CubeFactory() {
        cubeList = new ArrayList<Class<? extends ICube>>();
        registerCubes();
    }
    
    public byte getIdForCubeClass(Class<? extends ICube> cubeClass) {
        for (byte i = 0; i < cubeList.size(); i++) {
            Class<? extends ICube> cube = cubeList.get(i);
            if (cubeClass.equals(cube)) {
                return i;
            }
        }
        return -1;
    }
    
    public ICube getCubeInstanceFormId(byte id) throws InvalidCubeTypeException {
        Class<? extends ICube> cube = getCubeFormId(id);
        if (cube != null) {
            try {
                ICube cubeInstance = cube.newInstance();
                cubeInstance.setId(id);
                return cubeInstance;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new InvalidCubeTypeException();
    }
    
    public Class<? extends ICube> getCubeFormId(byte id) {
        if (id >= 0 && id < cubeList.size()) {
            return cubeList.get(id);
        }
        return null;
    }
    
    public byte getTotalCubes() {
        return (byte) cubeList.size();
    }
    
    private void registerCube(Class<? extends Cube> cubeClass) {
        byte id = getTotalCubes();
        cubeList.add(cubeClass);
        ModLogger.log("Registering equipment cube - id:" + id + " name:" + cubeClass.getSimpleName());
    }
    
    private void registerCubes() {
        registerCube(Cube.class);
        registerCube(CubeGlowing.class);
        registerCube(CubeGlass.class);
        registerCube(CubeGlassGlowing.class);
    }

    public boolean isBuildingBlock(Block block) {
        if (
                block == ModBlocks.colourable |
                block == ModBlocks.colourableGlowing |
                block == ModBlocks.colourableGlass |
                block == ModBlocks.colourableGlassGlowing
                ) {
            return true;
        }
        return false;
    }
}
