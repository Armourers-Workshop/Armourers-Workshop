package moe.plushie.armourers_workshop.utils.texture;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class TextureBox {

    private final Vector2f texturePos;
    private final ITextureProvider textureProvider;

    private final float width;
    private final float height;
    private final float depth;

    private final boolean mirror;

    private EnumMap<Direction, ITextureProvider> textureProviders;
    private EnumMap<Direction, Rectangle2f> textureRects;

    public TextureBox(float width, float height, float depth, boolean mirror, @Nullable Vector2f baseUV, ITextureProvider textureProvider) {
        this.texturePos = baseUV;
        this.textureProvider = textureProvider;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mirror = mirror;
    }

    public void put(Direction dir, ITextureProvider textureProvider) {
        if (textureProviders == null) {
            textureProviders = new EnumMap<>(Direction.class);
        }
        textureProviders.put(dir, textureProvider);
    }

    public void put(Direction dir, Rectangle2f rect) {
        if (textureRects == null) {
            textureRects = new EnumMap<>(Direction.class);
        }
        textureRects.put(dir, rect);
    }

    public TextureBox separated() {
        TextureBox box = new TextureBox(width, height, depth, mirror, null, textureProvider);
        for (Direction dir : Direction.values()) {
            ITextureKey key = getTexture(dir);
            if (key == null) {
                continue;
            }
            box.put(dir, new Rectangle2f(key.getU(), key.getV(), key.getWidth(), key.getHeight()));
            if (key.getProvider() == textureProvider) {
                continue;
            }
            box.put(dir, key.getProvider());
        }
        return box;
    }

    @Nullable
    public ITextureKey getTexture(Direction dir) {
        Direction dir1 = resolveDirection(dir);
        switch (dir1) {
            case UP: {
                return makeTexture(dir1, depth, 0, width, depth);
            }
            case DOWN: {
                return makeTexture(dir1, depth + width, 0, width, depth);
            }
            case NORTH: {
                return makeTexture(dir1, depth, depth, width, height);
            }
            case SOUTH: {
                return makeTexture(dir1, depth + width + depth, depth, width, height);
            }
            case WEST: {
                return makeTexture(dir1, depth + width, depth, depth, height);
            }
            case EAST: {
                return makeTexture(dir1, 0, depth, depth, height);
            }
        }
        return null;
    }

    private Direction resolveDirection(Direction dir) {
        // when mirroring occurs, the contents of the WEST and EAST sides will be swapped.
        if (mirror && dir.getAxis() == Direction.Axis.X) {
            return dir.getOpposite();
        }
        return dir;
    }

    @Nullable
    private ITextureKey makeTexture(Direction dir, float u, float v, float s, float t) {
        float x = 0;
        float y = 0;
        float width = s;
        float height = t;
        // specifies the uv origin for the face.
        Rectangle2f rect = getTextureRect(dir);
        if (rect != null) {
            x = rect.getX();
            y = rect.getY();
            width = rect.getWidth();
            height = rect.getHeight();
        } else {
            if (texturePos == null) {
                return null;
            }
            x = texturePos.getX() + u;
            y = texturePos.getY() + v;
        }
        if (rect == null) {
            return new Slice(x, y, width, height, getTextureProvider(dir), texturePos);
        }
        return new TextureKey(x, y, width, height, getTextureProvider(dir));
    }

    @Nullable
    private Rectangle2f getTextureRect(Direction dir) {
        if (textureRects != null) {
            return textureRects.get(dir);
        }
        return null;
    }

    private ITextureProvider getTextureProvider(Direction dir) {
        if (textureProviders != null) {
            return textureProviders.getOrDefault(dir, textureProvider);
        }
        return textureProvider;
    }

    public static class Slice extends TextureKey {

        protected final Vector2f parent;

        public Slice(float u, float v, float width, float height, ITextureProvider provider, Vector2f parent) {
            super(u, v, width, height, provider);
            this.parent = parent;
        }

        public Vector2f getParent() {
            return parent;
        }
    }
}
