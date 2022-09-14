package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Environment(value = EnvType.CLIENT)
public class AbstractRenderSystem extends RenderSystem {

    private static AbstractRenderPoseStack modelViewStack = new AbstractRenderPoseStack();

    private static Matrix4f textureMatrix = Matrix4f.createScaleMatrix(1, 1, 1);

    public static void assertOnRenderThread() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
    }

    public static void setShaderTexture(int i, ResourceLocation texture) {
        Minecraft.getInstance().getTextureManager().bind(texture);
    }

    public static void setShaderColor(float r, float g, float b, float a) {
        color4f(r, g, b, a);
    }

    public static AbstractRenderPoseStack getModelStack() {
        return modelViewStack;
    }

    public static void applyModelViewMatrix() {
    }

    public static Matrix4f getTextureMatrix() {
        assertOnRenderThread();
        return textureMatrix;
    }

    public static void setTextureMatrix(Matrix4f matrix) {
        if (!isOnRenderThread()) {
            recordRenderCall(() -> _setTextureMatrix(matrix));
        } else {
            _setTextureMatrix(matrix);
        }
    }

    private static void _setTextureMatrix(Matrix4f matrix) {
        textureMatrix = matrix;

    }

    public static void init() {
    }
}
