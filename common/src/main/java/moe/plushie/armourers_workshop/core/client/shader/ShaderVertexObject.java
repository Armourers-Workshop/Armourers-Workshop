package moe.plushie.armourers_workshop.core.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IRenderBufferObject;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public abstract class ShaderVertexObject {

    public abstract int getVertexOffset();

    public abstract int getVertexCount();

    public abstract IRenderBufferObject getVertexBuffer();

    public abstract int getLightmap();

    public abstract float getPolygonOffset();

    public abstract OpenPoseStack getPoseStack();

    public abstract VertexFormat getFormat();

    public abstract RenderType getType();

    public abstract boolean isGrowing();
}
