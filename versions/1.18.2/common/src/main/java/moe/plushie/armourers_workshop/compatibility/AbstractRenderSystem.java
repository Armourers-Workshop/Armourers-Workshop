package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AbstractRenderSystem extends RenderSystem {

//    private static Matrix4f savedTextureMatrix;
//
    //        com.mojang.blaze3d.systems.RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
//        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

//    public static void setShaderColor(float r, float g, float b, float a) {
    // RenderSystem.sss
//    }

//    public static void setShaderTexture(int i, ResourceLocation texture) {
//        Minecraft.getInstance().getTextureManager().bind(texture);
//    }

//    public static abstract class Shader extends GameRenderer {
    
    private static AbstractRenderPoseStack modelStack = new AbstractRenderPoseStack();

    public static AbstractRenderPoseStack getModelStack() {
        return modelStack;
    }

    public static void init() {
        AbstractShaders.initShader();
    }
}
