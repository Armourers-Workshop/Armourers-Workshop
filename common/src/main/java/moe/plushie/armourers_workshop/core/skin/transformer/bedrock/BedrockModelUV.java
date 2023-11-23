package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.BiConsumer;

public class BedrockModelUV {

    public static final BedrockModelUV EMPTY = new BedrockModelUV();

    private final Vector2f base;

    private final EnumMap<Direction, Rectangle2f> rects = new EnumMap<>(Direction.class);

    public BedrockModelUV() {
        this.base = null;
    }

    public BedrockModelUV(Vector2f uv) {
        this.base = uv;
    }

    public void forEach(BiConsumer<Direction, Rectangle2f> consumer) {
        if (base == null) {
            rects.forEach(consumer);
        }
    }

    public void put(Direction dir, Rectangle2f rect) {
        rects.put(dir, rect);
    }

    public Vector2f getBase() {
        return base;
    }

    @Nullable
    public Rectangle2f getRect(Direction dir) {
        return rects.get(dir);
    }
}
