package moe.plushie.armourers_workshop.core.skin.data.serialize;

import moe.plushie.armourers_workshop.api.skin.ISkinFileHeader;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;

public class SkinFileHeader implements ISkinFileHeader {

    private final int version;
    private final ISkinType type;
    private final ISkinProperties properties;

    private int lastModified = 0;

    public SkinFileHeader(int version, ISkinType type, ISkinProperties properties) {
        this.version = version;
        this.type = type;
        this.properties = properties;
    }

    public static SkinFileHeader of(int version, ISkinType type, ISkinProperties properties) {
        return new SkinFileHeader(version, type, properties);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public ISkinType getType() {
        return type;
    }

    @Override
    public ISkinProperties getProperties() {
        return properties;
    }

    public void setLastModified(int lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int getLastModified() {
        return lastModified;
    }
}
