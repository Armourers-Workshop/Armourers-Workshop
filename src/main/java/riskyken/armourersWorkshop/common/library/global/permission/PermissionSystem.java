package riskyken.armourersWorkshop.common.library.global.permission;

import java.util.EnumSet;

import net.minecraft.util.MathHelper;

public final class PermissionSystem {

    public final PermissionGroup[] permissionGroups = new PermissionGroup[256];
    
    private final PermissionGroup groupNoLogin;
    private final PermissionGroup groupUser;
    private final PermissionGroup groupUserUploadBan;
    private final PermissionGroup groupMod;
    private final PermissionGroup groupAdmin;

    public PermissionSystem() {
        EnumSet<PlushieAction> actions = EnumSet.noneOf(PlushieAction.class);

        actions.add(PlushieAction.SKIN_DOWNLOAD);
        actions.add(PlushieAction.GET_RECENTLY_UPLOADED);
        actions.add(PlushieAction.GET_MOST_DOWNLOADED);
        actions.add(PlushieAction.GET_MOST_LIKED);
        actions.add(PlushieAction.USER_INFO);
        actions.add(PlushieAction.SKIN_SEARCH);
        actions.add(PlushieAction.SKIN_LIST_USER);
        actions.add(PlushieAction.BETA_JOIN);
        actions.add(PlushieAction.BETA_CHECK);
        actions.add(PlushieAction.SERVER_VIEW_STATS);

        groupNoLogin = new PermissionGroup("no login", actions.clone());
        

        actions.add(PlushieAction.SKIN_RATE);
        actions.add(PlushieAction.SKIN_REPORT);
        actions.add(PlushieAction.SKIN_OWNER_DELETE);
        actions.add(PlushieAction.SKIN_OWNER_EDIT);
        actions.add(PlushieAction.SKIN_COMMENT_CREATE);
        actions.add(PlushieAction.SKIN_COMMENT_OWNER_DELETE);
        actions.add(PlushieAction.SKIN_COMMENT_OWNER_EDIT);
        groupUserUploadBan = new PermissionGroup("user_upload_ban", actions.clone());

        actions.add(PlushieAction.SKIN_UPLOAD);
        groupUser = new PermissionGroup("user", actions.clone());

        actions.add(PlushieAction.SKIN_MOD_EDIT);
        actions.add(PlushieAction.SKIN_MOD_DELETE);
        actions.add(PlushieAction.SKIN_COMMENT_MOD_DELETE);
        actions.add(PlushieAction.SKIN_COMMENT_MOD_EDIT);
        actions.add(PlushieAction.FLAG_GET_LIST);
        actions.add(PlushieAction.FLAG_DELETE);
        actions.add(PlushieAction.USER_BAN_TEMP);
        actions.add(PlushieAction.USER_BAN_PERM);
        groupMod = new PermissionGroup("mod", actions.clone());

        groupAdmin = new PermissionGroup("admin", EnumSet.allOf(PlushieAction.class));
        
        for (int i = 0; i < permissionGroups.length; i++) {
            permissionGroups[i] = groupNoLogin;
        }
        permissionGroups[0] = groupUser;
        permissionGroups[1] = groupMod;
        permissionGroups[2] = groupUserUploadBan;
        permissionGroups[255] = groupAdmin;
    }
    
    public PermissionGroup getNoLogin() {
        return groupNoLogin;
    }
    
    public PermissionGroup getPermissionGroup(int id) {
        if (id < 0 | id > 255) {
            return getNoLogin();
        }
        id = MathHelper.clamp_int(id, 0, 255);
        return permissionGroups[id];
    }

    public static enum PlushieAction {
        /** Get recently uploaded skin list. */
        GET_RECENTLY_UPLOADED,

        /** Get the most liked skin list. */
        GET_MOST_LIKED,

        /** Get most downloaded skin list. */
        GET_MOST_DOWNLOADED,

        /** Tried to join the beta using a code. */
        BETA_JOIN,

        /** Checks if a player is in the beta. */
        BETA_CHECK,

        /** Search for skins. */
        SKIN_SEARCH,

        /** Get a list of a users skins. */
        SKIN_LIST_USER,

        /** Download skins. */
        SKIN_DOWNLOAD,

        /** Upload skins */
        SKIN_UPLOAD,

        /** Leave a skin rating. */
        SKIN_RATE,

        /** Gets the rating a user left on a skin. */
        SKIN_GET_RATED,

        /** Report a skin. */
        SKIN_REPORT,

        /** Delete their own skin. */
        SKIN_OWNER_DELETE,

        /** Delete other users skins. */
        SKIN_MOD_DELETE,

        /** Edit their own skins. */
        SKIN_OWNER_EDIT,

        /** Edit other users skins. */
        SKIN_MOD_EDIT,

        /** Comment on skins. */
        SKIN_COMMENT_CREATE,

        /** Delete their own comments. */
        SKIN_COMMENT_OWNER_DELETE,

        /** Delete other users comments. */
        SKIN_COMMENT_MOD_DELETE,

        /** Edit their own comments. */
        SKIN_COMMENT_OWNER_EDIT,

        /** Edit other users comments. */
        SKIN_COMMENT_MOD_EDIT,

        /** Get the permission flag list. */
        FLAG_GET_LIST,
        /**  */
        FLAG_DELETE,

        /** Get user info. */
        USER_INFO,
        /** Ban a user temporarily. */
        USER_BAN_TEMP,
        /** Ban a user permanently. */
        USER_BAN_PERM,
        /** Change users permission group. */
        USER_GROUP_CHANGE,

        /** View server status. */
        SERVER_VIEW_STATS
    }

    public static class PermissionGroup {

        private final String name;
        private final EnumSet<PlushieAction> actions;

        public PermissionGroup(String name, EnumSet<PlushieAction> actions) {
            this.name = name;
            this.actions = EnumSet.noneOf(PlushieAction.class);
            this.actions.addAll(actions);
        }

        public String getName() {
            return name;
        }

        public boolean havePermission(PlushieAction action) {
            // PermissionAPI.hasPermission(profile, node, context)
            return actions.contains(action);
        }
    }
}
