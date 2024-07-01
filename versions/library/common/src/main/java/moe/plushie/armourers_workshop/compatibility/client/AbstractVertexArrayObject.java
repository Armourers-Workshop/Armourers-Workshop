package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexBufferObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public class AbstractVertexArrayObject extends VertexArrayObject {

    public static VertexArrayObject create(VertexFormat format, long offset, VertexBufferObject bufferObject, VertexIndexObject indexObject) {
        var arrayObject = new VertexArrayObject();
        var state = new SkinRenderState();
        state.save();

        // in the newer version rendering system, we will use a shader.
        // and shader requires we to split the quad into two triangles,
        // so we need use index buffer to control size of the vertex data.
        arrayObject.bind();
        bufferObject.bind();
        indexObject.bind();

        // the vertex offset no longer supported in vanilla,
        // so we need a special version of the format setup.
        format.setupBufferState(offset);

        // unbind the VBO/VAO to prevent accidentally modify VAO.
        VertexArrayObject.unbind();
        VertexBufferObject.unbind();

        // because the setup state by each format maybe different,
        // so we need to clear state first.
        format.clearBufferState();

        state.load();
        return arrayObject;
    }

}
