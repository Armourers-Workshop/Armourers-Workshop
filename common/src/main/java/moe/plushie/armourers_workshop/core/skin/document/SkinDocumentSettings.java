package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import net.minecraft.nbt.CompoundTag;

public class SkinDocumentSettings {

    private float scale = 1;
    private boolean showsOrigin = true;
    private boolean showsHelperModel = true;
    private SkinItemTransforms itemTransforms;

    private SkinDocumentListener listener;

    public SkinDocumentSettings() {
    }

    public SkinDocumentSettings(CompoundTag tag) {
        this.scale = tag.getOptionalFloat(Keys.SCALE, 1);
        this.showsHelperModel = tag.getOptionalBoolean(Keys.HELPER_MODEL, true);
        this.showsOrigin = tag.getOptionalBoolean(Keys.ORIGIN, true);
        this.itemTransforms = tag.getOptionalItemTransforms(Keys.ITEM_TRANSFORMS, null);
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putOptionalFloat(Keys.SCALE, scale, 1);
        tag.putOptionalBoolean(Keys.HELPER_MODEL, showsHelperModel, true);
        tag.putOptionalBoolean(Keys.ORIGIN, showsOrigin, true);
        tag.putOptionalItemTransforms(Keys.ITEM_TRANSFORMS, itemTransforms, null);
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
        if (tag.contains(Keys.ITEM_TRANSFORMS)) {
            itemTransforms = tag.getOptionalItemTransforms(Keys.ITEM_TRANSFORMS, null);
        }
    }


    public void setShowsHelperModel(boolean value) {
        this.showsHelperModel = value;
        if (listener != null) {
            var tag = new CompoundTag();
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
            var tag = new CompoundTag();
            tag.putBoolean(Keys.ORIGIN, value);
            listener.documentDidChangeSettings(tag);
        }
    }

    public boolean showsOrigin() {
        return showsOrigin;
    }

    public void setItemTransforms(SkinItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
        if (listener != null) {
            var tag = new CompoundTag();
            tag.putOptionalItemTransforms(Keys.ITEM_TRANSFORMS, itemTransforms, null);
            listener.documentDidChangeSettings(tag);
        }
    }

    public SkinItemTransforms getItemTransforms() {
        return itemTransforms;
    }

    protected void setListener(SkinDocumentListener listener) {
        this.listener = listener;
    }

    public static class Keys {

        public static final String SCALE = "Scale";
        public static final String ORIGIN = "Origin";
        public static final String HELPER_MODEL = "HelperModel";
        public static final String ITEM_TRANSFORMS = "ItemTransforms";
    }
}
