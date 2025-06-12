package moe.plushie.armourers_workshop.core.skin.property;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SkinSettings {

    public static final SkinSettings EMPTY = new SkinSettings();

    private boolean isPreviewMode = false;

    private int flags = 0;
    private List<OpenRectangle3f> collisionBox;
    private OpenItemTransforms itemTransforms;

    private String securityData;

    public SkinSettings() {
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        var writer = new DataWriter(stream);
        writer.write(DataItem.FLAGS, flags);
        writer.write(DataItem.ITEM_TRANSFORMS, itemTransforms);
        writer.write(DataItem.COLLISION_BOX, collisionBox);
        writer.write(DataItem.SECURITY_DATA, securityData);
        writer.end();
    }

    public void readFromStream(IInputStream stream) throws IOException {
        var reader = new DataReader(stream);
        flags = reader.read(DataItem.FLAGS);
        itemTransforms = reader.read(DataItem.ITEM_TRANSFORMS);
        collisionBox = reader.read(DataItem.COLLISION_BOX);
        securityData = reader.read(DataItem.SECURITY_DATA);
        reader.end();
    }


    public void setEditable(boolean newValue) {
        setFlag(0, !newValue);
    }

    public boolean isEditable() {
        return !getFlag(0);
    }

    public void setSavable(boolean newValue) {
        setFlag(1, !newValue);
    }

    public boolean isSavable() {
        return !getFlag(1);
    }

    public void setExportable(boolean newValue) {
        setFlag(2, !newValue);
    }

    public boolean isExportable() {
        return !getFlag(2);
    }


    public void setEncrypted(boolean newValue) {
        setFlag(3, newValue);
    }

    public boolean isEncrypted() {
        return getFlag(3) || securityData() != null;
    }

    public void setCompressed(boolean newValue) {
        setFlag(4, newValue);
    }

    public boolean isCompressed() {
        return getFlag(4);
    }

    public void setPreviewMode(boolean isPreviewMode) {
        this.isPreviewMode = isPreviewMode;
    }

    public boolean isPreviewMode() {
        return isPreviewMode;
    }

    public void setSecurityData(String securityData) {
        this.securityData = securityData;
    }

    public String securityData() {
        return securityData;
    }


    public void setItemTransforms(OpenItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
    }

    public OpenItemTransforms itemTransforms() {
        return itemTransforms;
    }

    public void setCollisionBox(List<OpenRectangle3f> collisionBox) {
        this.collisionBox = collisionBox;
    }

    public List<OpenRectangle3f> collisionBox() {
        return collisionBox;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinSettings that)) return false;
        return flags == that.flags && Objects.equals(collisionBox, that.collisionBox) && Objects.equals(itemTransforms, that.itemTransforms) && Objects.equals(securityData, that.securityData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags, collisionBox, itemTransforms, securityData);
    }

    @Override
    public String toString() {
        var properties = new LinkedHashMap<String, Object>();
        properties.put("editable", isEditable());
        properties.put("savable", isSavable());
        properties.put("exportable", isExportable());
        properties.put("encrypted", isEncrypted());
        properties.put("compressed", isCompressed());
        if (collisionBox != null && !collisionBox.isEmpty()) {
            properties.put("collisionBox", collisionBox);
        }
        if (itemTransforms != null) {
            properties.putAll(itemTransforms);
        }
        return properties.toString();
    }

    private void setFlag(int bit, boolean value) {
        if (value) {
            flags |= 1 << bit;
        } else {
            flags &= ~(1 << bit);
        }
    }

    private boolean getFlag(int bit) {
        return (flags & (1 << bit)) != 0;
    }

    public SkinSettings copy() {
        var settings = new SkinSettings();
        settings.flags = flags;
        settings.itemTransforms = itemTransforms;
        settings.collisionBox = collisionBox;
        settings.securityData = securityData;
        return settings;
    }

    public SkinSettings copyWithOptions(SkinFileOptions options) {
        var settings = copy();
        settings.setEditable(options.getEditable(isEditable()));
        settings.setSavable(options.getSavable(isSavable()));
        settings.setExportable(options.getExportable(isExportable()));
        settings.setEncrypted(options.getEncrypted(isEncrypted())); // set old security state.
        settings.setCompressed(options.isCompressed());
        settings.setSecurityData(options.securityData()); // set new security data.
        return settings;
    }

    private static abstract class DataItem<T> {

        static final DataItem<Integer> FLAGS = new DataItem<Integer>(1, 0) {
            @Override
            Integer read(IInputStream inputStream) throws IOException {
                return inputStream.readInt();
            }

            @Override
            void write(Integer value, IOutputStream outputStream) throws IOException {
                outputStream.writeInt(value);
            }
        };

        static final DataItem<OpenItemTransforms> ITEM_TRANSFORMS = new DataItem<OpenItemTransforms>(2, null) {
            @Override
            OpenItemTransforms read(IInputStream inputStream) throws IOException {
                var itemTransforms = new OpenItemTransforms();
                var size = inputStream.readVarInt();
                for (int i = 1; i < size; ++i) {
                    var name = inputStream.readString();
                    var translate = inputStream.readVector3f();
                    var rotation = inputStream.readVector3f();
                    var scale = inputStream.readVector3f();
                    itemTransforms.put(name, OpenTransform3f.create(translate, rotation, scale));
                }
                return itemTransforms;
            }

            @Override
            void write(OpenItemTransforms itemTransforms, IOutputStream outputStream) throws IOException {
                outputStream.writeVarInt(itemTransforms.size() + 1);
                for (var entry : itemTransforms.entrySet()) {
                    var transform = entry.getValue();
                    outputStream.writeString(entry.getKey());
                    outputStream.writeVector3f(transform.translate());
                    outputStream.writeVector3f(transform.rotation());
                    outputStream.writeVector3f(transform.scale());
                }
            }
        };

        static final DataItem<List<OpenRectangle3f>> COLLISION_BOX = new DataItem<List<OpenRectangle3f>>(3, null) {
            @Override
            List<OpenRectangle3f> read(IInputStream inputStream) throws IOException {
                var collisionBox = new ArrayList<OpenRectangle3f>();
                var size = inputStream.readVarInt();
                for (int i = 1; i < size; ++i) {
                    collisionBox.add(inputStream.readRectangle3f());
                }
                return collisionBox;
            }

            @Override
            void write(List<OpenRectangle3f> collisionBox, IOutputStream outputStream) throws IOException {
                outputStream.writeVarInt(collisionBox.size() + 1);
                for (var box : collisionBox) {
                    outputStream.writeRectangle3f(box);
                }
            }
        };

        static final DataItem<String> SECURITY_DATA = new DataItem<String>(4, null) {
            @Override
            String read(IInputStream inputStream) throws IOException {
                return inputStream.readString();
            }

            @Override
            void write(String value, IOutputStream outputStream) throws IOException {
                outputStream.writeString(value);
            }
        };

        final int id;
        final T defaultValue;

        private DataItem(int id, T defaultValue) {
            this.id = id;
            this.defaultValue = defaultValue;
        }

        abstract T read(IInputStream inputStream) throws IOException;

        abstract void write(T value, IOutputStream outputStream) throws IOException;
    }

    private static class DataWriter {

        private final IOutputStream outputStream;

        DataWriter(IOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        <T> void write(DataItem<T> item, T value) throws IOException {
            if (!Objects.equals(value, item.defaultValue)) {
                outputStream.writeVarInt(item.id);
                item.write(value, outputStream);
            }
        }

        void end() throws IOException {
            outputStream.writeVarInt(0);
        }
    }

    private static class DataReader {

        private int id = -1;
        private final IInputStream inputStream;

        DataReader(IInputStream inputStream) {
            this.inputStream = inputStream;
        }

        <T> T read(DataItem<T> item) throws IOException {
            if (id == -1) {
                id = inputStream.readVarInt();
            }
            if (id == item.id) {
                T value = item.read(inputStream);
                id = inputStream.readVarInt();
                return value;
            }
            return item.defaultValue;
        }

        void end() throws IOException {
            if (id != 0) {
                // found some unknown data, abort?
            }
        }
    }
}
