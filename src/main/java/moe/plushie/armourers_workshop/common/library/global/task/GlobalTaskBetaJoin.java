package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;

public class GlobalTaskBetaJoin extends GlobalTask<JsonObject> {

    public GlobalTaskBetaJoin(PlushieAction plushieAction) {
        super(PlushieAction.BETA_JOIN, true);
        // TODO Auto-generated constructor stub
    }

    @Override
    public JsonObject call() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
