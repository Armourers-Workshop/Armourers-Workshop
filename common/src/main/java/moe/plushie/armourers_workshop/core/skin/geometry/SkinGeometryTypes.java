package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;

@SuppressWarnings("unused")
public final class SkinGeometryTypes {

    private static final SkinGeometryType[] ALL_GEOMETRY_TYPE_MAPPING = new SkinGeometryType[256];
    private static final LinkedHashMap<String, SkinGeometryType> ALL_GEOMETRY_TYPES = new LinkedHashMap<>();

    public static final SkinGeometryType BLOCK_SOLID = register("solid", 0, ModBlocks.SKIN_CUBE);
    public static final SkinGeometryType BLOCK_GLOWING = register("glowing", 1, ModBlocks.SKIN_CUBE_GLOWING);
    public static final SkinGeometryType BLOCK_GLASS = register("glass", 2, ModBlocks.SKIN_CUBE_GLASS);
    public static final SkinGeometryType BLOCK_GLASS_GLOWING = register("glass_glowing", 3, ModBlocks.SKIN_CUBE_GLASS_GLOWING);

    public static final SkinGeometryType CUBE = register("cube", 4, ModBlocks.BOUNDING_BOX);
    public static final SkinGeometryType CUBE_CULL = register("cube_cull", 6, ModBlocks.BOUNDING_BOX);

    public static final SkinGeometryType MESH = register("mesh", 5, ModBlocks.BOUNDING_BOX);
    public static final SkinGeometryType MESH_CULL = register("mesh_cull", 7, ModBlocks.BOUNDING_BOX);

    public static SkinGeometryType byName(String name) {
        var cube = ALL_GEOMETRY_TYPES.get(name);
        if (cube != null) {
            return cube;
        }
        return BLOCK_SOLID;
    }

    public static SkinGeometryType byId(int index) {
        var cubeType = ALL_GEOMETRY_TYPE_MAPPING[index & 0xFF];
        if (cubeType != null) {
            return cubeType;
        }
        return BLOCK_SOLID;
    }

    public static SkinGeometryType byBlock(Block block) {
        for (var cubeType : ALL_GEOMETRY_TYPES.values()) {
            if (cubeType.block() == block) {
                return cubeType;
            }
        }
        return BLOCK_SOLID;
    }

    /**
     * Should this cube be rendered after the world?
     */
    public static boolean isGlassBlock(SkinGeometryType geometryType) {
        return geometryType == BLOCK_GLASS || geometryType == BLOCK_GLASS_GLOWING;
    }

    /**
     * Will this cube glow in the dark?
     */
    public static boolean isGlowingBlock(SkinGeometryType geometryType) {
        return geometryType == BLOCK_GLOWING || geometryType == BLOCK_GLASS_GLOWING;
    }

    private static SkinGeometryType register(String name, int id, IRegistryHolder<Block> block) {
        var geometryType = new SkinGeometryType(id, block);
        geometryType.setRegistryName(OpenResourceLocation.create("armourers", name));
        if (ALL_GEOMETRY_TYPES.containsKey(geometryType.registryName().toString())) {
            ModLog.warn("A mod tried to register a geometry type with an id that is in use.");
            return geometryType;
        }
        ALL_GEOMETRY_TYPES.put(geometryType.registryName().toString(), geometryType);
        ALL_GEOMETRY_TYPE_MAPPING[geometryType.id() & 0xFF] = geometryType;
        ModLog.debug("Registering Skin Geometry '{}'", geometryType.registryName());
        return geometryType;
    }

    public static int getTotalCubes() {
        return ALL_GEOMETRY_TYPES.size();
    }
}
