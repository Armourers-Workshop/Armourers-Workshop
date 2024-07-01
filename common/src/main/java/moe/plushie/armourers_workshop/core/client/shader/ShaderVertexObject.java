package moe.plushie.armourers_workshop.core.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexBufferObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public interface ShaderVertexObject {

    int getOffset();

    int getTotal();

    VertexArrayObject getArrayObject();

    VertexBufferObject getBufferObject();

    VertexIndexObject getIndexObject();

    int getOverlay();

    int getLightmap();

    float getPolygonOffset();

    OpenPoseStack getPoseStack();

    VertexFormat getFormat();

    RenderType getType();

    boolean isGrowing();

    void release();
}
