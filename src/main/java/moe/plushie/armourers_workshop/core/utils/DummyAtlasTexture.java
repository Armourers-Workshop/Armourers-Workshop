package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class DummyAtlasTexture extends AtlasTexture {

    public static AtlasTexture TEX_ITEMS = new DummyAtlasTexture(SkinCore.TEX_ITEMS);

    DummyAtlasTexture(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    HashMap<ResourceLocation, DummyAtlasTextureSprite> sprites = new HashMap<>();

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return sprites.computeIfAbsent(location, k -> new DummyAtlasTextureSprite(new DummyAtlasTexture(location), 16, 16));
    }

    static class DummyAtlasTextureSprite extends TextureAtlasSprite {
        DummyAtlasTextureSprite(AtlasTexture texture, int width, int height) {
            super(texture, new Info(texture.location(), width, height, AnimationMetadataSection.EMPTY), 0, width, height, 0, 0, new NativeImage(width, height, false));
        }
    }
}
