package moe.plushie.armourers_workshop.library.data.impl;

import java.util.EnumSet;

public class ServerPermissions {

    public static final ServerPermissions NO_LOGIN;
    public static final ServerPermissions USER;
    public static final ServerPermissions MOD;
    public static final ServerPermissions ADMIN;

    private final String name;
    private final EnumSet<ServerPermission> actions;

    public ServerPermissions(String name, EnumSet<ServerPermission> actions) {
        this.name = name;
        this.actions = EnumSet.noneOf(ServerPermission.class);
        this.actions.addAll(actions);
    }

    public static ServerPermissions byId(int groupId) {
        return switch (groupId) {
            case 0 -> USER;
            case 1 -> MOD;
            case 255 -> ADMIN;
            default -> NO_LOGIN;
        };
    }

    public String getName() {
        return name;
    }

    public EnumSet<ServerPermission> getActions() {
        return actions;
    }

    public boolean hasPermission(ServerPermission action) {
        return actions.contains(action);
    }


    static {
        EnumSet<ServerPermission> actions = EnumSet.noneOf(ServerPermission.class);

        actions.add(ServerPermission.SKIN_DOWNLOAD);
        actions.add(ServerPermission.GET_RECENTLY_UPLOADED);
        actions.add(ServerPermission.GET_MOST_DOWNLOADED);
        actions.add(ServerPermission.GET_MOST_LIKED);
        actions.add(ServerPermission.USER_INFO);
        actions.add(ServerPermission.SKIN_SEARCH);
        actions.add(ServerPermission.SKIN_LIST_USER);
        actions.add(ServerPermission.BETA_JOIN);
        actions.add(ServerPermission.BETA_CHECK);
        actions.add(ServerPermission.SERVER_VIEW_STATS);
        actions.add(ServerPermission.GET_SKIN_INFO);
        NO_LOGIN = new ServerPermissions("no login", actions.clone());

        actions.add(ServerPermission.SKIN_UPLOAD);
        actions.add(ServerPermission.SKIN_RATE);
        actions.add(ServerPermission.SKIN_REPORT);
        actions.add(ServerPermission.SKIN_OWNER_DELETE);
        actions.add(ServerPermission.SKIN_OWNER_EDIT);
        actions.add(ServerPermission.SKIN_COMMENT_CREATE);
        actions.add(ServerPermission.SKIN_COMMENT_OWNER_DELETE);
        actions.add(ServerPermission.SKIN_COMMENT_OWNER_EDIT);
        actions.add(ServerPermission.SKIN_GET_RATED);
        USER = new ServerPermissions("user", actions.clone());

        actions.add(ServerPermission.SKIN_MOD_EDIT);
        actions.add(ServerPermission.SKIN_MOD_DELETE);
        actions.add(ServerPermission.SKIN_COMMENT_MOD_DELETE);
        actions.add(ServerPermission.SKIN_COMMENT_MOD_EDIT);
        actions.add(ServerPermission.FLAG_GET_LIST);
        actions.add(ServerPermission.FLAG_DELETE);
        actions.add(ServerPermission.USER_BAN_TEMP);
        actions.add(ServerPermission.USER_BAN_PERM);
        actions.add(ServerPermission.GET_REPORT_LIST);
        MOD = new ServerPermissions("mod", actions.clone());

        ADMIN = new ServerPermissions("admin", EnumSet.allOf(ServerPermission.class));
    }
}

//public static class InsufficientPermissionsException extends Exception {
//
//    private final ServerPermission plushieAction;
//
//    public InsufficientPermissionsException(ServerPermission plushieAction) {
//        this.plushieAction = plushieAction;
//    }
//
//    public ServerPermission getAction() {
//        return plushieAction;
//    }
//}
//
//public static class AuthenticationException extends Exception {
//
//}
//}
