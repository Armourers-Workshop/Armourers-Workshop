package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

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


    public static void glGenVertexArrays(IntConsumer consumer) {
        // ignored
    }

    public static void glBindVertexArray(IntSupplier supplier) {
        // ignored
    }

    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GL11.glDisable(GL15.GL_RESCALE_NORMAL);
    }

    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GL11.glEnable(GL15.GL_RESCALE_NORMAL);
    }

    public static void applyModelViewMatrix() {
    }

    public static void mulMatrix(IMatrix4f matrix4f) {
        multMatrix(MatrixUtils.of(matrix4f));
    }

    public static void init() {
    }
}
