package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.nbt.CompoundTag;

public class SkinDocumentAnimation {

    private final String name;
    private final SkinDescriptor descriptor;

    public SkinDocumentAnimation(String name, SkinDescriptor descriptor) {
        this.name = name;
        this.descriptor = descriptor;
    }

    public SkinDocumentAnimation(CompoundTag tag) {
        this.name = tag.getString(Keys.NAME);
        this.descriptor = tag.getOptionalSkinDescriptor(Keys.SKIN); // extra link
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putString(Keys.NAME, name);
        tag.putOptionalSkinDescriptor(Keys.SKIN, descriptor); // extra link
        return tag;
    }

    public String getName() {
        return name;
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }

    public static class Keys {
        public static final String NAME = "Name";
        public static final String SKIN = "Skin";
    }
}
