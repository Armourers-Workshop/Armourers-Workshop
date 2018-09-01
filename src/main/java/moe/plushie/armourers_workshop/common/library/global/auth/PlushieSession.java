package moe.plushie.armourers_workshop.common.library.global.auth;

import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.Action;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PermissionGroup;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class PlushieSession {
    
    private PermissionGroup permissionGroup;
    private boolean isAuth;
    
    private int server_id;
    private String mc_id;
    private String mc_name;
    private String accessToken;
    private long accessTokenReceivedTime;
    private int accessTokenExpiryTime;
    private int permission_group_id;
    
    public PlushieSession() {
        this.permissionGroup = ArmourersWorkshop.getProxy().getPermissionSystem().groupNoLogin;
        server_id = 0;
    }
    
    public boolean authenticate(JsonObject jsonObject) {
        if (jsonObject != null) {
            if (jsonObject.has("valid")) {
                if (jsonObject.get("valid").getAsBoolean()) {
                    server_id = jsonObject.get("server_id").getAsInt();
                    mc_id = jsonObject.get("mc_id").getAsString();
                    mc_name = jsonObject.get("mc_name").getAsString();
                    accessToken = jsonObject.get("accessToken").getAsString();
                    accessTokenReceivedTime = System.currentTimeMillis();
                    accessTokenExpiryTime = jsonObject.get("expiryTime").getAsInt();
                    permission_group_id = jsonObject.get("permission_group_id").getAsInt();
                    isAuth = true;
                    return true;
                }
            }
        }
        isAuth = false;
        return false;
    }
    
    public int getServerId() {
        return server_id;
    }
    
    public void setPermission_group_id(int permission_group_id) {
        ModLogger.log("permission_group_id: " + permission_group_id);
        this.permission_group_id = permission_group_id;
    }
    
    public int getPermission_group_id() {
        return permission_group_id;
    }
    
    public void setServerId(int serverId) {
        this.server_id = serverId;
    }
    
    public boolean hasServerId() {
        return server_id > 0;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public boolean isAuthenticated() {
        if (isAuth) {
            if (accessTokenReceivedTime + (accessTokenExpiryTime * 1000) > System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        accessTokenReceivedTime = System.currentTimeMillis();
    }
    
    public void setPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }
    
    public boolean hasPermission(Action action) {
        return permissionGroup.havePermission(action);
    }
}
