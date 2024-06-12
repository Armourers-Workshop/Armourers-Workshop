package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;

public class ServerResponse {

    private final boolean valid;
    private final String message;

    public ServerResponse(IDataPackObject object) {
        this.valid = validFromJSON(object.get("valid"));
        this.message = object.get("reason").stringValue();
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    private boolean validFromJSON(IDataPackObject object) {
        return switch (object.type()) {
            case BOOLEAN -> object.boolValue();
            case STRING -> object.stringValue().equals("true");
            default -> true;
        };
    }
}
