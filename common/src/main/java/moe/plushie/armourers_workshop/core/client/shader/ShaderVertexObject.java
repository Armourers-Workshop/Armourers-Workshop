package moe.plushie.armourers_workshop.core.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IRenderBufferObject;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(value = EnvType.CLIENT)
public abstract class ShaderVertexObject {

    public abstract int getVertexOffset();

    public abstract int getVertexCount();

    public abstract IRenderBufferObject getVertexBuffer();

    public abstract int getLightmap();

    public abstract float getPolygonOffset();

    public abstract IPoseStack getPoseStack();

    public abstract VertexFormat getFormat();

    public abstract RenderType getType();

    public boolean isGrowing() {
        return getType() == SkinRenderType.FACE_LIGHTING || getType() == SkinRenderType.FACE_LIGHTING_TRANSLUCENT;
    }
}
