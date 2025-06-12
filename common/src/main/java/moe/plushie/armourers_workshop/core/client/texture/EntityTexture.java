package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EntityTexture {

    public static final EntityTexture EMPTY = new EntityTexture(null, null, null);

    private final EntityTextureDescriptor descriptor;
    private final String url;
    private final OpenResourceLocation location;

    private String modelType;

    @Environment(EnvType.CLIENT)
    private BakedEntityTexture texture;

    public EntityTexture(OpenResourceLocation location, String url, String modelType) {
        this(EntityTextureDescriptor.EMPTY, location, url, modelType);
    }

    public EntityTexture(EntityTextureDescriptor descriptor, OpenResourceLocation location, String url, String modelType) {
        this.descriptor = descriptor;
        this.location = location;
        this.modelType = modelType;
        this.url = url;
    }

    public String modelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public EntityTextureDescriptor descriptor() {
        return descriptor;
    }

    public OpenResourceLocation location() {
        return location;
    }

    public String url() {
        return url;
    }

    @Environment(EnvType.CLIENT)
    public boolean isDownloaded() {
        return texture != null && texture.isLoaded();
    }

    @Environment(EnvType.CLIENT)
    public BakedEntityTexture texture() {
        if (texture != null && texture.isLoaded()) {
            return texture;
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public void setTexture(BakedEntityTexture texture) {
        this.texture = texture;
    }
}
