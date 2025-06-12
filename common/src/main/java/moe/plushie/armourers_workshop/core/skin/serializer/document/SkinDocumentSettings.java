package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.nbt.CompoundTag;

public class SkinDocumentSettings implements IDataSerializable.Immutable {

    public static IDataCodec<SkinDocumentSettings> CODEC = IDataCodec.COMPOUND_TAG.serializer(SkinDocumentSettings::new);

    private float scale = 1;
    private boolean showsOrigin = true;
    private boolean showsHelperModel = true;
    private OpenItemTransforms itemTransforms;

    private SkinDocumentListener listener;

    public SkinDocumentSettings() {
    }

    public SkinDocumentSettings(IDataSerializer serializer) {
        this.scale = serializer.read(CodingKeys.SCALE);
        this.showsHelperModel = serializer.read(CodingKeys.HELPER_MODEL);
        this.showsOrigin = serializer.read(CodingKeys.ORIGIN);
        this.itemTransforms = serializer.read(CodingKeys.ITEM_TRANSFORMS);
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.SCALE, scale);
        serializer.write(CodingKeys.HELPER_MODEL, showsHelperModel);
        serializer.write(CodingKeys.ORIGIN, showsOrigin);
        serializer.write(CodingKeys.ITEM_TRANSFORMS, itemTransforms);
    }

    public void deserialize(IDataSerializer serializer) {
        var newScale = serializer.read(CodingKeys.INC_SCALE);
        if (newScale != null) {
            scale = newScale;
        }
        var newHelperModel = serializer.read(CodingKeys.INC_HELPER_MODEL);
        if (newHelperModel != null) {
            showsHelperModel = newHelperModel;
        }
        var newOrigin = serializer.read(CodingKeys.INC_ORIGIN);
        if (newOrigin != null) {
            showsHelperModel = newOrigin;
        }
        var newItemTransforms = serializer.read(CodingKeys.INC_ITEM_TRANSFORMS);
        if (newItemTransforms != null) {
            itemTransforms = newItemTransforms;
        }

    }

    public void applyChanges(CompoundTag tag) {
        deserialize(new TagSerializer(tag));
    }


    public void setShowsHelperModel(boolean value) {
        this.showsHelperModel = value;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_HELPER_MODEL, value);
            listener.documentDidChangeSettings(builder.tag());
        }
    }

    public boolean showsHelperModel() {
        return showsHelperModel;
    }

    public void setShowsOrigin(boolean value) {
        this.showsOrigin = value;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_ORIGIN, value);
            listener.documentDidChangeSettings(builder.tag());
        }
    }

    public boolean showsOrigin() {
        return showsOrigin;
    }

    public void setItemTransforms(OpenItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_ITEM_TRANSFORMS, itemTransforms);
            listener.documentDidChangeSettings(builder.tag());
        }
    }

    public OpenItemTransforms itemTransforms() {
        return itemTransforms;
    }

    protected void setListener(SkinDocumentListener listener) {
        this.listener = listener;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<Float> SCALE = IDataSerializerKey.create("Scale", IDataCodec.FLOAT, 1.0f);
        public static final IDataSerializerKey<Boolean> ORIGIN = IDataSerializerKey.create("Origin", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<Boolean> HELPER_MODEL = IDataSerializerKey.create("HelperModel", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<OpenItemTransforms> ITEM_TRANSFORMS = IDataSerializerKey.create("ItemTransforms", OpenItemTransforms.CODEC, null);

        public static final IDataSerializerKey<Float> INC_SCALE = IDataSerializerKey.create("Scale", IDataCodec.FLOAT, null);
        public static final IDataSerializerKey<Boolean> INC_ORIGIN = IDataSerializerKey.create("Origin", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<Boolean> INC_HELPER_MODEL = IDataSerializerKey.create("HelperModel", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<OpenItemTransforms> INC_ITEM_TRANSFORMS = IDataSerializerKey.create("ItemTransforms", OpenItemTransforms.CODEC, null);
    }
}
