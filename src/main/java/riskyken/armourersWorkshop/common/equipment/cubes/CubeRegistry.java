package riskyken.armourersWorkshop.common.equipment.cubes;

import java.util.ArrayList;

import riskyken.armourersWorkshop.utils.ModLogger;


public final class CubeRegistry {
    
    public static CubeRegistry INSTANCE;
    
    private ArrayList<Cube> cubeList;
    
    public static void init() {
        INSTANCE = new CubeRegistry();
    }
    
    public CubeRegistry() {
        cubeList = new ArrayList<Cube>();
        registerCubes();
    }
    
    private byte getNextFreeCubeId() {
        return (byte) cubeList.size();
    }
    
    private void registerCube(Cube cube) {
        cube.setId(getNextFreeCubeId());
        cubeList.add(cube);
        ModLogger.log("Registering armour cube: " + cube.getId());
    }
    
    private void registerCubes() {
        registerCube(new CubeNormal());
        registerCube(new CubeGlowing());
    }
}
