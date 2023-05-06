package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class SkinCubeTypes {

    private static final SkinCubeType[] ALL_CUBES_MAPPING = new SkinCubeType[256];
    private static final Map<String, SkinCubeType> ALL_CUBES = new HashMap<>();

    public static final ISkinCubeType SOLID = register("solid", 0, false, false, ModBlocks.SKIN_CUBE);
    public static final ISkinCubeType GLOWING = register("glowing", 1, false, true, ModBlocks.SKIN_CUBE_GLOWING);
    public static final ISkinCubeType GLASS = register("glass", 2, true, false, ModBlocks.SKIN_CUBE_GLASS);
    public static final ISkinCubeType GLASS_GLOWING = register("glass_gowing", 3, true, true, ModBlocks.SKIN_CUBE_GLASS_GLOWING);

    public static ISkinCubeType byName(String name) {
        ISkinCubeType cube = ALL_CUBES.get(name);
        if (cube != null) {
            return cube;
        }
        return SOLID;
    }

    public static ISkinCubeType byId(int index) {
        ISkinCubeType cubeType = ALL_CUBES_MAPPING[index & 0xFF];
        if (cubeType != null) {
            return cubeType;
        }
        return SOLID;
    }

    public static ISkinCubeType byBlock(Block block) {
        for (SkinCubeType cubeType : ALL_CUBES.values()) {
            if (cubeType.getBlock() == block) {
                return cubeType;
            }
        }
        return SOLID;
    }

    private static SkinCubeType register(String name, int id, boolean glass, boolean glowing, IRegistryKey<Block> block) {
        SkinCubeType cube = new SkinCubeType(id, glass, glowing, block);
        cube.setRegistryName(new SkinResourceLocation("armourers", name));
        if (ALL_CUBES.containsKey(cube.getRegistryName().toString())) {
            ModLog.warn("A mod tried to register a cube with an id that is in use.");
            return cube;
        }
        ALL_CUBES.put(cube.getRegistryName().toString(), cube);
        ALL_CUBES_MAPPING[cube.getId() & 0xFF] = cube;
        ModLog.debug("Registering Skin Cube '{}'", cube.getRegistryName());
        return cube;
    }

    public static int getTotalCubes() {
        return ALL_CUBES.size();
    }
}
