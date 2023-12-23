package moe.plushie.armourers_workshop.core.skin.document;

import net.minecraft.nbt.CompoundTag;

public class SkinDocumentSettings {

    private float scale = 1;
    private boolean showModel = true;

    public SkinDocumentSettings() {
    }

    public SkinDocumentSettings(CompoundTag tag) {
        this.scale = tag.getOptionalFloat(Keys.SCALE, 1);

    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putOptionalFloat(Keys.SCALE, scale, 1);
//        tag.putOptionalString(SkinDocumentNode.Keys.NAME, name, null);
//        tag.putOptionalType(SkinDocumentNode.Keys.TYPE, type, SkinPartTypes.ADVANCED);
//        tag.putOptionalSkinDescriptor(SkinDocumentNode.Keys.SKIN, skin);
//        tag.putOptionalVector3f(SkinDocumentNode.Keys.LOCATION, location, Vector3f.ZERO);
//        tag.putOptionalVector3f(SkinDocumentNode.Keys.ROTATION, rotation, Vector3f.ZERO);
//        tag.putOptionalVector3f(SkinDocumentNode.Keys.SCALE, scale, Vector3f.ONE);
//        if (children.size() != 0) {
//            ListTag listTag = new ListTag();
//            children.forEach(it -> listTag.add(it.serializeNBT()));
//            tag.put(SkinDocumentNode.Keys.CHILDREN, listTag);
//        }
//        tag.putOptionalBoolean(SkinDocumentNode.Keys.ENABLED, isEnabled, true);
//        tag.putOptionalBoolean(SkinDocumentNode.Keys.MIRROR, isMirror, false);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(Keys.SCALE)) {
            scale = tag.getFloat(Keys.SCALE);
        }
//        SkinDescriptor newSkin = tag.getOptionalSkinDescriptor(SkinDocumentNode.Keys.SKIN, null);
//        if (newSkin != null) {
//            skin = newSkin;
//        }
//        Vector3f newLocation = tag.getOptionalVector3f(SkinDocumentNode.Keys.LOCATION, null);
//        if (newLocation != null) {
//            location = newLocation;
//        }
//        Vector3f newRotation = tag.getOptionalVector3f(SkinDocumentNode.Keys.ROTATION, null);
//        if (newRotation != null) {
//            rotation = newRotation;
//        }
//        Vector3f newScale = tag.getOptionalVector3f(SkinDocumentNode.Keys.SCALE, null);
//        if (newScale != null) {
//            scale = newScale;
//        }
//        String newName = tag.getOptionalString(SkinDocumentNode.Keys.NAME, null);
//        if (newName != null) {
//            name = newName;
//        }
//        if (tag.contains(SkinDocumentNode.Keys.ENABLED)) {
//            isEnabled = tag.getBoolean(SkinDocumentNode.Keys.ENABLED);
//        }
//        if (tag.contains(SkinDocumentNode.Keys.MIRROR)) {
//            isMirror = tag.getBoolean(SkinDocumentNode.Keys.MIRROR);
//        }
//        listener.documentDidUpdateNode(this, tag);
    }

    protected static class Keys {

        public static final String SCALE = "Scale";

    }
}
