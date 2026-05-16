package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import org.lwjgl.opengl.GL20;

@Available("[21, 26)")
public class AbstractGLVertexBufferImpl {

    public static void setupBufferState(IVertexFormat format, long address) {
        var stride = format.byteSize();
        var elements = format.get().getElements();
        for (int index = 0; index < elements.size(); ++index) {
            var element = elements.get(index);
            GL20.glEnableVertexAttribArray(index);
            element.setupBufferState(index, address, stride);
            address += element.byteSize();
        }
    }

    public static void clearBufferState(IVertexFormat format) {
        var elements = format.get().getElements();
        for (int index = 0; index < elements.size(); ++index) {
            GL20.glDisableVertexAttribArray(index);
        }
    }
}
