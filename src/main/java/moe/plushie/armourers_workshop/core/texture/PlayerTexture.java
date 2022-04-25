package moe.plushie.armourers_workshop.core.texture;

import net.minecraft.util.ResourceLocation;

public class PlayerTexture {

    public static final PlayerTexture DEFAULT = new PlayerTexture(null, null, null);

    private final String url;
    private final ResourceLocation location;

    private String model;
    private BakedEntityTexture texture;

    public PlayerTexture(String url, ResourceLocation location, String model) {
//        this.profile = profile;
        this.location = location;
        this.model = model;
        this.url = url;
//        this.texture = texture;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public BakedEntityTexture getTexture() {
        if (texture != null && texture.isLoaded()) {
            return texture;
        }
        return null;
    }

    public void setTexture(BakedEntityTexture texture) {
        this.texture = texture;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public String getURL() {
        return url;
    }
}
