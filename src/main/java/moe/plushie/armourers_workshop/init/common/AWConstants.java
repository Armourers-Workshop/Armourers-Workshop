package moe.plushie.armourers_workshop.init.common;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.storage.FolderName;

public final class AWConstants {

    public final static Vector3f ZERO = new Vector3f();
    public final static Vector3f ONE = new Vector3f(1, 1, 1);

    public static final String EXT = ".armour";
    public static final String PRIVATE = "/private";

    public final static class Folder {

        public static final FolderName LOCAL_DB = new FolderName("skin-database");
    }

    public final static class NBT {
        public static final String SKIN = "ArmourersWorkshop";
        public static final String SKIN_TYPE = "SkinType";
        public static final String SKIN_PART_TYPE = "PartType";
        public static final String SKIN_IDENTIFIER = "Identifier";
        public static final String SKIN_DYE = "SkinDyes";
        public static final String SKIN_PROPERTIES = "SkinProperties";

        public static final String SKIN_CUBES = "Cubes";
        public static final String SKIN_PAINTS = "Paints";
        public static final String SKIN_MARKERS = "Markers";

        public static final String TEXTURE_URL = "URL";
        public static final String TEXTURE_PROFILE = "User";

        public static final String PAINT_DATA = "PaintData";

        public static final String MIRROR = "Mirror";

        public static final String SOURCE = "Source";
        public static final String DESTINATION = "Destination";

        public static final String COLOR = "Color";
        public static final String DATA_VERSION = "DataVersion";

        public static final String COLOR_1 = "Color1";
        public static final String COLOR_2 = "Color2";

        public static final String GIFT = "Gift";
        public static final String HOLIDAY = "Holiday";
        public static final String HOLIDAY_TRACKER = "HolidayTracker";
        public static final String HOLIDAY_LOGS = "Logs";

        public static final String FLAGS = "Flags";

        public static final String DOWN = "Down";
        public static final String UP = "Up";
        public static final String NORTH = "North";
        public static final String SOUTH = "South";
        public static final String WEST = "West";
        public static final String EAST = "East";

        public static final String ENTITY = "EntityTag";
        public static final String BLOCK_ENTITY = "BlockEntityTag";

        public static final String ENTITY_SCALE = "Scale";

        public static final String ENTITY_IS_SMALL = "Small";
        public static final String ENTITY_IS_FLYING = "Flying";
        public static final String ENTITY_IS_GHOST = "Ghost";
        public static final String ENTITY_IS_VISIBLE = "ModelVisible";
        public static final String ENTITY_EXTRA_RENDER = "ExtraRender";
        public static final String ENTITY_TEXTURE = "Texture";
        public static final String ENTITY_POSE = "Pose";

        public static final String TILE_ENTITY_NAME = "Name";
        public static final String TILE_ENTITY_FLAVOUR = "Flavour";

        public static final String TILE_ENTITY_ANGLE = "Angle";
        public static final String TILE_ENTITY_OFFSET = "Offset";
        public static final String TILE_ENTITY_ROTATION_SPEED = "RotSpeed";
        public static final String TILE_ENTITY_ROTATION_OFFSET = "RotOffset";

        public static final String TILE_ENTITY_POWER_MODE = "PowerMode";
        public static final String TILE_ENTITY_IS_GLOWING = "Glowing";
        public static final String TILE_ENTITY_IS_POWERED = "Powered";

        public static final String TILE_ENTITY_REFER = "Refer";
        public static final String TILE_ENTITY_REFERS = "Refers";
        public static final String TILE_ENTITY_MARKERS = "Markers";
        public static final String TILE_ENTITY_LINKED_POS = "LinkedPos";

        public static final String TILE_ENTITY_SHAPE = "Shape";

        public static final String TILE_ENTITY_SKIN = "Skin";
        public static final String TILE_ENTITY_SKIN_PROPERTIES = "SkinProperties";
    }
}
