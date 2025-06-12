package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

import java.util.Objects;
import java.util.UUID;

public class ServerUser {

    private final String id;
    private final UUID uuid;

    private final ServerPermissions permissions;

    private String name;
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

    public static ServerUser fromJSON(IODataObject object) {
        var id = object.get("id").stringValue();
        var uuid = UUID.fromString(object.get("uuid").stringValue());
        var name = object.get("username").stringValue();
        var permissions = ServerPermissions.byId(object.get("permission_group_id").intValue());
        return new ServerUser(id, uuid, name, permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerUser that)) return false;
        return Objects.equals(id, that.id);
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

    public String id() {
        return id;
    }

    public UUID uuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void setAccessToken(ServerToken accessToken) {
        this.accessToken = accessToken;
    }

    public ServerToken accessToken() {
        return accessToken;
    }
}
