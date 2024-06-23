package moe.plushie.armourers_workshop.core.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IRenderBufferObject;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public interface ShaderVertexObject {

    int getVertexOffset();

    int getVertexCount();

    IRenderBufferObject getVertexBuffer();

    int getLightmap();

    float getPolygonOffset();

    OpenPoseStack getPoseStack();

    VertexFormat getFormat();

    RenderType getType();

    boolean isGrowing();

    void release();
}
