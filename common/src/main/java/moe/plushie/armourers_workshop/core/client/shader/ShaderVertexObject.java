package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexBufferObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ShaderVertexObject {

    int getOffset();

    int getTotal();

    VertexArrayObject getArrayObject();

    VertexBufferObject getBufferObject();

    VertexIndexObject getIndexObject();

    int getOverlay();

    int getLightmap();

    int getOutlineColor();

    float getPolygonOffset();

    OpenPoseStack getPoseStack();

    IVertexFormat getFormat();

    IRenderType getType();

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    void release();
}
