package moe.plushie.armourers_workshop.common.library.global.permission;

import java.util.EnumSet;

public final class PermissionSystem {
    
    public final PermissionGroup groupNoLogin;
    public final PermissionGroup groupUser;
    public final PermissionGroup groupMod;
    public final PermissionGroup groupAdmin;
    
    public PermissionSystem() {
        EnumSet<Action> actions = EnumSet.noneOf(Action.class);
        
        actions.add(Action.SKIN_DOWNLOAD);
        groupNoLogin = new PermissionGroup("no login", actions.clone());
        
        actions.add(Action.SKIN_UPLOAD);
        actions.add(Action.SKIN_RATE);
        actions.add(Action.SKIN_FLAG);
        actions.add(Action.SKIN_OWNER_DELETE);
        actions.add(Action.SKIN_OWNER_EDIT);
        actions.add(Action.SKIN_COMMENT_CREATE);
        actions.add(Action.SKIN_COMMENT_OWNER_DELETE);
        actions.add(Action.SKIN_COMMENT_OWNER_EDIT);
        groupUser = new PermissionGroup("user", actions.clone());
        
        actions.add(Action.SKIN_COMMENT_MOD_DELETE);
        actions.add(Action.SKIN_COMMENT_MOD_EDIT);
        actions.add(Action.FLAG_GET_LIST);
        actions.add(Action.FLAG_DELETE);
        actions.add(Action.USER_BAN_TEMP);
        actions.add(Action.USER_BAN_PERM);
        groupMod = new PermissionGroup("mod", actions.clone());
        
        groupAdmin = new PermissionGroup("admin", EnumSet.allOf(Action.class));
    }
    
    public static enum Action {
        SKIN_DOWNLOAD,
        SKIN_UPLOAD,

        SKIN_RATE,
        SKIN_FLAG,
        SKIN_OWNER_DELETE,
        SKIN_MOD_DELETE,
        SKIN_OWNER_EDIT,
        SKIN_MOD_EDIT,
        
        SKIN_COMMENT_CREATE,
        SKIN_COMMENT_OWNER_DELETE,
        SKIN_COMMENT_MOD_DELETE,
        SKIN_COMMENT_OWNER_EDIT,
        SKIN_COMMENT_MOD_EDIT,
        
        FLAG_GET_LIST,
        FLAG_DELETE,
        
        USER_BAN_TEMP,
        USER_BAN_PERM,
        USER_GROUP_CHANGE,
        
        SERVER_VIEW_STATS
    }
    
    public static class PermissionGroup {
        
        private final String name;
        private final EnumSet<Action> actions;
        
        public PermissionGroup(String name, EnumSet<Action> actions) {
            this.name = name;
            this.actions = EnumSet.noneOf(Action.class);
            this.actions.addAll(actions);
        }
        
        public String getName() {
            return name;
        }
        
        public boolean havePermission(Action action) {
            return actions.contains(action);
        }
    }
}
