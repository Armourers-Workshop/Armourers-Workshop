package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.utils.SkinLog;
import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class SkinCubes {

    private static final ISkinCube[] ALL_CUBES_MAPPING = new ISkinCube[256];
    private static final Map<String, ISkinCube> ALL_CUBES = new HashMap<>();

    public static final ISkinCube SOLID = register("solid", 0, false, false);
    public static final ISkinCube GLOWING = register("glowing", 1, false, true);
    public static final ISkinCube GLASS = register("glass", 2, true, false);
    public static final ISkinCube GLASS_GLOWING = register("glass_gowing", 3, true, true);


    public static ISkinCube byName(String name) {
        ISkinCube cube = ALL_CUBES.get(name);
        if (cube != null) {
            return cube;
        }
        return SOLID;
    }

    public static ISkinCube byId(int index) {
        ISkinCube cube = ALL_CUBES_MAPPING[index & 0xFF];
        if (cube != null) {
            return cube;
        }
        return SOLID;
    }

    private static SkinCube register(String name, int id, boolean glass, boolean glowing) {
        SkinCube cube = new SkinCube(id, glass, glowing);
        cube.setRegistryName("armourers:" + name);
        if (ALL_CUBES.containsKey(cube.getRegistryName())) {
            SkinLog.warn("A mod tried to register a cube with an id that is in use.");
            return cube;
        }
        ALL_CUBES.put(cube.getRegistryName(), cube);
        ALL_CUBES_MAPPING[cube.getId() & 0xFF] = cube;
        SkinLog.debug("Registering Skin Cube '{}'", cube.getRegistryName());
        return cube;
    }

    public static int getTotalCubes() {
        return ALL_CUBES.size();
    }
//
//    private void registerCube(ISkinCube cube) {
//        cubeList.add(cube);
//        SkinLog.info("Registering equipment cube - id:" + cube.getId() + " name:" + cube.getClass().getSimpleName());
//    }
//
//    public boolean isBuildingBlock(Block block) {
//        for (int i = 0; i < cubeList.size(); i++) {
//            if (cubeList.get(i).getMinecraftBlock() == block) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public ISkinCube getCubeFromBlock(Block block) {
//        for (int i = 0; i < cubeList.size(); i++) {
//            if (cubeList.get(i).getMinecraftBlock() == block) {
//                return cubeList.get(i);
//            }
//        }
//        return null;
//    }
}
