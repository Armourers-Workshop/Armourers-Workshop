package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@Environment(value = EnvType.CLIENT)
public class AbstractRenderSystem extends RenderSystem {

    public static void assertOnRenderThread() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
    }

    public static void setShaderTexture(int i, ResourceLocation texture) {
        Minecraft.getInstance().getTextureManager().bind(texture);
    }

    public static void setShaderColor(float r, float g, float b, float a) {
        color4f(r, g, b, a);
    }

    public static void init() {
    }
}
