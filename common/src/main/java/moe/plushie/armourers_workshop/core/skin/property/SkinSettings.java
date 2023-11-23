package moe.plushie.armourers_workshop.core.skin.property;

import java.util.Objects;

public class SkinSettings {

    public static final SkinSettings EMPTY = new SkinSettings();

    private boolean isEditable = false;


    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isEditable() {
        return isEditable;
    }
}
