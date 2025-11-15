package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

@OnlyIn(Dist.CLIENT)
public class EntityTexture {

    public static final EntityTexture EMPTY = new EntityTexture(null, null, null);

    private final EntityTextureDescriptor descriptor;
    private final OpenResourceLocation location;
    private final EntityTextureDescriptor.Model model;
    private final String url;

    private OpenNativeImage image;

    public EntityTexture(OpenResourceLocation location, String url, EntityTextureDescriptor.Model model) {
        this(EntityTextureDescriptor.EMPTY, location, url, model);
    }

    public EntityTexture(EntityTextureDescriptor descriptor, OpenResourceLocation location, String url, EntityTextureDescriptor.Model model) {
        this.descriptor = descriptor;
        this.location = location;
        this.model = model;
        this.url = url;
    }

    public void setImage(OpenNativeImage image) {
        this.image = image;
    }

    public OpenNativeImage image() {
        return image;
    }

    public EntityTextureDescriptor descriptor() {
        return descriptor;
    }

    public OpenResourceLocation location() {
        return location;
    }

    public EntityTextureDescriptor.Model model() {
        return model;
    }

    public String url() {
        return url;
    }
}
