package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.api.skin.serializer.ISkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

public class SkinFileHeader implements ISkinFileHeader {

    private final int version;
    private final SkinType type;
    private final SkinProperties properties;

    private int lastModified = 0;

    public SkinFileHeader(int version, SkinType type, SkinProperties properties) {
        this.version = version;
        this.type = type;
        this.properties = properties;
    }

    public static SkinFileHeader of(int version, SkinType type, SkinProperties properties) {
        return new SkinFileHeader(version, type, properties);
    }

    public static SkinFileHeader optimized(int version, SkinType type, SkinProperties properties) {
        var result = new SkinProperties();
        if (properties != null) {
            result.put(SkinProperty.ALL_CUSTOM_NAME, properties.get(SkinProperty.ALL_CUSTOM_NAME));
            result.put(SkinProperty.ALL_AUTHOR_NAME, properties.get(SkinProperty.ALL_AUTHOR_NAME));
            result.put(SkinProperty.ALL_FLAVOUR_TEXT, properties.get(SkinProperty.ALL_FLAVOUR_TEXT));
            result.put(SkinProperty.SECURITY_DATA, properties.get(SkinProperty.SECURITY_DATA));
        }
        return of(version, type, properties);
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public SkinType type() {
        return type;
    }

    @Override
    public SkinProperties properties() {
        return properties;
    }

    public void setLastModified(int lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int lastModified() {
        return lastModified;
    }
}
