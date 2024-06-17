package moe.plushie.armourers_workshop.core.texture;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.client.texture.BakedEntityTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class PlayerTexture {

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

    public static final PlayerTexture DEFAULT = new PlayerTexture(null, null, null);

    private final String url;
    private final IResourceLocation location;

    private String model;

    @Environment(EnvType.CLIENT)
    private BakedEntityTexture texture;

    public PlayerTexture(String url, IResourceLocation location, String model) {
//        this.profile = profile;
        this.location = location;
        this.model = model;
        this.url = url;
//        this.texture = texture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public IResourceLocation getLocation() {
        return location;
    }

    public String getURL() {
        return url;
    }

    @Environment(EnvType.CLIENT)
    public boolean isDownloaded() {
        return texture != null && texture.isLoaded();
    }

    @Environment(EnvType.CLIENT)
    public BakedEntityTexture getTexture() {
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
