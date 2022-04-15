package moe.plushie.armourers_workshop.library.data.global.permission;

import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;

public final class PermissionManager {
    
    public static ArrayList<Permission> getPermissions() {
        ArrayList<Permission> permissions = new ArrayList<Permission>();
//        for (Block block : ModBlocks.BLOCK_LIST) {
//            if (block instanceof IPermissionHolder) {
//                ((IPermissionHolder) block).getPermissions(permissions);
//            }
//        }
//        for (Item item : ModItems.ITEM_LIST) {
//            if (item instanceof IPermissionHolder) {
//                ((IPermissionHolder) item).getPermissions(permissions);
//            }
//        }
//
        // Remove duplicates
        ArrayList<Permission> permissionsDedup = new ArrayList<Permission>();
        for (Permission permission : permissions) {
            if (!permissionsDedup.contains(permission)) {
                permissionsDedup.add(permission);
            }
        }
        
        return permissionsDedup;
    }

    public static void registerPermissions() {
        ArrayList<Permission> permissions = getPermissions();
        for (Permission permission : permissions) {
            ModLog.debug("Registering permission: " + permission.node);
            PermissionAPI.registerNode(permission.node, permission.level, permission.description);
        }
    }
}
