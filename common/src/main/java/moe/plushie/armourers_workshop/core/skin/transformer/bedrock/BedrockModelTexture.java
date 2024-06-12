package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import moe.plushie.armourers_workshop.utils.texture.TextureData;
import moe.plushie.armourers_workshop.utils.texture.TextureOptions;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

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
        return getTextureBox(cube, cube.getUV(), getTextureData(cube));
    }

    protected TextureBox getTextureBox(BedrockModelCube cube, BedrockModelUV uv, TextureData textureData) {
        Size3f size = cube.getSize();
        TextureBox skyBox = new TextureBox(size.getWidth(), size.getHeight(), size.getDepth(), cube.isMirror(), uv.getBase(), textureData);
        uv.forEach((dir, rect) -> {
            skyBox.putTextureRect(dir, rect);
            skyBox.putTextureProvider(dir, getTextureData(cube, dir));
        });
        uv.forEachRotations((dir, rot) -> {
            TextureOptions options = new TextureOptions();
            options.setRotation(rot);
            skyBox.putTextureOptions(dir, options);
        });
        return skyBox;
    }

    @Nullable
    protected TextureData getTextureData(BedrockModelCube cube) {
        return textureData;
    }

    @Nullable
    protected TextureData getTextureData(BedrockModelCube cube, Direction dir) {
        return textureData;
    }
}
