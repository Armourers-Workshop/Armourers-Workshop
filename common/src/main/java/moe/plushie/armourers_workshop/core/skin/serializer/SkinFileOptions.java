package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.nbt.CompoundTag;

public class SkinFileOptions {

    private final CompoundTag values;
    private final TagSerializer serializer;

    public SkinFileOptions() {
        this(new CompoundTag());
    }

    public SkinFileOptions(CompoundTag compoundTag) {
        this.values = compoundTag;
        this.serializer = new TagSerializer(compoundTag);
    }

    public CompoundTag serializeNBT() {
        return values.copy();
    }

    public void merge(SkinFileOptions options) {
        if (options != null) {
            var fileVersion = Math.max(getFileVersion(), options.getFileVersion());
            values.merge(options.values);
            setFileVersion(fileVersion);
        }
    }


    public void setFileVersion(int fileVersion) {
        serializer.write(CodingKeys.FILE_VERSION, fileVersion);
    }

    public int getFileVersion() {
        return serializer.read(CodingKeys.FILE_VERSION);
    }

    public void setEditable(boolean newValue) {
        serializer.write(CodingKeys.IS_EDITABLE, newValue);
    }

    public boolean getEditable(boolean defaultValue) {
        var value = serializer.read(CodingKeys.IS_EDITABLE);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public boolean isEditable() {
        return getEditable(true);
    }

    public void setSavable(boolean newValue) {
        serializer.write(CodingKeys.IS_SAVABLE, newValue);
    }

    public boolean getSavable(boolean defaultValue) {
        var value = serializer.read(CodingKeys.IS_SAVABLE);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public boolean isSavable() {
        return getSavable(true);
    }

    public void setExportable(boolean newValue) {
        serializer.write(CodingKeys.IS_EXPORTABLE, newValue);
    }

    public boolean getExportable(boolean defaultValue) {
        var value = serializer.read(CodingKeys.IS_EXPORTABLE);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public boolean isExportable() {
        return getExportable(true);
    }


    public void setEncrypted(boolean newValue) {
        serializer.write(CodingKeys.IS_ENCRYPTED, newValue);
    }

    public boolean getEncrypted(boolean defaultValue) {
        var value = serializer.read(CodingKeys.IS_ENCRYPTED);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public boolean isEncrypted() {
        return getEncrypted(false);
    }

    public void setCompressed(boolean newValue) {
        serializer.write(CodingKeys.IS_COMPRESSED, newValue);
    }

    public boolean getCompressed(boolean defaultValue) {
        var value = serializer.read(CodingKeys.IS_COMPRESSED);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public boolean isCompressed() {
        return getCompressed(false);
    }

    public void setSecurityKey(String securityKey) {
        serializer.write(CodingKeys.SECURITY_KEY, securityKey);
    }

    public String getSecurityKey() {
        return serializer.read(CodingKeys.SECURITY_KEY);
    }

    public void setSecurityData(String securityData) {
        serializer.write(CodingKeys.SECURITY_DATA, securityData);
    }

    public String getSecurityData() {
        return serializer.read(CodingKeys.SECURITY_DATA);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinFileOptions options)) return false;
        return values.equals(options.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return values.toString();
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<Integer> FILE_VERSION = IDataSerializerKey.create("FileVersion", IDataCodec.INT, 0);

        public static final IDataSerializerKey<Boolean> IS_EDITABLE = IDataSerializerKey.create("Editable", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<Boolean> IS_SAVABLE = IDataSerializerKey.create("Savable", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<Boolean> IS_EXPORTABLE = IDataSerializerKey.create("Exportable", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<Boolean> IS_ENCRYPTED = IDataSerializerKey.create("Encrypted", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<Boolean> IS_COMPRESSED = IDataSerializerKey.create("Compressed", IDataCodec.BOOL, null);

        public static final IDataSerializerKey<String> SECURITY_KEY = IDataSerializerKey.create("SecurityKey", IDataCodec.STRING, null);
        public static final IDataSerializerKey<String> SECURITY_DATA = IDataSerializerKey.create("SecurityData", IDataCodec.STRING, null);
    }
}
