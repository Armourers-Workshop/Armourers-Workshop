package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelUV;
import moe.plushie.armourers_workshop.utils.math.Vector2f;

public class BlockBenchModelUV extends BedrockModelUV {

    private int textureId;

    public BlockBenchModelUV(Vector2f uv) {
        super(uv);
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public int getTextureId() {
        return textureId;
    }
}
