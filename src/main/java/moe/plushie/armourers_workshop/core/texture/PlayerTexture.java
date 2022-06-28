package moe.plushie.armourers_workshop.core.texture;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerTexture {

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

    public static final PlayerTexture DEFAULT = new PlayerTexture(null, null, null);

    private final String url;
    private final ResourceLocation location;

    private String model;
    @OnlyIn(Dist.CLIENT)
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

    public ResourceLocation getLocation() {
        return location;
    }

    public String getURL() {
        return url;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isDownloaded() {
        return texture != null && texture.isLoaded();
    }

    @OnlyIn(Dist.CLIENT)
    public BakedEntityTexture getTexture() {
        if (texture != null && texture.isLoaded()) {
            return texture;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void setTexture(BakedEntityTexture texture) {
        this.texture = texture;
    }
}
