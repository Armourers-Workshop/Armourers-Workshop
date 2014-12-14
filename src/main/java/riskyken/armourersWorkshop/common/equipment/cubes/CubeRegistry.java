package riskyken.armourersWorkshop.common.equipment.cubes;

import java.util.ArrayList;

import riskyken.armourersWorkshop.utils.ModLogger;


public final class CubeRegistry {
    
    public static CubeRegistry INSTANCE;
    
    private ArrayList<Class<? extends ICube>> cubeList;
    
    public static void init() {
        INSTANCE = new CubeRegistry();
    }
    
    public CubeRegistry() {
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
    
    public ICube getCubeInstanceFormId(byte id) {
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
        return null;
    }
    
    public Class<? extends ICube> getCubeFormId(byte id) {
        if (id >= 0 && id < cubeList.size()) {
            return cubeList.get(id);
        }
        return null;
    }
    
    private byte getNextFreeCubeId() {
        return (byte) cubeList.size();
    }
    
    private void registerCube(Class<? extends Cube> cubeClass) {
        byte id = getNextFreeCubeId();
        cubeList.add(cubeClass);
        ModLogger.log("Registering equipment cube: " + cubeClass.getSimpleName() + " - id: " + id);
    }
    
    private void registerCubes() {
        registerCube(Cube.class);
        registerCube(CubeGlowing.class);
    }
}
