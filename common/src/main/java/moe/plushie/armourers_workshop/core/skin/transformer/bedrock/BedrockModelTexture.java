package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.api.common.IResource;
import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import moe.plushie.armourers_workshop.utils.texture.TextureData;

import java.io.IOException;

public class BedrockModelTexture {

    protected TextureData textureData;

    public BedrockModelTexture() {
    }

    public static BedrockModelTexture simpleTexture(BedrockModelGeometry geometry, IResource textureFile) throws IOException {
        BedrockModelTexture texture = new BedrockModelTexture();
        texture.load(geometry, textureFile);
        return texture;
    }

    public void load(BedrockModelGeometry geometry, IResource textureFile) throws IOException {
        this.textureData = new TextureData(textureFile.getName(), geometry.getTextureWidth(), geometry.getTextureHeight());
        this.textureData.load(textureFile.getInputStream());
    }

    public TextureBox read(BedrockModelCube cube) {
        Size3f size = cube.getSize();
        BedrockModelUV uv = cube.getUV();
        TextureBox skyBox = new TextureBox(size.getWidth(), size.getHeight(), size.getDepth(), cube.isMirror(), uv.getBase(), textureData);
        uv.forEach(skyBox::put);
        return skyBox;
    }
}
