package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelUV;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import net.minecraft.core.Direction;

import java.util.EnumMap;

public class BlockBenchModelUV extends BedrockModelUV {

    private int defaultTextureId = -1;
    private EnumMap<Direction, Integer> textureIds;

    public BlockBenchModelUV(Vector2f uv) {
        super(uv);
    }

    public void setTextureId(Direction dir, int textureId) {
        if (textureIds == null) {
            textureIds = new EnumMap<>(Direction.class);
        }
        textureIds.put(dir, textureId);
    }

    public int getTextureId(Direction dir) {
        if (textureIds != null) {
            return textureIds.getOrDefault(dir, defaultTextureId);
        }
        return defaultTextureId;
    }

    public void setDefaultTextureId(int defaultTextureId) {
        this.defaultTextureId = defaultTextureId;
    }

    public int getDefaultTextureId() {
        return defaultTextureId;
    }
}
