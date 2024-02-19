package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.core.permission.BlockPermission;
import moe.plushie.armourers_workshop.core.permission.ContainerPermission;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.world.inventory.MenuType;

@SuppressWarnings("unused")
public class ModPermissions {

    public static final BlockPermission UNDO = new BlockPermission("undo", ModBlocks.SKIN_CUBE);
    public static final BlockPermission REDO = new BlockPermission("redo", ModBlocks.SKIN_CUBE);

    public static final BlockPermission SKINNABLE_SIT = new BlockPermission("sit", ModBlocks.SKINNABLE);
    public static final BlockPermission SKINNABLE_SLEEP = new BlockPermission("sleep", ModBlocks.SKINNABLE);

    public static final BlockPermission ARMOURER_SAVE = new BlockPermission("save", ModBlocks.ARMOURER);
    public static final BlockPermission ARMOURER_LOAD = new BlockPermission("load", ModBlocks.ARMOURER);
    public static final BlockPermission ARMOURER_SETTING = new BlockPermission("setting", ModBlocks.ARMOURER);
    public static final BlockPermission ARMOURER_CLEAR = new BlockPermission("clear", ModBlocks.ARMOURER);
    public static final BlockPermission ARMOURER_COPY = new BlockPermission("copy", ModBlocks.ARMOURER);
    public static final BlockPermission ARMOURER_REPLACE = new BlockPermission("replace", ModBlocks.ARMOURER);

    public static final BlockPermission OUTFIT_MAKER_MAKE = new BlockPermission("make", ModBlocks.OUTFIT_MAKER);

    public static final BlockPermission SKIN_LIBRARY_RELOAD = new BlockPermission("reload", ModBlocks.SKIN_LIBRARY);
    public static final BlockPermission SKIN_LIBRARY_MKDIR = new BlockPermission("mkdir", ModBlocks.SKIN_LIBRARY);
    public static final BlockPermission SKIN_LIBRARY_RENAME = new BlockPermission("rename", ModBlocks.SKIN_LIBRARY);
    public static final BlockPermission SKIN_LIBRARY_DELETE = new BlockPermission("delete", ModBlocks.SKIN_LIBRARY);

    public static final BlockPermission SKIN_LIBRARY_SKIN_UPLOAD = new BlockPermission("skin.upload", ModBlocks.SKIN_LIBRARY);
    public static final BlockPermission SKIN_LIBRARY_SKIN_DOWNLOAD = new BlockPermission("skin.download", ModBlocks.SKIN_LIBRARY);
    public static final BlockPermission SKIN_LIBRARY_SKIN_LOAD = new BlockPermission("skin.load", ModBlocks.SKIN_LIBRARY);
    public static final BlockPermission SKIN_LIBRARY_SKIN_SAVE = new BlockPermission("skin.save", ModBlocks.SKIN_LIBRARY);

    public static final BlockPermission ADVANCED_SKIN_BUILDER_SKIN_EXPORT = new BlockPermission("skin.export", ModBlocks.ADVANCED_SKIN_BUILDER);
    public static final BlockPermission ADVANCED_SKIN_BUILDER_SKIN_IMPORT = new BlockPermission("skin.import", ModBlocks.ADVANCED_SKIN_BUILDER);

    public static final BlockPermission SKIN_LIBRARY_GLOBAL_SKIN_UPLOAD = new BlockPermission("skin.upload", ModBlocks.SKIN_LIBRARY_GLOBAL);

    public static final ContainerPermission OPEN = new ContainerPermission("open-gui", TypedRegistry.findEntries(MenuType.class)::forEach);

    public static void init() {
    }
}
