package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.core.permission.BlockPermission;
import moe.plushie.armourers_workshop.core.permission.ContainerPermission;
import moe.plushie.armourers_workshop.core.permission.Permission;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.platform.PermissionManager;
import moe.plushie.armourers_workshop.utils.TranslateUtils;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ModPermissions {

    private static final ArrayList<Permission> PERMISSIONS = new ArrayList<>();

    public static final BlockPermission UNDO = register(new BlockPermission("undo", ModBlocks.SKIN_CUBE));
    public static final BlockPermission REDO = register(new BlockPermission("redo", ModBlocks.SKIN_CUBE));

    public static final BlockPermission SKINNABLE_SIT = register(new BlockPermission("sit", ModBlocks.SKINNABLE));
    public static final BlockPermission SKINNABLE_SLEEP = register(new BlockPermission("sleep", ModBlocks.SKINNABLE));

    public static final BlockPermission ARMOURER_SAVE = register(new BlockPermission("save", ModBlocks.ARMOURER));
    public static final BlockPermission ARMOURER_LOAD = register(new BlockPermission("load", ModBlocks.ARMOURER));
    public static final BlockPermission ARMOURER_SETTING = register(new BlockPermission("setting", ModBlocks.ARMOURER));
    public static final BlockPermission ARMOURER_CLEAR = register(new BlockPermission("clear", ModBlocks.ARMOURER));
    public static final BlockPermission ARMOURER_COPY = register(new BlockPermission("copy", ModBlocks.ARMOURER));
    public static final BlockPermission ARMOURER_REPLACE = register(new BlockPermission("replace", ModBlocks.ARMOURER));

    public static final BlockPermission OUTFIT_MAKER_MAKE = register(new BlockPermission("make", ModBlocks.OUTFIT_MAKER));

    public static final BlockPermission SKIN_LIBRARY_RELOAD = register(new BlockPermission("reload", ModBlocks.SKIN_LIBRARY));
    public static final BlockPermission SKIN_LIBRARY_MKDIR = register(new BlockPermission("mkdir", ModBlocks.SKIN_LIBRARY));
    public static final BlockPermission SKIN_LIBRARY_RENAME = register(new BlockPermission("rename", ModBlocks.SKIN_LIBRARY));
    public static final BlockPermission SKIN_LIBRARY_DELETE = register(new BlockPermission("delete", ModBlocks.SKIN_LIBRARY));

    public static final BlockPermission SKIN_LIBRARY_SKIN_UPLOAD = register(new BlockPermission("skin.upload", ModBlocks.SKIN_LIBRARY));
    public static final BlockPermission SKIN_LIBRARY_SKIN_DOWNLOAD = register(new BlockPermission("skin.download", ModBlocks.SKIN_LIBRARY));
    public static final BlockPermission SKIN_LIBRARY_SKIN_LOAD = register(new BlockPermission("skin.load", ModBlocks.SKIN_LIBRARY));
    public static final BlockPermission SKIN_LIBRARY_SKIN_SAVE = register(new BlockPermission("skin.save", ModBlocks.SKIN_LIBRARY));

    public static final ContainerPermission OPEN = register(new ContainerPermission("open-gui", Registry.MENU_TYPE.getEntries()::forEach));

    private static <T extends Permission> T register(T permission) {
        PERMISSIONS.add(permission);
        return permission;
    }

    public static void init() {
        for (Permission permission : PERMISSIONS) {
            for (String key : permission.getNodes()) {
                String desc = TranslateUtils.title("permission." + key).getContents();
                ModLog.info("Registering Permission '{}'", key);
                PermissionManager.registerNode(key, PermissionManager.Level.ALL, desc);
            }
        }
    }
}
