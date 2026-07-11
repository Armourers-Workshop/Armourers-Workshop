package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinTextureBox;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class SkinTextureBox implements ISkinTextureBox {

    private final OpenVector2f texturePos;
    private final SkinTextureData defaultTexture;

    private final float width;
    private final float height;
    private final float depth;

    private final boolean mirror;

    private EnumMap<OpenDirection, OpenRectangle2f> variantRects;
    private EnumMap<OpenDirection, SkinTextureOptions> variantOptions;
    private EnumMap<OpenDirection, SkinTextureData> variantTextures;

    public SkinTextureBox(float width, float height, float depth, boolean mirror, @Nullable OpenVector2f baseUV, @Nullable SkinTextureData defaultTexture) {
        this.texturePos = baseUV;
        this.defaultTexture = defaultTexture;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mirror = mirror;
    }

    public void putTextureRect(OpenDirection dir, OpenRectangle2f rect) {
        if (variantRects == null) {
            variantRects = new EnumMap<>(OpenDirection.class);
        }
        variantRects.put(dir, rect);
    }

    public void putTextureOptions(OpenDirection dir, SkinTextureOptions options) {
        if (variantOptions == null) {
            variantOptions = new EnumMap<>(OpenDirection.class);
        }
        variantOptions.put(dir, options);
    }

    public void putTextureProvider(OpenDirection dir, SkinTextureData textureProvider) {
        if (variantTextures == null) {
            variantTextures = new EnumMap<>(OpenDirection.class);
        }
        variantTextures.put(dir, textureProvider);
    }

    public SkinTextureBox separated() {
        var box = new SkinTextureBox(width, height, depth, mirror, null, defaultTexture);
        for (var dir : OpenDirection.values()) {
            var key = getTexture(dir);
            if (key == null) {
                continue;
            }
            box.putTextureRect(dir, new OpenRectangle2f(key.u(), key.v(), key.width(), key.height()));
            if (key.data() == defaultTexture) {
                continue;
            }
            box.putTextureProvider(dir, key.data());
        }
        return box;
    }

    @Nullable
    @Override
    public SkinTexturePos getTexture(IDirection dir) {
        return getTexture((OpenDirection) dir);
    }

    public SkinTexturePos getTexture(OpenDirection dir) {
        // when mirroring occurs, the contents of the WEST and EAST sides will be swapped.
        if (mirror) {
            return getMirrorTexture(dir);
        }
        return switch (dir) {
            case UP -> makeTexture(dir, depth, 0, width, depth);
            case DOWN -> makeTexture(dir, depth + width, 0, width, depth);
            case NORTH -> makeTexture(dir, depth, depth, width, height);
            case SOUTH -> makeTexture(dir, depth + width + depth, depth, width, height);
            case WEST -> makeTexture(dir, depth + width, depth, depth, height);
            case EAST -> makeTexture(dir, 0, depth, depth, height);
        };
    }

    private SkinTexturePos getMirrorTexture(OpenDirection dir) {
        return switch (dir) {
            case UP -> makeTexture(dir, depth + width, 0, -width, depth);
            case DOWN -> makeTexture(dir, depth + width + width, 0, -width, depth);
            case NORTH -> makeTexture(dir, depth + width, depth, -width, height);
            case SOUTH -> makeTexture(dir, depth + width + depth + width, depth, -width, height);
            case WEST -> makeTexture(dir, 0 + depth, depth, -depth, height);
            case EAST -> makeTexture(dir, depth + width + depth, depth, -depth, height);
        };
    }

    @Nullable
    private SkinTexturePos makeTexture(OpenDirection dir, float u, float v, float s, float t) {
        var texture = getTextureProvider(dir);
        if (texture == null) {
            return null;
        }
        // specifies the uv origin for the face.
        var rect = getTextureRect(dir);
        if (rect != null) {
            var options = getTextureOptions(dir);
            return new SkinTexturePos(rect.x(), rect.y(), rect.width(), rect.height(), options, texture);
        }
        var pos = texturePos;
        if (pos != null) {
            return new Entry(pos.x() + u, pos.y() + v, s, t, texture, pos);
        }
        return null;
    }

    @Nullable
    private OpenRectangle2f getTextureRect(OpenDirection dir) {
        if (variantRects != null) {
            return variantRects.get(dir);
        }
        return null;
    }

    private SkinTextureOptions getTextureOptions(OpenDirection dir) {
        if (variantOptions != null) {
            return variantOptions.get(dir);
        }
        return null;
    }

    private SkinTextureData getTextureProvider(OpenDirection dir) {
        if (variantTextures != null) {
            return variantTextures.getOrDefault(dir, defaultTexture);
        }
        return defaultTexture;
    }

    public static class Entry extends SkinTexturePos {

        protected final OpenVector2f parent;

        public Entry(float u, float v, float width, float height, SkinTextureData textureData, OpenVector2f parent) {
            super(u, v, width, height, textureData);
            this.parent = parent;
        }

        public OpenVector2f parent() {
            return parent;
        }
    }
}
