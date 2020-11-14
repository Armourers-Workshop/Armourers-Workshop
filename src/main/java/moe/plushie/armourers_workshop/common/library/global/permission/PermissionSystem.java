package moe.plushie.armourers_workshop.common.library.global.permission;

import java.util.EnumSet;

public final class PermissionSystem {

    public final PermissionGroup groupNoLogin;
    public final PermissionGroup groupUser;
    public final PermissionGroup groupUserBanned;
    public final PermissionGroup groupUserBannedUploads;
    public final PermissionGroup groupUserBannedRatings;
    public final PermissionGroup groupUserBannedReporting;
    public final PermissionGroup groupUserBannedComment;
    public final PermissionGroup groupMod;
    public final PermissionGroup groupAdmin;

    public PermissionSystem() {
        EnumSet<PlushieAction> actionsNoLogin = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsUser = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsUserBanned = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsBannedUploads = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsBannedRatings = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsBannedReporting = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsBannedComment = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsMod = EnumSet.noneOf(PlushieAction.class);
        EnumSet<PlushieAction> actionsAdmin = EnumSet.noneOf(PlushieAction.class);

        actionsNoLogin.add(PlushieAction.SKIN_DOWNLOAD);
        actionsNoLogin.add(PlushieAction.USER_INFO);
        actionsNoLogin.add(PlushieAction.SKIN_SEARCH);
        actionsNoLogin.add(PlushieAction.SKIN_LIST_USER);
        actionsNoLogin.add(PlushieAction.BETA_JOIN);
        actionsNoLogin.add(PlushieAction.BETA_CHECK);
        actionsNoLogin.add(PlushieAction.SERVER_VIEW_STATS);
        actionsNoLogin.add(PlushieAction.GET_SKIN_INFO);

        actionsUser.addAll(actionsNoLogin);
        actionsUser.add(PlushieAction.SKIN_UPLOAD);
        actionsUser.add(PlushieAction.SKIN_RATE);
        actionsUser.add(PlushieAction.SKIN_REPORT);
        actionsUser.add(PlushieAction.SKIN_OWNER_DELETE);
        actionsUser.add(PlushieAction.SKIN_OWNER_EDIT);
        actionsUser.add(PlushieAction.SKIN_COMMENT_CREATE);
        actionsUser.add(PlushieAction.SKIN_COMMENT_OWNER_DELETE);
        actionsUser.add(PlushieAction.SKIN_COMMENT_OWNER_EDIT);
        actionsUser.add(PlushieAction.SKIN_GET_RATED);

        actionsUserBanned.addAll(actionsUser);
        actionsUserBanned.remove(PlushieAction.SKIN_UPLOAD);
        actionsUserBanned.remove(PlushieAction.SKIN_RATE);
        actionsUserBanned.remove(PlushieAction.SKIN_REPORT);
        actionsUserBanned.remove(PlushieAction.SKIN_OWNER_EDIT);
        actionsUserBanned.remove(PlushieAction.SKIN_COMMENT_CREATE);
        actionsUserBanned.remove(PlushieAction.SKIN_COMMENT_OWNER_EDIT);

        actionsBannedUploads.addAll(actionsUser);
        actionsBannedUploads.remove(PlushieAction.SKIN_UPLOAD);

        actionsBannedRatings.addAll(actionsUser);
        actionsBannedRatings.remove(PlushieAction.SKIN_RATE);

        actionsBannedReporting.addAll(actionsUser);
        actionsBannedReporting.remove(PlushieAction.SKIN_REPORT);

        actionsBannedComment.addAll(actionsUser);
        actionsBannedComment.remove(PlushieAction.SKIN_COMMENT_CREATE);
        actionsBannedComment.remove(PlushieAction.SKIN_COMMENT_OWNER_EDIT);

        actionsMod.addAll(actionsUser);
        actionsMod.add(PlushieAction.SKIN_MOD_EDIT);
        actionsMod.add(PlushieAction.SKIN_MOD_DELETE);
        actionsMod.add(PlushieAction.SKIN_COMMENT_MOD_DELETE);
        actionsMod.add(PlushieAction.SKIN_COMMENT_MOD_EDIT);
        actionsMod.add(PlushieAction.FLAG_GET_LIST);
        actionsMod.add(PlushieAction.FLAG_DELETE);
        actionsMod.add(PlushieAction.USER_BAN_TEMP);
        actionsMod.add(PlushieAction.USER_BAN_PERM);
        actionsMod.add(PlushieAction.GET_REPORT_LIST);

        actionsAdmin.addAll(EnumSet.allOf(PlushieAction.class));

        groupNoLogin = new PermissionGroup("no login", actionsNoLogin.clone());
        groupUser = new PermissionGroup("user", actionsUser.clone());
        groupUserBanned = new PermissionGroup("user", actionsUserBanned.clone());
        groupUserBannedUploads = new PermissionGroup("user", actionsBannedUploads.clone());
        groupUserBannedRatings = new PermissionGroup("user", actionsBannedRatings.clone());
        groupUserBannedReporting = new PermissionGroup("user", actionsBannedReporting.clone());
        groupUserBannedComment = new PermissionGroup("user", actionsBannedComment.clone());
        groupMod = new PermissionGroup("mod", actionsMod.clone());
        groupAdmin = new PermissionGroup("admin", actionsAdmin.clone());
    }

    public static enum PlushieAction {
        // ------- Actions for all clients. -------

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

    public PermissionGroup getPermissionGroup(int id) {
        switch (id) {
        case 0:
            return groupUser;
        case 1:
            return groupMod;
        case 10:
            return groupUserBanned;
        case 11:
            return groupUserBannedUploads;
        case 12:
            return groupUserBannedRatings;
        case 13:
            return groupUserBannedReporting;
        case 14:
            return groupUserBannedComment;
        case 255:
            return groupAdmin;
        default:
            return groupNoLogin;
        }
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
