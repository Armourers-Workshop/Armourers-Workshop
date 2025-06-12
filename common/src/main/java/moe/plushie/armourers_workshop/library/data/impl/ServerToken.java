package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public class ServerToken {

    private final String serverId;

//    private final String mc_id;
//    private final String mc_name;
//
//    private final ServerPermissions permissions;

    private final String accessToken;

    private final long receivedTime;
    private final long expiryTime;

    public ServerToken(IODataObject object) {
        this.serverId = object.get("server_id").stringValue();
//        this.mc_id = object.get("mc_id").stringValue();
//        this.mc_name = object.get("mc_name").stringValue();
//        this.permissions = ServerPermissions.byId(object.get("permission_group_id").intValue());
        this.accessToken = object.get("accessToken").stringValue();
        this.expiryTime = object.get("expiryTime").intValue();
        this.receivedTime = System.currentTimeMillis();

    }

    public boolean isValid() {
        return remainingTime() >= 0;
    }

    public String value() {
        return accessToken;
    }

    public String serverId() {
        return serverId;
    }

    public long remainingTime() {
        return (receivedTime + (expiryTime * 1000L)) - System.currentTimeMillis();
    }
}
