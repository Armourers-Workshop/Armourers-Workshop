package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SkinSettings {

    public static final SkinSettings EMPTY = new SkinSettings();

    private boolean isPreviewMode = false;

    private int flags = 0;
    private List<Rectangle3f> collisionBox;
    private SkinItemTransforms itemTransforms;

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


    public void setEditable(boolean isEditable) {
        if (isEditable) {
            flags &= ~0x01;
        } else {
            flags |= 0x01;
        }
    }

    public boolean isEditable() {
        return (flags & 0x01) == 0;
    }

    public void setSavable(boolean isSavable) {
        if (isSavable) {
            flags &= ~0x02;
        } else {
            flags |= 0x02;
        }
    }

    public boolean isSavable() {
        return (flags & 0x02) == 0;
    }

    public void setExportable(boolean isExportable) {
        if (isExportable) {
            flags &= ~0x04;
        } else {
            flags |= 0x04;
        }
    }

    public boolean isExportable() {
        return (flags & 0x04) == 0;
    }


    public void setCompressed(boolean isCompressed) {
        if (isCompressed) {
            flags |= 0x10;
        } else {
            flags &= ~0x10;
        }
    }

    public boolean isCompressed() {
        return (flags & 0x10) != 0;
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

    public String getSecurityData() {
        return securityData;
    }


    public void setItemTransforms(SkinItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
    }

    public SkinItemTransforms getItemTransforms() {
        return itemTransforms;
    }

    public void setCollisionBox(List<Rectangle3f> collisionBox) {
        this.collisionBox = collisionBox;
    }

    public List<Rectangle3f> getCollisionBox() {
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
        settings.setEditable(options.getEditable(settings.isEditable()));
        settings.setSavable(options.getSavable(settings.isSavable()));
        settings.setExportable(options.getExportable(settings.isExportable()));
        settings.setCompressed(options.getCompressed(settings.isCompressed()));
        settings.setSecurityData(options.getSecurityData());
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

        static final DataItem<SkinItemTransforms> ITEM_TRANSFORMS = new DataItem<SkinItemTransforms>(2, null) {
            @Override
            SkinItemTransforms read(IInputStream inputStream) throws IOException {
                var itemTransforms = new SkinItemTransforms();
                var size = inputStream.readVarInt();
                for (int i = 1; i < size; ++i) {
                    var name = inputStream.readString();
                    var translate = inputStream.readVector3f();
                    var rotation = inputStream.readVector3f();
                    var scale = inputStream.readVector3f();
                    itemTransforms.put(name, SkinTransform.create(translate, rotation, scale));
                }
                return itemTransforms;
            }

            @Override
            void write(SkinItemTransforms itemTransforms, IOutputStream outputStream) throws IOException {
                outputStream.writeVarInt(itemTransforms.size() + 1);
                for (var entry : itemTransforms.entrySet()) {
                    var transform = entry.getValue();
                    outputStream.writeString(entry.getKey());
                    outputStream.writeVector3f(transform.getTranslate());
                    outputStream.writeVector3f(transform.getRotation());
                    outputStream.writeVector3f(transform.getScale());
                }
            }
        };

        static final DataItem<List<Rectangle3f>> COLLISION_BOX = new DataItem<List<Rectangle3f>>(3, null) {
            @Override
            List<Rectangle3f> read(IInputStream inputStream) throws IOException {
                var collisionBox = new ArrayList<Rectangle3f>();
                var size = inputStream.readVarInt();
                for (int i = 1; i < size; ++i) {
                    collisionBox.add(inputStream.readRectangle3f());
                }
                return collisionBox;
            }

            @Override
            void write(List<Rectangle3f> collisionBox, IOutputStream outputStream) throws IOException {
                outputStream.writeVarInt(collisionBox.size());
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
