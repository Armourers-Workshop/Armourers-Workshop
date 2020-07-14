package riskyken.armourersWorkshop.common.library.global.auth;

import com.google.gson.JsonObject;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.library.global.permission.PermissionSystem;
import riskyken.armourersWorkshop.common.library.global.permission.PermissionSystem.PermissionGroup;
import riskyken.armourersWorkshop.common.library.global.permission.PermissionSystem.PlushieAction;
import riskyken.armourersWorkshop.utils.ModLogger;

public class PlushieSession {

    private PermissionGroup permissionGroup;
    private boolean isAuth;

    private int server_id;
    private String mc_id;
    private String mc_name;
    private String accessToken;
    private long accessTokenReceivedTime;
    private int accessTokenExpiryTime;
    private int permission_group_id = -1;

    public PlushieSession() {
        updatePermissionGroup();
        server_id = 0;
    }

    public boolean authenticate(JsonObject jsonObject) {
        if (jsonObject != null) {
            if (jsonObject.has("valid")) {
                if (jsonObject.get("valid").getAsBoolean()) {
                    server_id = jsonObject.get("server_id").getAsInt();
                    mc_id = jsonObject.get("mc_id").getAsString();
                    mc_name = jsonObject.get("mc_name").getAsString();
                    setPermission_group_id(jsonObject.get("permission_group_id").getAsInt());
                    updateToken(jsonObject);
                    return true;
                }
            }
        }
        return false;
    }

    public void updateToken(JsonObject json) {
        if (json != null && json.has("valid")) {
            if (json.has("valid")) {
                if (json.has("accessToken") & json.has("expiryTime")) {
                    setAccessToken(json.get("accessToken").getAsString(), json.get("expiryTime").getAsInt());
                    isAuth = true;
                    return;
                }
            }
        }
        isAuth = false;
    }

    public int getServerId() {
        return server_id;
    }

    public boolean isOwner(int userId) {
        return this.server_id == userId;
    }

    public void setPermission_group_id(int permission_group_id) {
        this.permission_group_id = permission_group_id;
        updatePermissionGroup();
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

    public int getTokenExpiryTime() {
        return (accessTokenExpiryTime * 1000) - getTimeSinceTokenUpdate();
    }

    public int getTimeSinceTokenUpdate() {
        return (int) (System.currentTimeMillis() - accessTokenReceivedTime);
    }

    public void setAccessToken(String accessToken, int expiryTime) {
        this.accessToken = accessToken;
        this.accessTokenExpiryTime = expiryTime;
        accessTokenReceivedTime = System.currentTimeMillis();
    }

    private void updatePermissionGroup() {
        PermissionSystem ps = ArmourersWorkshop.getProxy().getPermissionSystem();
        if (!isAuthenticated()) {
            this.permissionGroup = ps.getNoLogin();
        }
        this.permissionGroup = ps.getPermissionGroup(permission_group_id);
        ModLogger.log("Permission group set to " + this.permissionGroup.getName());
    }

    public boolean hasPermission(PlushieAction action) {
        return permissionGroup.havePermission(action);
    }
}
