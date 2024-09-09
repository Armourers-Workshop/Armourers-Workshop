package moe.plushie.armourers_workshop.core.skin.serializer;

import net.minecraft.nbt.CompoundTag;

public class SkinFileOptions {

    private final CompoundTag values;

    public SkinFileOptions() {
        this.values = new CompoundTag();
    }

    public SkinFileOptions(CompoundTag compoundTag) {
        this.values = compoundTag;
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
        values.putOptionalInt("FileVersion", fileVersion, 0);
    }

    public int getFileVersion() {
        return values.getOptionalInt("FileVersion", 0);
    }

    public void setEditable(boolean isEditable) {
        values.putOptionalBoolean("Editable", isEditable, true);
    }

    public boolean getEditable(boolean defaultValue) {
        return values.getOptionalBoolean("Editable", defaultValue);
    }


    public void setSavable(boolean isSavable) {
        values.putOptionalBoolean("Savable", isSavable, true);
    }

    public boolean getSavable(boolean defaultValue) {
        return values.getOptionalBoolean("Savable", defaultValue);
    }

    public void setExportable(boolean isExportable) {
        values.putOptionalBoolean("Exportable", isExportable, true);
    }

    public boolean getExportable(boolean defaultValue) {
        return values.getOptionalBoolean("Exportable", defaultValue);
    }


    public void setCompressed(boolean compressed) {
        values.putOptionalBoolean("Compressed", compressed, false);
    }

    public boolean getCompressed(boolean defaultValue) {
        return values.getOptionalBoolean("Compressed", defaultValue);
    }

    public void setSecurityKey(String securityKey) {
        values.putOptionalString("SecurityKey", securityKey, null);
    }

    public String getSecurityKey() {
        return values.getOptionalString("SecurityKey", null);
    }

    public void setSecurityData(String securityData) {
        values.putOptionalString("SecurityData", securityData, null);
    }

    public String getSecurityData() {
        return values.getOptionalString("SecurityData", null);
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
}
