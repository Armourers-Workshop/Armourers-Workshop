package moe.plushie.armourers_workshop.utils.texture;

import moe.plushie.armourers_workshop.api.common.ITextureBox;
import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.common.ITextureOptions;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class TextureBox implements ITextureBox {

    private final Vector2f texturePos;
    private final ITextureProvider defaultTexture;

    private final float width;
    private final float height;
    private final float depth;

    private final boolean mirror;

    private EnumMap<Direction, Rectangle2f> variantRects;
    private EnumMap<Direction, ITextureOptions> variantOptions;
    private EnumMap<Direction, ITextureProvider> variantTextures;

    public TextureBox(float width, float height, float depth, boolean mirror, @Nullable Vector2f baseUV, @Nullable ITextureProvider defaultTexture) {
        this.texturePos = baseUV;
        this.defaultTexture = defaultTexture;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mirror = mirror;
    }

    public void putTextureRect(Direction dir, Rectangle2f rect) {
        if (variantRects == null) {
            variantRects = new EnumMap<>(Direction.class);
        }
        variantRects.put(dir, rect);
    }

    public void putTextureOptions(Direction dir, ITextureOptions options) {
        if (variantOptions == null) {
            variantOptions = new EnumMap<>(Direction.class);
        }
        variantOptions.put(dir, options);
    }

    public void putTextureProvider(Direction dir, ITextureProvider textureProvider) {
        if (variantTextures == null) {
            variantTextures = new EnumMap<>(Direction.class);
        }
        variantTextures.put(dir, textureProvider);
    }

    public TextureBox separated() {
        TextureBox box = new TextureBox(width, height, depth, mirror, null, defaultTexture);
        for (Direction dir : Direction.values()) {
            ITextureKey key = getTexture(dir);
            if (key == null) {
                continue;
            }
            box.putTextureRect(dir, new Rectangle2f(key.getU(), key.getV(), key.getWidth(), key.getHeight()));
            if (key.getProvider() == defaultTexture) {
                continue;
            }
            box.putTextureProvider(dir, key.getProvider());
        }
        return box;
    }

    @Nullable
    @Override
    public ITextureKey getTexture(Direction dir) {
        // when mirroring occurs, the contents of the WEST and EAST sides will be swapped.
        if (mirror) {
            return getMirrorTexture(dir);
        }
        switch (dir) {
            case UP: {
                return makeTexture(dir, depth, 0, width, depth);
            }
            case DOWN: {
                return makeTexture(dir, depth + width, 0, width, depth);
            }
            case NORTH: {
                return makeTexture(dir, depth, depth, width, height);
            }
            case SOUTH: {
                return makeTexture(dir, depth + width + depth, depth, width, height);
            }
            case WEST: {
                return makeTexture(dir, depth + width, depth, depth, height);
            }
            case EAST: {
                return makeTexture(dir, 0, depth, depth, height);
            }
        }
        return null;
    }

    private ITextureKey getMirrorTexture(Direction dir) {
        switch (dir) {
            case UP: {
                return makeTexture(dir, depth + width, 0, -width, depth);
            }
            case DOWN: {
                return makeTexture(dir, depth + width + width, 0, -width, depth);
            }
            case NORTH: {
                return makeTexture(dir, depth + width, depth, -width, height);
            }
            case SOUTH: {
                return makeTexture(dir, depth + width + depth + width, depth, -width, height);
            }
            case WEST: {
                return makeTexture(dir, 0 + depth, depth, -depth, height);
            }
            case EAST: {
                return makeTexture(dir, depth + width + depth, depth, -depth, height);
            }
        }
        return null;
    }

    @Nullable
    private ITextureKey makeTexture(Direction dir, float u, float v, float s, float t) {
        ITextureProvider texture = getTextureProvider(dir);
        if (texture == null) {
            return null;
        }
        // specifies the uv origin for the face.
        Rectangle2f rect = getTextureRect(dir);
        if (rect != null) {
            ITextureOptions options = getTextureOptions(dir);
            return new TextureKey(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), options, texture);
        }
        Vector2f pos = texturePos;
        if (pos != null) {
            return new Entry(pos.getX() + u, pos.getY() + v, s, t, texture, pos);
        }
        return null;
    }

    @Nullable
    private Rectangle2f getTextureRect(Direction dir) {
        if (variantRects != null) {
            return variantRects.get(dir);
        }
        return null;
    }

    private ITextureOptions getTextureOptions(Direction dir) {
        if (variantOptions != null) {
            return variantOptions.get(dir);
        }
        return null;
    }

    private ITextureProvider getTextureProvider(Direction dir) {
        if (variantTextures != null) {
            return variantTextures.getOrDefault(dir, defaultTexture);
        }
        return defaultTexture;
    }

    public static class Entry extends TextureKey {

        protected final Vector2f parent;

        public Entry(float u, float v, float width, float height, ITextureProvider provider, Vector2f parent) {
            super(u, v, width, height, provider);
            this.parent = parent;
        }

        public Vector2f getParent() {
            return parent;
        }
    }
}
