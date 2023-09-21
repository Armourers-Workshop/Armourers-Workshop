package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;

import java.util.HashMap;

public class ServerSkin {

    private String id;
    private String userId;
    private String name;
    private String description;

    public int downloads = 0;
    public float rating = 0;
    public int ratingCount = 0;

    public boolean showsDownloads = true;
    public boolean showsRating = false;
    public boolean showsGlobalId = true;

    private final SkinDescriptor descriptor;

    public ServerSkin(IDataPackObject object) {
        this.id = object.get("id").stringValue();
        this.userId = object.get("user_id").stringValue();
        this.name = object.get("name").stringValue();
        this.description = object.get("description").stringValue();
        this.descriptor = new SkinDescriptor(DataDomain.GLOBAL_SERVER_PREVIEW.normalize(id));
        this.showsDownloads = object.has("downloads");
        if (this.showsDownloads) {
            this.downloads = object.get("downloads").intValue();
        }
        if (object.has("rating")) {
            this.rating = object.get("rating").floatValue();
            this.showsRating = true;
        }
        if (object.has("rating_count")) {
            this.ratingCount = object.get("rating_count").intValue();
            this.showsRating = true;
        }
    }

    public void update(String name, String desc, IResultHandler<ServerSkin> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("description", desc);
        parameters.put("skinId", id);
        parameters.put("skinOwner", userId);
        getLibrary().request("/skin/edit", parameters, null, (response, exception) -> {
            if (exception == null) {
                this.name = name;
                this.description = desc;
                handler.accept(this);
            } else {
                handler.throwing(exception);
            }
        });
    }

    public void getRate(IResultHandler<Integer> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinId", id);
        getLibrary().request("/skin/rating", parameters, o -> o.get("rating").intValue(), handler);
    }


    public void updateRate(int rate, IResultHandler<Integer> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinId", id);
        parameters.put("rating", rate);
        getLibrary().request("/skin/rate", parameters, o -> o.get("rating").intValue(), (rating, exception) -> {
            if (exception == null) {
                this.rating = rating;
            }
            handler.apply(rate, exception);
        });
    }


    public void remove(IResultHandler<Void> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinId", id);
        parameters.put("skinOwner", userId);
        getLibrary().request("/skin/delete", parameters, null, handler);
    }

    public void report(String message, ReportType reportType, IResultHandler<Void> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("reportSkinId", id);
        parameters.put("reportType", reportType.toString());
        parameters.put("reportMessage", message);
        getLibrary().request("/skin/report", parameters, null, handler);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public ServerUser getUser() {
        return getLibrary().getUserById(userId);
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }

    private GlobalSkinLibrary getLibrary() {
        return GlobalSkinLibrary.getInstance();
    }
}
