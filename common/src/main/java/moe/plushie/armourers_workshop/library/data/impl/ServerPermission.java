package moe.plushie.armourers_workshop.library.data.impl;

public enum ServerPermission {

    // ------- Actions for all clients. -------

    /**
     * Get recently uploaded skin list.
     */
    GET_RECENTLY_UPLOADED,

    /**
     * Get the most liked skin list.
     */
    GET_MOST_LIKED,

    /**
     * Get most downloaded skin list.
     */
    GET_MOST_DOWNLOADED,

    /**
     * Tried to join the beta using a code.
     */
    BETA_JOIN("/join"),

    /**
     * Checks if a player is in the beta.
     */
    BETA_CHECK("/connect"),

    /**
     * Search for skins.
     */
    SKIN_SEARCH("/skin/search"),

    /**
     * Get a list of a users skins.
     */
    SKIN_LIST_USER("/skin/user"),

    /**
     * Download skins.
     */
    SKIN_DOWNLOAD("/skin/download"),

    /**
     * Get user info.
     */
    USER_INFO("/user/info"),

    /**
     * View server status.
     */
    SERVER_VIEW_STATS("/stats"),

    /**
     * Get the skin info
     */
    GET_SKIN_INFO("/skin/info"),

    // ------- Actions for users only. -------

    /**
     * Upload skins
     */
    SKIN_UPLOAD("/skin/upload"),

    /**
     * Leave a skin rating.
     */
    SKIN_RATE("/skin/rate"),

    /**
     * Gets the rating a user left on a skin.
     */
    SKIN_GET_RATED("/skin/rating"),

    /**
     * Report a skin.
     */
    SKIN_REPORT("/skin/report"),

    /**
     * Delete their own skin.
     */
    SKIN_OWNER_DELETE("/skin/delete"),

    /**
     * Edit their own skins.
     */
    SKIN_OWNER_EDIT("/skin/edit"),

    /**
     * Comment on skins.
     */
    SKIN_COMMENT_CREATE,

    /**
     * Delete their own comments.
     */
    SKIN_COMMENT_OWNER_DELETE,

    /**
     * Edit their own comments.
     */
    SKIN_COMMENT_OWNER_EDIT,

    // ------- Actions for mods only. -------

    /**
     * Gets the list of reported skins.
     */
    GET_REPORT_LIST("/skin/reports"),

    /**
     * Delete other users skins.
     */
    SKIN_MOD_DELETE,

    /**
     * Edit other users skins.
     */
    SKIN_MOD_EDIT,

    /**
     * Delete other users comments.
     */
    SKIN_COMMENT_MOD_DELETE,

    /**
     * Edit other users comments.
     */
    SKIN_COMMENT_MOD_EDIT,

    /**
     * Get the permission flag list.
     */
    FLAG_GET_LIST,

    /**
     *
     */
    FLAG_DELETE,

    /**
     * Ban a user temporarily.
     */
    USER_BAN_TEMP,

    /**
     * Ban a user permanently.
     */
    USER_BAN_PERM,

    /**
     * Change users permission group.
     */
    USER_GROUP_CHANGE;

    private final String id;
    ServerPermission(String id) {
        this.id = id;
    }

    ServerPermission() {
        this(null);
    }

    public static ServerPermission byId(String id) {
        for (ServerPermission permission : ServerPermission.values()) {
            if (permission.id != null && permission.id.equals(id)) {
                return permission;
            }
        }
        return null;
    }
}
