package moe.plushie.armourers_workshop.utils;


import net.minecraft.world.level.storage.LevelResource;

public final class Constants {

    public static final String EXT = ".armour";
    public static final String PRIVATE = "/private";

    public final static class Folder {

        public static final LevelResource LOCAL_DB = new LevelResource("skin-database");
    }

    public final static class Key {

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

        public static final String KEEP_COLOR = "KeepColor";
        public static final String KEEP_PAINT_TYPE = "KeepPaintType";

        public static final String SOURCE = "Source";
        public static final String DESTINATION = "Destination";

        public static final String BLOCK = "Block";
        public static final String COLOR = "Color";
        public static final String DATA_VERSION = "DataVersion";

        public static final String FACING = "Facing";

        public static final String COLOR_1 = "Color1";
        public static final String COLOR_2 = "Color2";

        public static final String GIFT = "Gift";
        public static final String HOLIDAY = "Holiday";
        public static final String HOLIDAY_TRACKER = "HolidayTracker";
        public static final String HOLIDAY_LOGS = "Logs";

        public static final String FLAGS = "Flags";

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

    /**
     * NBT Tag type IDS, used when storing the nbt to disc, Should align with {@link net.minecraft.nbt.Tag}
     * and {@link net.minecraft.nbt.Tag#getType()}
     * <p>
     * Main use is checking tag type in {@link net.minecraft.nbt.CompoundTag#contains(String, int)}
     */
    public static class TagFlags {
        public static final int END = 0;
        public static final int BYTE = 1;
        public static final int SHORT = 2;
        public static final int INT = 3;
        public static final int LONG = 4;
        public static final int FLOAT = 5;
        public static final int DOUBLE = 6;
        public static final int BYTE_ARRAY = 7;
        public static final int STRING = 8;
        public static final int LIST = 9;
        public static final int COMPOUND = 10;
        public static final int INT_ARRAY = 11;
        public static final int LONG_ARRAY = 12;
        public static final int ANY_NUMERIC = 99;
    }

    /**
     * Flags can be combined with bitwise OR
     */
    public static class BlockFlags {
        /**
         * neighborChanged} on surrounding blocks (with isMoving as false). Also updates comparator output state.
         */
        public static final int NOTIFY_NEIGHBORS = (1 << 0);
        /**
         * Server-side, this updates all the path-finding navigators.
         */
        public static final int BLOCK_UPDATE = (1 << 1);
        /**
         * Stops the blocks from being marked for a render update
         */
        public static final int NO_RERENDER = (1 << 2);
        /**
         * Makes the block be re-rendered immediately, on the main thread.
         * If NO_RERENDER is set, then this will be ignored
         */
        public static final int RERENDER_MAIN_THREAD = (1 << 3);
        /**
         * Causes neighbor updates to be sent to all surrounding blocks (including
         * diagonals).
         */
        public static final int UPDATE_NEIGHBORS = (1 << 4);

        /**
         * Prevents neighbor changes from spawning item drops.
         */
        public static final int NO_NEIGHBOR_DROPS = (1 << 5);

        /**
         * Tell the block being changed that it was moved, rather than removed/replaced.
         */
        public static final int IS_MOVING = (1 << 6);

        public static final int DEFAULT = NOTIFY_NEIGHBORS | BLOCK_UPDATE;
        public static final int DEFAULT_AND_RERENDER = DEFAULT | RERENDER_MAIN_THREAD;
    }
}
