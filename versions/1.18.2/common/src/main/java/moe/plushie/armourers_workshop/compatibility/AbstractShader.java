package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Environment(value = EnvType.CLIENT)
public class AbstractShader extends ShaderInstance {

    @Nullable
    public final Uniform LIGHT_MODULATOR;

    @Nullable
    public final Uniform INVERSE_NORMAL_MATRIX;

    public AbstractShader(ResourceManager resourceManager, String string, VertexFormat vertexFormat) throws IOException {
        super(_getResourceProvider(resourceManager), string, vertexFormat);
        this.LIGHT_MODULATOR = getUniform("LightModulator");
        this.INVERSE_NORMAL_MATRIX = getUniform("INormalMat");
    }

    protected static ResourceProvider _getResourceProvider(ResourceManager resourceManager) {
        return rl -> {
            ResourceLocation nrl = ModConstants.key(rl.getPath());
            if (resourceManager.hasResource(nrl)) {
                return resourceManager.getResource(nrl);
            }
            return resourceManager.getResource(rl);
        };
    }

    protected void _apply() {
        for (int i = 0; i < 8; ++i) {
            setSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
        }
        if (MODEL_VIEW_MATRIX != null) {
            MODEL_VIEW_MATRIX.set(RenderSystem.getExtendedModelViewMatrix());
        }
        if (PROJECTION_MATRIX != null) {
            PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
        }
        if (INVERSE_VIEW_ROTATION_MATRIX != null) {
            INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if (TEXTURE_MATRIX != null) {
            TEXTURE_MATRIX.set(RenderSystem.getExtendedTextureMatrix());
        }
        if (COLOR_MODULATOR != null) {
            COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if (FOG_START != null) {
            FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if (FOG_END != null) {
            FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if (FOG_COLOR != null) {
            FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if (FOG_SHAPE != null) {
            FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        if (GAME_TIME != null) {
            GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        if (SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            SCREEN_SIZE.set((float) window.getWidth(), (float) window.getHeight());
        }
        if (LINE_WIDTH != null) {
            LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(this);
        // Custom
        if (INVERSE_NORMAL_MATRIX != null) {
            INVERSE_NORMAL_MATRIX.set(RenderSystem.getExtendedNormalMatrix());
        }
        if (LIGHT_MODULATOR != null) {
            int light = RenderSystem.getShaderLight();
            LIGHT_MODULATOR.set(light & 0xffff, (light >> 16) & 0xffff);
        }
    }

    protected void _clear() {
    }

    @Override
    public void apply() {
        _apply();
        super.apply();
    }

    @Override
    public void clear() {
        _clear();
        super.clear();
    }
}
