package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class SkinCubes {

    private static final SkinCube[] ALL_CUBES_MAPPING = new SkinCube[256];
    private static final Map<String, SkinCube> ALL_CUBES = new HashMap<>();

    public static final ISkinCube SOLID = register("solid", 0, false, false, ModBlocks.SKIN_CUBE);
    public static final ISkinCube GLOWING = register("glowing", 1, false, true, ModBlocks.SKIN_CUBE_GLOWING);
    public static final ISkinCube GLASS = register("glass", 2, true, false, ModBlocks.SKIN_CUBE_GLASS);
    public static final ISkinCube GLASS_GLOWING = register("glass_gowing", 3, true, true, ModBlocks.SKIN_CUBE_GLASS_GLOWING);

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

    public static ISkinCube byBlock(Block block) {
        for (SkinCube cube : ALL_CUBES.values()) {
            if (cube.getBlock() == block) {
                return cube;
            }
        }
        return SOLID;
    }

    private static SkinCube register(String name, int id, boolean glass, boolean glowing, IRegistryObject<Block> block) {
        SkinCube cube = new SkinCube(id, glass, glowing, block);
        cube.setRegistryName(new SkinResourceLocation("armourers", name));
        if (ALL_CUBES.containsKey(cube.getRegistryName().toString())) {
            ModLog.warn("A mod tried to register a cube with an id that is in use.");
            return cube;
        }
        ALL_CUBES.put(cube.getRegistryName().toString(), cube);
        ALL_CUBES_MAPPING[cube.getId() & 0xFF] = cube;
        ModLog.info("Registering Skin Cube '{}'", cube.getRegistryName());
        return cube;
    }

    public static int getTotalCubes() {
        return ALL_CUBES.size();
    }
}
