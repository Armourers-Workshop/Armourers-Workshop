package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;

import java.util.Objects;
import java.util.UUID;

public class ServerUser {

    private final String id;
    private final UUID uuid;
    private final String name;

    private final ServerPermissions permissions;

    private ServerToken accessToken;

    public ServerUser(UUID uuid, String name) {
        this(null, uuid, name, ServerPermissions.NO_LOGIN);
    }

    public ServerUser(String id, UUID uuid, String name, ServerPermissions permissions) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.permissions = permissions;
    }

    public static ServerUser fromJSON(IDataPackObject object) {
        String id = object.get("id").stringValue();
        UUID uuid = UUID.fromString(object.get("uuid").stringValue());
        String name = object.get("username").stringValue();
        ServerPermissions permissions = ServerPermissions.byId(object.get("permission_group_id").intValue());
        return new ServerUser(id, uuid, name, permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerUser user = (ServerUser) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean hasPermission(ServerPermission action) {
        return permissions.hasPermission(action);
    }

    public boolean isMember() {
        return id != null;
    }

    public boolean isAuthenticated() {
        return accessToken != null && accessToken.isValid();
    }

    public String getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public ServerPermissions getPermissions() {
        return permissions;
    }

    public void setAccessToken(ServerToken accessToken) {
        this.accessToken = accessToken;
    }

    public ServerToken getAccessToken() {
        return accessToken;
    }
}
