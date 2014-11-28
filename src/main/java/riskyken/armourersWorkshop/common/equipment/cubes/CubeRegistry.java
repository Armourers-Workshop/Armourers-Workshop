package riskyken.armourersWorkshop.common.equipment.cubes;

import java.util.ArrayList;

import riskyken.armourersWorkshop.utils.ModLogger;


public final class CubeRegistry {
    
    public static CubeRegistry INSTANCE;
    
    private ArrayList<Class<? extends Cube>> cubeList;
    
    public static void init() {
        INSTANCE = new CubeRegistry();
    }
    
    public CubeRegistry() {
        cubeList = new ArrayList<Class<? extends Cube>>();
        registerCubes();
    }
    
    private byte getNextFreeCubeId() {
        return (byte) cubeList.size();
    }
    
    private void registerCube(Class<? extends Cube> cubeClass) {
        //cubeClass.setId(getNextFreeCubeId());
        cubeList.add(cubeClass);
        ModLogger.log("Registering armour cube: " + cubeClass.getSimpleName());
    }
    
    private void registerCubes() {
        registerCube(CubeNormal.class);
        registerCube(CubeGlowing.class);
    }
}
