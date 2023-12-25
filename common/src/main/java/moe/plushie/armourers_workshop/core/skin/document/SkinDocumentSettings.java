package moe.plushie.armourers_workshop.core.skin.document;

import net.minecraft.nbt.CompoundTag;

public class SkinDocumentSettings {

    private float scale = 1;
    private boolean showsOrigin = true;
    private boolean showsHelperModel = true;

    private SkinDocumentListener listener;

    public SkinDocumentSettings() {
    }

    public SkinDocumentSettings(CompoundTag tag) {
        this.scale = tag.getOptionalFloat(Keys.SCALE, 1);
        this.showsHelperModel = tag.getOptionalBoolean(Keys.HELPER_MODEL, true);
        this.showsOrigin = tag.getOptionalBoolean(Keys.ORIGIN, true);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putOptionalFloat(Keys.SCALE, scale, 1);
        tag.putOptionalBoolean(Keys.HELPER_MODEL, showsHelperModel, true);
        tag.putOptionalBoolean(Keys.ORIGIN, showsOrigin, true);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(Keys.SCALE)) {
            scale = tag.getFloat(Keys.SCALE);
        }
        if (tag.contains(Keys.HELPER_MODEL)) {
            showsHelperModel = tag.getBoolean(Keys.HELPER_MODEL);
        }
        if (tag.contains(Keys.ORIGIN)) {
            showsHelperModel = tag.getBoolean(Keys.ORIGIN);
        }
    }


    public void setShowsHelperModel(boolean value) {
        this.showsHelperModel = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(Keys.HELPER_MODEL, value);
            listener.documentDidChangeSettings(tag);
        }
    }

    public boolean showsHelperModel() {
        return showsHelperModel;
    }

    public void setShowsOrigin(boolean value) {
        this.showsOrigin = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(Keys.ORIGIN, value);
            listener.documentDidChangeSettings(tag);
        }
    }

    public boolean showsOrigin() {
        return showsOrigin;
    }

    protected void setListener(SkinDocumentListener listener) {
        this.listener = listener;
    }

    protected static class Keys {

        public static final String SCALE = "Scale";
        public static final String ORIGIN = "Origin";
        public static final String HELPER_MODEL = "HelperModel";

    }
}
