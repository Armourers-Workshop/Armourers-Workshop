package moe.plushie.armourers_workshop.common.library.global.permission;

import java.util.EnumSet;

public final class PermissionSystem {

    public final PermissionGroup groupNoLogin;
    public final PermissionGroup groupUser;
    public final PermissionGroup groupMod;
    public final PermissionGroup groupAdmin;

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
        actions.add(PlushieAction.GET_SKIN_INFO);
        groupNoLogin = new PermissionGroup("no login", actions.clone());

        actions.add(PlushieAction.SKIN_UPLOAD);
        actions.add(PlushieAction.SKIN_RATE);
        actions.add(PlushieAction.SKIN_REPORT);
        actions.add(PlushieAction.SKIN_OWNER_DELETE);
        actions.add(PlushieAction.SKIN_OWNER_EDIT);
        actions.add(PlushieAction.SKIN_COMMENT_CREATE);
        actions.add(PlushieAction.SKIN_COMMENT_OWNER_DELETE);
        actions.add(PlushieAction.SKIN_COMMENT_OWNER_EDIT);
        actions.add(PlushieAction.SKIN_GET_RATED);
        groupUser = new PermissionGroup("user", actions.clone());

        actions.add(PlushieAction.SKIN_MOD_EDIT);
        actions.add(PlushieAction.SKIN_MOD_DELETE);
        actions.add(PlushieAction.SKIN_COMMENT_MOD_DELETE);
        actions.add(PlushieAction.SKIN_COMMENT_MOD_EDIT);
        actions.add(PlushieAction.FLAG_GET_LIST);
        actions.add(PlushieAction.FLAG_DELETE);
        actions.add(PlushieAction.USER_BAN_TEMP);
        actions.add(PlushieAction.USER_BAN_PERM);
        actions.add(PlushieAction.GET_REPORT_LIST);
        groupMod = new PermissionGroup("mod", actions.clone());

        groupAdmin = new PermissionGroup("admin", EnumSet.allOf(PlushieAction.class));
    }

    public static enum PlushieAction {
        // ------- Actions for all clients. -------

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

        /** Get user info. */
        USER_INFO,

        /** View server status. */
        SERVER_VIEW_STATS,

        /** Get the skin info */
        GET_SKIN_INFO,

        // ------- Actions for users only. -------

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

        /** Edit their own skins. */
        SKIN_OWNER_EDIT,

        /** Comment on skins. */
        SKIN_COMMENT_CREATE,

        /** Delete their own comments. */
        SKIN_COMMENT_OWNER_DELETE,

        /** Edit their own comments. */
        SKIN_COMMENT_OWNER_EDIT,

        // ------- Actions for mods only. -------

        /** Gets the list of reported skins. */
        GET_REPORT_LIST,

        /** Delete other users skins. */
        SKIN_MOD_DELETE,

        /** Edit other users skins. */
        SKIN_MOD_EDIT,

        /** Delete other users comments. */
        SKIN_COMMENT_MOD_DELETE,

        /** Edit other users comments. */
        SKIN_COMMENT_MOD_EDIT,

        /** Get the permission flag list. */
        FLAG_GET_LIST,

        /**  */
        FLAG_DELETE,

        /** Ban a user temporarily. */
        USER_BAN_TEMP,

        /** Ban a user permanently. */
        USER_BAN_PERM,

        /** Change users permission group. */
        USER_GROUP_CHANGE
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

    public static class InsufficientPermissionsException extends Exception {

        private final PlushieAction plushieAction;

        public InsufficientPermissionsException(PlushieAction plushieAction) {
            this.plushieAction = plushieAction;
        }

        public PlushieAction getAction() {
            return plushieAction;
        }
    }

    public static class AuthenticationException extends Exception {

    }
}
