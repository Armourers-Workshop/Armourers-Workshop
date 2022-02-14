package moe.plushie.armourers_workshop.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class CustomSpriteUploader extends SpriteUploader {

    public CustomSpriteUploader(ResourceLocation name) {
        super(Minecraft.getInstance().getTextureManager(), name, "items");
    }

    protected Stream<ResourceLocation> getResourcesToLoad() {
        return Registry.MOB_EFFECT.keySet().stream();
    }

    public TextureAtlasSprite get(Effect effect) {
        return this.getSprite(Registry.MOB_EFFECT.getKey(effect));
    }
}