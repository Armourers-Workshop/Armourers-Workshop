package moe.plushie.armourers_workshop.library.data.global;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;

import java.util.UUID;

public class PlushieUser {

    private final int id;
    private final UUID uuid;
    private final String username;
    private final int permissionGroupId;

    private PlushieUser(int id, UUID uuid, String username, int permissionGroupId) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.permissionGroupId = permissionGroupId;
    }

    public static PlushieUser readPlushieUser(JsonObject json) {
        if (json != null) {
            if (json.has("valid") && json.get("valid").getAsBoolean()) {
                if (json.has("id") & json.has("uuid") & json.has("username") & json.has("permission_group_id")) {
                    int id = json.get("id").getAsInt();
                    UUID uuid = null;
                    try {
                        uuid = UUID.fromString(json.get("uuid").getAsString());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                    String username = json.get("username").getAsString();
                    int permission_group_id = json.get("permission_group_id").getAsInt();
                    return new PlushieUser(id, uuid, username, permission_group_id);
                }
            }
        }
        return null;
    }

    public static PlushieUser getLocalUser() {
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;
        return new PlushieUser(session.getServerId(), session.getMcId(), session.getMcName(), session.getPermissionGroupID());
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public int getPermissionGroupId() {
        return permissionGroupId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlushieUser other = (PlushieUser) obj;
        return id == other.id;
    }
}
