package moe.plushie.armourers_workshop.compat.client.renderer.vertex;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IVertexElement;
import org.lwjgl.opengl.GL20;

@Available("[1.21, 1.22)")
public abstract class AbstractVertexElementImpl implements IVertexElement {

    @Override
    public void setupBufferState(int index, int stride, long pointer) {
        GL20.glEnableVertexAttribArray(index);
        get().setupBufferState(index, pointer, stride);
    }

    @Override
    public void clearBufferState(int index) {
        GL20.glDisableVertexAttribArray(index);
    }

    @Override
    public int byteSize() {
        return get().byteSize();
    }
}
