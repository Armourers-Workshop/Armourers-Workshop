package moe.plushie.armourers_workshop.core.skin.transformer;

import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModel;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelAnimation;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelGeometry;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public interface SkinPackModelReader {

    BedrockModel readModel() throws IOException;

    BedrockModelTexture readTexture(BedrockModelGeometry geometry) throws IOException;

    @Nullable
    Map<String, BedrockModelAnimation> getAnimations();

    @Nullable
    Map<String, BedrockTransform> getTransforms();

    SkinPack getPack();
}
