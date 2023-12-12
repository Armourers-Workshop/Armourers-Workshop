package moe.plushie.armourers_workshop.core.skin.property;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.IOException;
import java.util.Map;

public class SkinSettings {

    private boolean isPreviewMode = false;
    private boolean isEditable = true;
    private SkinItemTransforms itemTransforms;

    public void writeToStream(IOutputStream stream) throws IOException {
        if (itemTransforms != null) {
            stream.writeVarInt(itemTransforms.size() + 1);
            for (Map.Entry<String, ITransformf> entry : itemTransforms.entrySet()) {
                ITransformf transform = entry.getValue();
                stream.writeString(entry.getKey());
                stream.writeVector3f(transform.getTranslate());
                stream.writeVector3f(transform.getRotation());
                stream.writeVector3f(transform.getScale());
            }
        } else {
            stream.writeVarInt(0);
        }
        stream.writeBoolean(isEditable);
    }

    public void readFromStream(IInputStream stream) throws IOException {
        int itemTransformSize = stream.readVarInt();
        if (itemTransformSize != 0) {
            itemTransforms = new SkinItemTransforms();
            for (int i = 1; i < itemTransformSize; ++i) {
                String name = stream.readString();
                Vector3f translate = stream.readVector3f();
                Vector3f rotation = stream.readVector3f();
                Vector3f scale = stream.readVector3f();
                itemTransforms.put(name, SkinTransform.create(translate, rotation, scale));
            }
        } else {
            itemTransforms = null;
        }
        isEditable = stream.readBoolean();
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setPreviewMode(boolean isPreviewMode) {
        this.isPreviewMode = isPreviewMode;
    }

    public boolean isPreviewMode() {
        return isPreviewMode;
    }

    public void setItemTransforms(SkinItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
    }

    public SkinItemTransforms getItemTransforms() {
        return itemTransforms;
    }

    public boolean isEmpty() {
        return isEditable && itemTransforms == null;
    }

    public SkinSettings copy() {
        SkinSettings settings = new SkinSettings();
        settings.isEditable = isEditable;
        settings.isPreviewMode = isPreviewMode;
        settings.itemTransforms = itemTransforms;
        return settings;
    }
}
