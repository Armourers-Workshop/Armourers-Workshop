package moe.plushie.armourers_workshop.compat.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.core.client.shader.ShaderContext;
import moe.plushie.armourers_workshop.core.client.shader.ShaderRenderer;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL15;

@Available("[1.18, )")
public class AbstractShaderRenderer implements ShaderRenderer {

    private static final AbstractShaderRenderer INSTANCE = new AbstractShaderRenderer();

    public static AbstractShaderRenderer newInstance() {
        return INSTANCE;
    }

    @Override
    public void draw(ShaderVertexObject object, ShaderContext context) {
        drawElements(object.arrayObject(), object.indexObject(), object.vertexCount());
    }

    private void drawElements(VertexArrayObject arrayObject, @Nullable VertexIndexObject indexObject, int count) {
        arrayObject.bind();
        if (indexObject != null) {
            GL15.glDrawElements(GL15.GL_TRIANGLES, indexObject.stride(count), indexObject.type(), 0);
        } else {
            GL15.glDrawArrays(GL15.GL_TRIANGLES, 0, count);
        }
    }
}
