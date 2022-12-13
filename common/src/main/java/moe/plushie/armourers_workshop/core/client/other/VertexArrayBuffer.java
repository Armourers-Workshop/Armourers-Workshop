package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class VertexArrayBuffer {

    private int name;

    public VertexArrayBuffer() {
        RenderSystem.glGenVertexArrays(i -> name = i);
    }

    public void bind() {
        RenderSystem.glBindVertexArray(() -> name);
    }

    public void unbind() {
        RenderSystem.glBindVertexArray(() -> 0);
    }
}
