package moe.plushie.armourers_workshop.init;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class will load before the MOD initialized,
 * So NEVER don't refer any external class.
 */
public class ModConstants {

    private static final ArrayList<String> ENTITY_TYPES = new ArrayList<>();
    private static final ArrayList<String> BLOCK_ENTITY_TYPES = new ArrayList<>();

    public static final String MOD_ID = "armourers_workshop";
    public static final String MOD_NET_ID = "a";

    public static final String ENTITY_MANNEQUIN = buildEntityType("mannequin");
    public static final String ENTITY_SEAT = buildEntityType("seat");

    public static final String BLOCK_HOLOGRAM_PROJECTOR = buildBlockEntityType("hologram-projector");
    public static final String BLOCK_OUTFIT_MAKER = buildBlockEntityType("outfit-maker");
    public static final String BLOCK_DYE_TABLE = buildBlockEntityType("dye-table");

    public static final String BLOCK_COLOR_MIXER = buildBlockEntityType("colour-mixer");
    public static final String BLOCK_ARMOURER = buildBlockEntityType("armourer");
    public static final String BLOCK_ADVANCED_SKIN_BUILDER = buildBlockEntityType("advanced-skin-builder");

    public static final String BLOCK_SKIN_LIBRARY = buildBlockEntityType("skin-library");
    public static final String BLOCK_SKIN_LIBRARY_GLOBAL = buildBlockEntityType("skin-library-global");

    public static final String BLOCK_SKINNABLE = buildBlockEntityType("skinnable");
    public static final String BLOCK_BOUNDING_BOX = buildBlockEntityType("bounding-box");
    public static final String BLOCK_SKIN_CUBE = buildBlockEntityType("skin-cube");

    public static ResourceLocation key(String path) {
        return new ResourceLocation(ModConstants.MOD_ID, path);
    }

    public static Collection<ResourceLocation> entityTypes() {
        return ENTITY_TYPES.stream().map(ModConstants::key).collect(Collectors.toList());
    }

    public static Collection<ResourceLocation> blockEntityTypes() {
        return BLOCK_ENTITY_TYPES.stream().map(ModConstants::key).collect(Collectors.toList());
    }

    private static String buildEntityType(String name) {
        ENTITY_TYPES.add(name);
        return name;
    }

    private static String buildBlockEntityType(String name) {
        BLOCK_ENTITY_TYPES.add(name);
        return name;
    }
}
