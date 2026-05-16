package moe.plushie.armourers_workshop.compat.client.platform;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;

@OnlyIn(Dist.CLIENT)
public interface AbstractRenderBuffer {

    IMeshData slice(int offset, int vertexCount, IVertexFormat format);

    void retain();

    void release();
}
