package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PlayerSkinPart {

    private final OpenResourceLocation texture;
    private final String url;

    public PlayerSkinPart(OpenResourceLocation texture) {
        this(texture, null);
    }

    public PlayerSkinPart(OpenResourceLocation texture, String url) {
        this.texture = texture;
        this.url = url;
    }

    public OpenResourceLocation texture() {
        return texture;
    }

    @Nullable
    public String url() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerSkinPart that)) return false;
        return Objects.equals(texture, that.texture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texture);
    }

    @Override
    public String toString() {
        return Objects.toString(this, "texture", texture, "url", url);
    }
}
