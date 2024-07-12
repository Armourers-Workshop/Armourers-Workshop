package moe.plushie.armourers_workshop.core.skin.property;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkinSettings {

    private int dataVersion = 1;
    private boolean isPreviewMode = false;
    private boolean isEditable = true;
    private List<Rectangle3i> collisionBox;
    private SkinItemTransforms itemTransforms;

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeVarInt(dataVersion);
        writeItemTransform(stream);
        writeCollisionBox(stream);
        stream.writeBoolean(isEditable);
    }

    public void readFromStream(IInputStream stream) throws IOException {
        dataVersion = stream.readVarInt();
        readItemTransforms(stream);
        readCollisionBox(stream);
        isEditable = stream.readBoolean();
    }

    // TODO: @SAGESSE data migration for the internal-test version, and will be removed in later versions.
    public void readFromLegacyStream(IInputStream stream) throws IOException {
        readItemTransforms(stream);
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

    public void setCollisionBox(List<Rectangle3i> collisionBox) {
        this.collisionBox = collisionBox;
    }

    public List<Rectangle3i> getCollisionBox() {
        return collisionBox;
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

    private void writeItemTransform(IOutputStream stream) throws IOException {
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
    }

    private void readItemTransforms(IInputStream stream) throws IOException {
        int size = stream.readVarInt();
        if (size != 0) {
            itemTransforms = new SkinItemTransforms();
            for (int i = 1; i < size; ++i) {
                String name = stream.readString();
                Vector3f translate = stream.readVector3f();
                Vector3f rotation = stream.readVector3f();
                Vector3f scale = stream.readVector3f();
                itemTransforms.put(name, SkinTransform.create(translate, rotation, scale));
            }
        } else {
            itemTransforms = null;
        }
    }

    private void writeCollisionBox(IOutputStream stream) throws IOException {
        if (collisionBox != null) {
            stream.writeVarInt(collisionBox.size() + 1);
            for (var box : collisionBox) {
                stream.writeRectangle3i(box);
            }
        } else {
            stream.writeVarInt(0);
        }
    }

    private void readCollisionBox(IInputStream stream) throws IOException {
        int size = stream.readVarInt();
        if (size != 0) {
            collisionBox = new ArrayList<>();
            for (int i = 1; i < size; ++i) {
                collisionBox.add(stream.readRectangle3i());
            }
        } else {
            collisionBox = null;
        }
    }
}
